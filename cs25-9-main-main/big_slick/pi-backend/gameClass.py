import random as rnd            # used for random actions, temporary
import itertools as it          # for combinations
import threading                # for server multithreading
from typing import List         # to define types in functions
from collections import Counter # to count occurrences of ranks for hand strength evaluation
from multiprocessing.managers import BaseManager # to communicate with alter ego on the front-end
import gameConnection as gc                 # more front-end communication


# returns True if hand is a flush
def flush(hand : List["Card"]):
    return all(hand[0].same_suit(c) for c in hand)

# returns True if hand is a straight
def straight(hand : List["Card"]):
    hand.sort(reverse=True)
    return all(hand[i].value - 1 == hand[i + 1].value for i in range(len(hand)-1))

# returns True if hand is a straight flush
def straight_flush(hand :List["Card"]):
    return flush(hand) & straight(hand)

# returns an integer from 1-9 as specified inside the function corresponding to the hand rank of the argument
def hand_rank(hand : List["Card"]):
    # Category ranking:
        # 9: Straight Flush
        # 8: Four of a Kind
        # 7: Full House
        # 6: Flush
        # 5: Straight
        # 4: Three of a Kind
        # 3: Two Pair
        # 2: One Pair
        # 1: High Card
    # count the number of occurrences of ranks in hand
    rank_counts = Counter(card.rank for card in hand)
    # take count values and sort from high to low to get pattern
    rank_value_counts = sorted(rank_counts.values(), reverse=True)
    # sort hand, specifically prioritizing how often the card occurs before its absolute rank: ex: [4,4,4,4,A].
    # this is done so that > and < can be used on two hands and return the stronger one, taking into account -
    # the hand combination first, and then the high card.
    sorted_hand = sorted(hand,  key = lambda c: (-rank_counts[c.rank], -c.value))
    # check for each hand rank if they appear in the hand
    if straight_flush(hand):
        rank = 9
    elif rank_value_counts == [4, 1]:
        rank = 8
    elif rank_value_counts == [3, 2]:
        rank = 7
    elif flush(hand):
        rank = 6
    elif straight(hand):
        rank = 5
    elif rank_value_counts == [3, 1, 1]:
        rank = 4
    elif rank_value_counts == [2, 2, 1]:
        rank = 3
    elif rank_value_counts == [2, 1, 1, 1]:
        rank = 2
    else:
        rank = 1
    # return the hand and special sorted hand
    return rank, sorted_hand


class GameClass:
    # TODO: Verify whether all variables are used and named properly
    def __init__(self, username : str, chips : int, debug : bool = False):
        # debug to print most game states to keep track of actions
        self.debug = debug
        # gameID passed to ID the game
        self.gameID = rnd.randrange(1, 10000)
        # to emulate the shuffler and dealer mechanism, replaced later with card detection
        self.deck = Deck()
        self.deck.shuffle()

        # initialise the table class variable, that will hold the card logic during the game
        self.table = None # to be initialized when round begins

        # fixed player-count and waitlist size, for players wanting to join the table when its full
        self.playerCount = 6
        self.waitlistSize = 10


        self.hostUsr = username                                 # username of player who created the game (host)
        self.playerWallet = [0] * self.playerCount              # stores player chip count
        self.playerWallet[0] = chips                            # host chip count at position 0
        self.playerUsernames = [''] * self.playerCount          # stores player ids
        self.playerUsernames[0] = self.hostUsr                  # host username is seated in position 0
        self.has_folded = None                                  # to track folding in game

        self.hand_strength = None                               # to compare hand strength of player later in game
        self.best_hand = None                                   # to store the best 5 card combination a player can make

        self.roundOngoing = False                               # to avoid players sitting at the table mid round + other out of turn events
        self.smallBlindId = 0                                   # id of small blind player in self.playerUsernames
        self.bigBlindId = 0                                     # id of big blind player in self.playerUsernames
        self.smallBlindAmount = 5                               # value of small blind (BB = 2 * SB)
        self.waitlist = [''] * self.waitlistSize                # stores players to join after the round is over
        self.waitlistWallet = [0] * self.waitlistSize           # store wallets of players wanting to join

        self.pot_contributions = [0] * self.playerCount         # each players' pot contribution to split pot in case of all in
        self.pot = 0                                            # store total table pot

        # communication with frontend, define QueueManager and register queues used to pass information to frontend.
        class QueueManager(BaseManager): pass
        QueueManager.register('get_to_pass_join')
        QueueManager.register('get_to_pass_turn')
        QueueManager.register('get_to_pass_left')
        QueueManager.register('get_to_pass_create')
        QueueManager.register('get_to_return_join')
        QueueManager.register('get_to_return_move')
        QueueManager.register('get_to_return_left')
        QueueManager.register('get_to_return_create')
        # connect to address with authkey
        manager = QueueManager(address=('127.0.0.1', 50000), authkey=b'secret')
        manager.connect()
        # define functions for communication
        self.pass_join = manager.get_to_pass_join()
        self.pass_turn = manager.get_to_pass_turn()
        self.return_join = manager.get_to_return_join()
        self.return_move = manager.get_to_return_move()

        # async join/leave logic to have listener active during the game
        self.join_Thread = threading.Thread(target=self.join_routine)
        self.join_Thread.start()
        self.leave_Thread = threading.Thread(target=self.leave_routine)
        self.leave_Thread.start()
        self.game_start = False
        self.start_game()




    # async join routine
    def join_routine(self):
        while True:
            (username, wallet) = self.pass_join.get()       # gets username and wallet from frontend
            r = self.add_player(username, wallet)           # tries to add player and return status
            self.return_join.put(r)
            a = []
            listZero = []
            listFolded = []
            for p in range(len(self.playerUsernames)):
                listZero.append(0)
                listFolded.append(False)
            gc.post_game(self.gameID, [list(n) for n in list(zip(self.playerUsernames, listZero, listFolded))],self.hostUsr, self.hostUsr, self.pot)

    def start_game(self):
        while True:
            if not self.game_start:
                returnMessage = self.return_move.get()
                if (returnMessage is not None):
                    self.game_start = True
                    self.pass_turn.put("This worked apparently")
                    listZero = []
                    listFolded = []
                    for p in range(len(self.playerUsernames)):
                        listZero.append(0)
                        listFolded.append(False)
                    gc.post_game(self.gameID, [list(n) for n in list(zip(self.playerUsernames,listZero,listFolded))], self.hostUsr, self.hostUsr, self.pot)
                    break

    # TODO: add the appropriate queue for leaving players and return wallet + comments
    def leave_routine(self):
        while True:
            username = self.pass_join.get()
            wallet = self.remove_player(username, username)
            if wallet >= 0:
                self.return_join.put((username, wallet))
            else:
                self.return_join.put(-1)

    # Returns a list of any active players in the round
    def active_players(self):
        active_players = [
            i for i in range(self.playerCount) # for each player check whether they have folded or have no money left
            if not self.has_folded[i] and self.playerWallet[i] > 0
        ] # the other players are still in the game, and their index in self.playerUsernames is returned
        return active_players

    # TODO: Add the machine code for the physical dealer and image detection, and remove the simulated deal
    def deal_players(self):
        if self.roundOngoing: # cannot deal players in if round is ongoing
            return -1
        # make any empty seat or player with empty wallet fold to filter them out
        self.has_folded = [True if self.playerUsernames[i] == "" or self.playerWallet == 0 else False for i in range(len(self.playerUsernames))]
        # initialise table for card logic
        self.table = Table(self.playerUsernames, self.debug)
        self.roundOngoing = True # set the round to be ongoing
        # define active players twice to deal two cards to each
        active_player_ids = self.active_players() + self.active_players()

        for idx in active_player_ids:
            #TODO: add the robotics for controlling the servos and tesseract? for card detection
            card = self.deck.deal(1)[0] #TODO: remove placeholder and replace with card detection
            self.table.assign_card(card, self.playerUsernames[idx]) # assign the card dealt to the table
        return 0

    # TODO: implement dealing machine and card recognition
    def deal_community(self, n : int):
        for i in range(n):
            card = self.deck.deal(1)[0]  # TODO: replace with card detection
            self.table.assign_community(card) # assign community card to table
        return 0

    # TODO: Test backend communication, write test cases, needs comments
    def bet_round(self, pre_flop : bool = False):
        # initialises bet list for the round
        bets = [0] * self.playerCount
        # gets list of players active in the round
        active_player_ids = self.active_players()
        # last raise is used to determine when the round is over
        last_raise = -1
        if pre_flop: # pre-flop needs blind assignment
            # search for first user ID that belongs to the active players
            while self.smallBlindId not in active_player_ids:
                self.smallBlindId = (self.smallBlindId + 1) % self.playerCount
            # make sure the small blind cannot be above the players' wallet
            sb_amount = min(self.smallBlindAmount, self.playerWallet[self.smallBlindId])
            # add the amount to the players' bet, pot contribution, pot, and remove the amount from their wallet
            bets[self.smallBlindId] += sb_amount
            self.pot_contributions[self.smallBlindId] += sb_amount
            self.pot += sb_amount
            self.playerWallet[self.smallBlindId] -= sb_amount


            # set the ID of the small blind in self.active player IDs
            sb_active_id = active_player_ids.index(self.smallBlindId)
            # find index of big blind = next player in active players
            self.bigBlindId = active_player_ids[(sb_active_id + 1) % len(active_player_ids)]
            # make sure the big blind cannot be above the players' wallet
            bb_amount = min(self.smallBlindAmount * 2, self.playerWallet[self.bigBlindId])
            # add the amount to the players' bet, pot contribution, pot, and remove the amount from their wallet
            bets[self.bigBlindId] += bb_amount
            self.pot_contributions[self.bigBlindId] += bb_amount
            self.pot += bb_amount
            self.playerWallet[self.bigBlindId] -= bb_amount
            # next player in line to bet is next in line in active_player_ids
            next_player_id = active_player_ids[(sb_active_id + 2) % len(active_player_ids)]
            next_pos = active_player_ids.index(next_player_id)
            # the active_player_ids is rotated so the current player needing to perform an action is at position 0
            active_player_ids = active_player_ids[next_pos:] + active_player_ids[:next_pos]

        else:
            # search for next player after small blind if they happen to not be active in game (folded/all in)
            sb_id = self.smallBlindId
            while sb_id not in active_player_ids:
                sb_id = (sb_id + 1) % self.playerCount
            # rotate the active player list to get this player in position 0 and begin betting round
            next_pos = active_player_ids.index(sb_id)
            active_player_ids = active_player_ids[next_pos:] + active_player_ids[:next_pos]

        # this first iteration is to initialise the last raise ID properly
        first_iteration = True
        # check if only one player is left or its the turn of the last player who raised (= everyone fold/match bet)
        while last_raise != active_player_ids[0] and not len(self.active_players()) == 1:
            if first_iteration:
                last_raise = active_player_ids[0] # set the first ID to the starting player id
                first_iteration = False
            # define idx of current player to make code more readable
            idx = active_player_ids[0]
            # TODO: Write test cases to check functionning of server communication, once it finally works
            # ask frontend for player action based on current game state
            gc.post_game(self.gameID, [list(n) for n in list(zip(self.playerUsernames,bets,self.has_folded))], self.hostUsr, self.playerUsernames[idx], self.pot)
            # wait for reply
            action = self.return_move.get()
            # check if reply is in the correct range
            if action not in (0,1,2):
                self.pass_turn.put()
            else:
                self.pass_turn.put(0)
                throw_error("Abnormal action received, exiting...")
            # this is to simulate random player action for testing
            # action = rnd.randint(0, 2) # 0 = fold, 1 = call/check, 2 = raise
            # max bet to calculate how big the call is
            max_bet = max(bets)
            runBefore = False
            # fold logic
            if action == 0:
                if self.debug: print(self.playerUsernames[idx] + " folds")          # debug messages
                self.has_folded[idx] = True
                active_player_ids.pop(0) # pop from active players if folded to keep loop going

            elif action == 1: # check / call function
                # make sure amount does not exceed player wallet
                amount = min(self.playerWallet[idx], max_bet - bets[idx])
                if amount == self.playerWallet[idx]:
                    active_player_ids.pop(0) # if all in pop from active players
                    if self.debug: print(self.playerUsernames[idx] + " calls all-in with "+ str(amount + bets[idx]))# debug messages
                else:
                    if self.debug: print(self.playerUsernames[idx] + " calls " + str(amount + bets[idx]))           # debug messages
                # add the amount to the players' bet, pot contribution, pot, and remove the amount from their wallet
                bets[idx] += amount
                self.pot_contributions[idx] += amount
                self.pot += amount
                self.playerWallet[idx] -= amount
            elif action == 2:
                # make sure amount does not exceed player wallet
                if max_bet - bets[idx] > self.playerWallet[idx]:
                    amount = self.playerWallet[idx]
                    if self.debug: print(self.playerUsernames[idx] + " calls (raise) all-in with " + str(amount + bets[idx]))# debug messages
                    active_player_ids.pop(0)
                else:
                    amount = rnd.randrange(max_bet - bets[idx], self.playerWallet[idx])
                    if amount == self.playerWallet[idx]:
                        if self.debug: print(self.playerUsernames[idx] + " raises all-in with " + str(amount + bets[idx]))   # debug messages
                        active_player_ids.pop(0)
                    else:
                        if self.debug: print(
                            self.playerUsernames[idx] + " raises with " + str(amount + bets[idx]))# debug messages
                    last_raise = idx
                # add the amount to the players' bet, pot contribution, pot, and remove the amount from their wallet
                bets[idx] += amount
                self.pot_contributions[idx] += amount
                self.pot += amount
                self.playerWallet[idx] -= amount
            else:
                self.pass_turn.put("Invalid")
                runBefore = True
            if not runBefore :
                self.pass_turn.put("complete")
            # debug messages
            if self.debug:
                print(self.playerUsernames)
                print("Player wallets: " + str(self.playerWallet))
                print("Contributions: ", str(self.pot_contributions))
                print("Bets: " + str(bets))
                print(last_raise, idx)
            # if the current player folded or went all in, there's no need for active player
            # list rotation since next in line is already at index 0
            if idx == active_player_ids[0]:
                active_player_ids = active_player_ids[1:] + active_player_ids[:1]
            pass

        pass
        # when conditions are satisfied continue
        if self.debug: print("Done betting")

    # TODO: Add test cases and communication with database and frontend
    # calculates how the pot should be distributed
    def distribute_pot(self):
        # initialise hand strength list, best hand list
        self.hand_strength = [0] * self.playerCount
        self.best_hand = [[] * 5 for _ in range(self.playerCount)]
        # call the table function to get hand strength and best hand for each player
        self.hand_strength, self.best_hand = self.table.river_hand_strength()

        # to rank people by usernames and store hand strength and hand in same order, from best to worst
        username_rank = []
        hand_strength = []
        hands = []

        # count how many unique hand strengths there are
        unique_count = sorted(set(self.hand_strength), reverse=True)
        # loop over counts
        for count in unique_count:
            # indices of players with the current count
            indices = [i for i, x in enumerate(self.hand_strength) if x == count]
            # associate players with their hands
            pairs = [(self.best_hand[i], self.playerUsernames[i]) for i in indices]
            # store them in best hand and usernames, sorting by the pairs
            best_hand, usernames = zip(*sorted(pairs, reverse=True))
            # remove any players that have folded
            for username in usernames:
                idx = self.playerUsernames.index(username)
                if self.has_folded[idx]: continue
                username_rank.append(self.playerUsernames[idx])
                hands.append(self.best_hand[idx])
                hand_strength.append(self.hand_strength[idx])
        # store list in results
        results = list(zip(hand_strength, hands, username_rank))
        # print if debug
        if self.debug: print("Results =====")
        if self.debug: print(results)
        # calculate groups to find out ties
        groups = []
        # sort by hand strength, then by hands
        for key, group in it.groupby(results, key=lambda x: (x[0], x[1])):
            groups.append(list(group))
        if self.debug: print("Ranking =====")
        # possible to print if debug
        for group in groups:
            if len(group) > 1:
                if self.debug: print("Tie between:", [g[2] for g in group])
            else:
                if self.debug: print("-", group[0][2])

        if self.debug: print("Distribution =====")
        # make copy of contributions since it will be modified
        contributions = self.pot_contributions.copy()
        # initialise a check to verify all money has been distributed
        check = 0
        # loop until every value in contributions is 0
        while any(c > 0 for c in contributions):
            # take all contributions > 0
            active = [i for i, c in enumerate(contributions) if c > 0]
            # determine which is the smallest
            smallest = min(contributions[i] for i in active)
            # determine which players are eligible for this pot (if they did not fold)
            eligible = [i for i in active if not self.has_folded[i]]
            # the pot amount is the smallest amount times the number of active players
            amount = smallest * len(active)
            # then loop through winners until you find someone eligible to win the pot (or a tie)
            winners = []
            for group in groups:
                for hand in group:
                    if hand[2] in [self.playerUsernames[i] for i in eligible]:
                        winners.append(hand[2]) # TODO: Maybe change to index all the way through to remove the .index later
                # if winners are found break
                if winners:
                    break
            # find the indices of winners
            idx = [self.playerUsernames.index(winner) for winner in winners]
            for i in idx: # distribute to each winner a share (if 1 winner, full amount)
                self.playerWallet[i] += amount/len(winners)
                # check for debugging
                check += amount/len(winners)
            # subtract the smallest value from all the contributions, then continue until all contributions are 0
            for i in active:
                contributions[i] -= smallest
            # check sum of contributions during this process and the check if debug
            if self.debug: print(sum(self.pot_contributions), check)


    # TODO: Add frontend communication to pass game state
    def game_reset(self):
        # reset the game variables
        self.table = None
        self.roundOngoing = False
        self.has_folded = None
        self.smallBlindId = self.bigBlindId
        self.bigBlindId = 0
        self.pot_contributions = [0] * self.playerCount
        self.pot = 0
        # to simulate dealing cards
        self.deck = Deck()
        self.deck.shuffle()
        # deal with players in the waitlist
        while self.waitlist[0] != '':
            try:
                # if an empty seat is found at the table, the first player in waitlist is filled in (FIFO)
                index = self.playerUsernames.index('')
                self.playerUsernames[index] = self.waitlist[0]
                self.playerWallet[index] = self.waitlistWallet[0]
                self.waitlist[0] = ''
                self.waitlistWallet[0] = 0
                # rotate to new value in waitlist
                self.waitlist = self.waitlist[1:] + [self.waitlist[0]]
                self.waitlistWallet = self.waitlistWallet[1:] + [self.waitlistWallet[0]]

                # send updated table list to backend
            except ValueError:
                return 0 # table is full
        return 1         # waitlist is empty

    # adds player to the table
    def add_player(self, username :str, chips:int):
        print(self.playerUsernames)
        try:
            print("Adding player", username, " ", chips)
            # try to find empty postion
            index = self.playerUsernames.index('')
            # if no round is ongoing, add to table
            if not self.roundOngoing:
                self.playerUsernames[index] = username
                self.playerWallet[index] = chips
                return 0
            else:
                # otherwise, try to add to waitlist
                return self.add_player_to_waitlist(username, chips)
        except ValueError:
            # if no space in table, try to add to waitlist

            return self.add_player_to_waitlist(username, chips)


    def add_player_to_waitlist(self, username, chips):
        print(self.waitlist)
        try:
            # try to find empty spot in waitlist, and add player to it if there is
            index_waitlist = self.waitlist.index('')
            self.waitlist[index_waitlist] = username
            self.waitlistWallet[index_waitlist] = chips
            # send confirmation and waitlist position
            return 1
        except ValueError:
            return -1 # return -1 if waitlist is full

    # TODO: Add host rotate if current one leaves (maybe from waitlist too in future)
    # removes player from table
    def remove_player(self, username : str, username_request):
        try:
            # try to find player in table
            index = self.playerUsernames.index(username)
            # remove if requesting player is themselves or host
            if username == username_request or username_request == self.hostUsr:
                self.playerUsernames[index] = ''
                player_wallet = self.playerWallet[index]  # send the final wallet to the backend
                self.playerWallet[index] = 0
                return player_wallet
            else:
                return -1 # not allowed to remove from table
        except ValueError:
            return -1 # player is not at table

    # TODO: To be implemented
    def transfer_host(self, username : str):
        pass

    # TODO: Not up to date / necessary
    def debug_state(self):
        print(self.playerUsernames, self.playerWallet)
        print(self.table.community)
        print(self.table.hands)

class Table:

    def __init__(self, player_username_list : list, debug = False):
        self.debug = debug
        # make a copy of players at seats
        self.players = player_username_list
        # define their hands
        self.hands = [[None, None] for _ in range(len(player_username_list))]
        # define array for community cards
        self.community = [None] * 5

        # hand strength and best hand to return
        self.hand_strength = [0] * len(player_username_list)
        self.best_hand = [[] * 5 for _ in range(len(player_username_list))]

    # assign card to players' hand
    def assign_card(self, card : "Card", username):
        try:
            # try to find user in username
            idx_usr = self.players.index(username)
            try:
                # try to add card to hand
                idx_hand = self.hands[idx_usr].index(None)
                self.hands[idx_usr][idx_hand] = card
                return 0 # card has been added
            except ValueError:
                return -1 # hand full
        except ValueError:
            return None   # user doesnt exist

    # assign card(s) to community cards
    def assign_community(self, card : "Card"):
        try:
            # try to find space in community cards and assign card to it
            index = self.community.index(None)
            self.community[index] = card
            return  0 # successfully added card to community
        except ValueError:
             return -1 # no more space for community cards

    # reset hands and community cards
    def reset_cards(self):
        self.hands = [[None, None] for _ in range(len(self.players))]
        self.community = [None] * 5

    # calculate hand strength on river
    def river_hand_strength(self):
        if len(self.community) != 5:  return 1 # if it's not the river exit, someone probably won alreaady
        # loop through all players
        for player_id in range(len(self.players)):
            # if one or both of their assigned cards is None, then those are empty seats
            if self.hands[player_id][0] is None or self.hands[player_id][1] is None:
                self.hand_strength[player_id] = 0 # set hand strength to 0
                self.best_hand[player_id] = [None] * 5 # and none best hand
                continue
            # make list of all cards held by the player (7)
            cards = self.community + self.hands[player_id]
            # loop through all combinations of 5 cards
            for combination in it.combinations(cards, 5):
                # tuple to list
                combination = list(combination)
                # call hand rank to get evalutation and sorted hand for comparaison
                (evaluation, sorted_hand) = hand_rank(combination)

                # if the evaluation is better than the previous hand strength, update best hand
                if self.hand_strength[player_id] < evaluation:
                    self.hand_strength[player_id] = evaluation
                    self.best_hand[player_id] = sorted_hand
                # if the rank is better than the previous hand rank, with equal evaluation, update best hand
                else:
                    if sorted_hand > self.best_hand[player_id] and self.hand_strength[player_id] == evaluation:
                        self.hand_strength[player_id] = evaluation
                        self.best_hand[player_id] = sorted_hand
        # for debug
        if self.debug:
            print(self.players)
            print(self.hand_strength)
            print(self.best_hand)

        # returns best hand for each player
        return self.hand_strength, self.best_hand

# card class to facilitate hand rank comparaison and strenght calculations
class Card:
    # define ranks, suits, and how to convert from ranks (strings) to values 2-14
    RANKS = ['2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K', 'A']
    SUITS = ['Hearts', 'Diamonds', 'Clubs', 'Spades']
    RANK_VALUES = {r: i + 2 for i, r in enumerate(RANKS)}  # "2" -> 2, "3" -> 3, ..., "J" -> 11, "Q" -> 12, "K" -> 13, "A" -> 14
    VALUE_TO_RANK = {v: r for r, v in RANK_VALUES.items()} # the opposite assignment from RANK_VALUES

    # define rank and suit on initialisation + value
    def __init__(self, rank, suit):
        if rank not in Card.RANKS:
            raise ValueError(f"Invalid rank: {rank}")
        if suit not in Card.SUITS:
            raise ValueError(f"Invalid suit: {suit}")
        self.rank = rank
        self.suit = suit
        self.value = Card.RANK_VALUES[rank]

    # define readable format for printing during debugging and testing
    def __repr__(self):
        return f"{self.rank} of {self.suit}" # ex: "2 of Q"

    # define custom logic behavior to facilitate comparison
    def __eq__(self, other):
        return isinstance(other, Card) and self.rank == other.rank
    def __lt__(self, other):
        return self.value < other.value

    # define a function to compare suits easily
    def same_suit(self, other : "Card"):
        return self.suit == other.suit

# temporary for testing, emulating the raspberry pi machine dealing to players
class Deck:
    def __init__(self):
        self.cards = [Card(rank, suit) for suit in Card.SUITS for rank in Card.RANKS]

    def shuffle(self):
        rnd.shuffle(self.cards)

    def deal(self, n=1):
        if n > len(self.cards):
            raise ValueError("Not enough cards left in the deck!")
        dealt = self.cards[:n]
        self.cards = self.cards[n:]
        return dealt
