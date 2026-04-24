from multiprocessing.managers import BaseManager
import threading
import queue
from flask import Flask, request
from gameClass import *
import time


app = Flask(__name__) #create new flash application using this file as main

class Server:
    def __init__(self, manager):
        self.server = manager.get_server()

    def run(self):
        print("Starting server on port 8080-------")
        try:
            self.server.serve_forever()
        except KeyboardInterrupt:
            pass
        finally:
            self.server.shutdown()

pass_join_queue = queue.Queue()
pass_turn_queue = queue.Queue()
pass_left_queue = queue.Queue()
pass_create_queue = queue.Queue()
return_join_queue = queue.Queue()
return_move_queue = queue.Queue()
return_left_queue = queue.Queue()
return_create_queue = queue.Queue()

class QueueManager(BaseManager): pass
QueueManager.register('get_to_pass_join', callable=lambda: pass_join_queue)
QueueManager.register('get_to_pass_turn', callable=lambda: pass_turn_queue)
QueueManager.register('get_to_pass_left', callable=lambda: pass_left_queue)
QueueManager.register('get_to_pass_create', callable=lambda: pass_create_queue)
QueueManager.register('get_to_return_join', callable=lambda: return_join_queue)
QueueManager.register('get_to_return_move', callable=lambda: return_move_queue)
QueueManager.register('get_to_return_left', callable=lambda: return_left_queue)
QueueManager.register('get_to_return_create', callable=lambda: return_create_queue)

manager = QueueManager(address=('127.0.0.1', 50000), authkey=b'secret')

#receives a move of a person under the address /move
@app.route('/move', methods=['POST'])
def receive_move():
    print("I received a move")
    data = request.get_json() #gets a json from the request
    typeOfMove = data["typeOfMove"]
    username = None
    amountBet = None
    if (typeOfMove!="start"):
        username = data["username"]
        amountBet = data["amountBet"]
        # return move to game logic
        if (typeOfMove == "fold"):
            typeOfMove = 0
        elif (typeOfMove == "check"):
            typeOfMove = 1
        elif (typeOfMove == "raise"):
            typeOfMove = 2
    elif (typeOfMove == "start"):
        typeOfMove = 3
    else:
        print("Not allowed move")
        return "Error, not allowed moved", 500
    if (isinstance(typeOfMove, int)):
        if typeOfMove != 3:
            return_move_queue.put((username, typeOfMove, amountBet))
        elif typeOfMove == 3:
            return_move_queue.put("hello")
        else:
            return "Error: invalid credentials", 400
        try:
            pass_turn_queue.get()
            if pass_turn_queue == "Invalid":
                print("Invalid moved")
                return "Error: invalid move", 400
            else:
                print("Move received")
                return "Move received", 200
        except Exception as e:
            print(e, "HIIIIIIIIII")
            return "Error, move got no response", 501
    else :
        print("Wrong move")
        return "Wrong move", 501


#receives a person that joined the game under the address /join
@app.route('/join', methods=['POST'])
def receive_join():
    print("I received a join")
    data = request.get_json()
    if data["username"] is None:
        return "No username given", 400
    username = data["username"]
    try:
        wallet = int(data["amount_bet"])
    except Exception as e:
        wallet = 0
        print(e, "UR A DUMMY, PASS AN INTEGER FOR WALLET")
    pass_join_queue.put((username, 400))
    try:
        ans = return_join_queue.get(timeout=3)
        if ans == 0:
            print("User joined table")
            return "User joined table", 200
        elif ans == 1:
            print("User joined playlist")
            return "User joined playlist", 200
        else:
            print("No space on waitlist")
            return "No space in waitlist/table", 400
    except Exception as e:
        print(e)
        print("Error 500")
        return "Error, join got no response", 501

@app.route('/leave',  methods=['POST'])
def receive_leave():
    print("I received a leave")
    data = request.get_json()
    personLeaving = data["personLeaving"]
    personWhoLeftThem = data["personWhoLeftThem"]
    pass_left_queue.put((personLeaving, personWhoLeftThem))
    try:
        ans = return_left_queue.get(timeout=3)
        if ans == 0:
            return "User left table", 200
        else:
            return "You are not allowed to kick that player", 400
    except Exception as e:
        print(e)
        return "Error, receive got no response", 500

@app.route('/create', methods=['POST'])
def receive_create():
    print("I received a create")
    data = request.get_json()
    if data["username"] is None:
        return "No username given", 400
    username = data["username"]
    smallBlindSize = data["smallBlindSize"]
    maxBuyIn = data["maxBuyIn"]
    start = threading.Thread(target=startgame, args=(username, smallBlindSize, maxBuyIn))
    print("Game started")
    start.start()
    return "Game created", 200
    try:
        ans = return_create_queue.get(timeout=3)
        if ans == 0:
            print("Good")
            return "Game created", 200
        else:
            print("Bad")
            return "No more game space left", 500
    except Exception as e:
        print(e)
        return "Error, create got no response", 500

def startgame(username, smallBlindSize, maxBuyIn):
    hostUsr=username
    game = GameClass(hostUsr, maxBuyIn, False)
    if (smallBlindSize!=None):
        game.smallBlindAmount = smallBlindSize
    print("Giving cards")
    print(game.waitlist)
    print(game.playerUsernames)
    while (game.game_start is False):
        time.sleep(0.3)
    game.deal_players()
    print("Bet")
    game.bet_round(True)  # pre-flop bet

    print("Deal")
    game.deal_community(5)
    print("Bet2")
    game.bet_round()
    print("Pot")
    game.distribute_pot()
    print("Reset")
    game.game_reset()


#Flask server only starts if you run file
if __name__ == '__main__':
    from waitress import serve
    # Connect to the Managing server
    manager = QueueManager(address=('127.0.0.1', 50000), authkey=b'secret')
    server = Server(manager)
    server_thread = threading.Thread(None, server.run).start()
    serve(app, host='0.0.0.0', port=8080)