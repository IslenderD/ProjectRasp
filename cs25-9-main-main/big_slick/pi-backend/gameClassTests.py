from random import randint
from gameClass import *

hostUsr = "host-player"

print("test1")
game = GameClass(hostUsr, 2000, False)
print("test2")
game.add_player("Mert", 2000)
game.add_player("Dani", 2000)
game.add_player("Rick", 2000)
game.add_player("Boann", 2000)
game.add_player("Oeds", 2000)

# TODO: Make the actual game loop and implement raspberry pi hardware controls

# add big and small blind bets
for _ in range(1000):
    game.deal_players()

    game.bet_round(True)  # pre-flop bet

    game.deal_community(5)

    game.bet_round()

    game.distribute_pot()

    game.game_reset()

    print(game.playerWallet)
    if game.playerWallet.count(0) == len(game.playerWallet) - 1:
        game.remove_player("host-player", "host-player")
        game.remove_player("Mert", "Mert")
        game.remove_player("Dani", "Dani")
        game.remove_player("Rick", "Rick")
        game.remove_player("Boann", "Boann")
        game.remove_player("Oeds", "Oeds")
        game.add_player("host-player", 2000)
        game.add_player("Mert", 2000)
        game.add_player("Dani", 2000)
        game.add_player("Rick", 2000)
        game.add_player("Boann", 2000)
        game.add_player("Oeds", 2000)
pass

