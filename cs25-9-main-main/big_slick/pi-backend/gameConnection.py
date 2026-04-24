import time
import os
from typing import List

import requests

RETRY_DELAY_SECONDS = 0.5
BACKEND_BASE_URL = os.getenv("BACKEND_BASE_URL", "http://192.168.222.172:8081")

# Sends a post request to the java backend. This post sends the turn of a player. People contains username (string),
def post_game(id : int, people : List[List[object]], host : str, turn : str, pot : int):
    url = f"{BACKEND_BASE_URL}/backend/game/turn"
    peopleOnGame = []
    print("Post game")
    for person in people: #creates and adds a json to the list of json that contains all players on the game
        peopleJson = {"username":person[0], "amount_bet":person[1], "isFolded":person[2]}
        peopleOnGame.append(peopleJson)

    data = {"id":id, "peopleOnGame": peopleOnGame, "host":host, "turn":turn, "pot":pot, "isGame": True}
    retries = 20
    attempt = 0
    while attempt < retries:
        try:
            response = requests.post(url, json=data, timeout=5)
            response.raise_for_status()  # Raise exception for HTTP errors (4xx, 5xx)
            print("Request succeeded on attempt", attempt + 1)
            return response.text
        except requests.Timeout:
            # Timeout occurred; retry after delay
            attempt += 1
            print(f"Timeout occurred, retrying {attempt}/{retries} in {RETRY_DELAY_SECONDS}s...")
            time.sleep(RETRY_DELAY_SECONDS)
        except requests.RequestException as e:
            # Non-timeout errors
            print("Error sending data to Java backend:", e)
            return f"Error: {e}"

    # If all retries failed due to timeout
    return f"Error: Request timed out after {retries} attempts"

# Sends a post request to the java backend. This post sends the winner of the game.
def post_winner(username : str, amount_won: int):

    url = f"{BACKEND_BASE_URL}/backend/game/win"

    data = {"username": username, "amount_won":amount_won, "isGame": False}
    print("In post winner")
    try:
        response = requests.post(url, json=data, timeout=5)
        response.raise_for_status() #if anything above 400 error, cause a request error
        return response.text
    except requests.RequestException as e:
        print("Error sending data to Java backend:", e)
        return f"Error: {e}"