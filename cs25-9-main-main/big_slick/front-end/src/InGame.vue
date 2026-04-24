<script>
import GameBoard from './components/GameBoard.vue'
import { API_BASE_URL, WS_BASE_URL } from '@/config'

export default {
  data() {
    return {
      isMyTurn: false,
      showQR: false,
      turnChoice: null,
      raise: false,
      raiseAmount: 0,
      socket: null,
      logOutMenu: false, //for showing if the user menu must be shown
      gameKey: 0,
      gameStarted: false,

      /* Make these empty when connecting backend */
      name: '',
      chipAmount: 0,

      betAmount: 0,

      id: 1,
      peopleOnGame: [],
      playerBets: [0,0,0,0,0,0],
      host: '',
      turn: '',
      pot: 0,
      requiredCode: "B3QPA7",
    }
  },

  components: {
    GameBoard
  },

  async mounted() {
    await this.startSocket();
    await this.fetchWallet();
  },

  methods: {
    /*
    Sleep function that makes sure everything is properly loaded before a function is called
     */
    sleep(ms) {
      return new Promise(resolve => setTimeout(resolve, ms))
    },

    /*
      Function for fetching the wallet of the player
       */
    async fetchWallet() {
      console.log("in fetchWallet");
      await this.sleep(1000);

      this.name = this.$route.params.name[0];

      try {
        const response = await fetch(`${API_BASE_URL}/backend/move/` + this.name,
            {
              method: "GET",

            }
        );


        if (!response.ok) {
          if (response.status === 401) {
            this.showRegisterError = true;
          }
          else {
            //handle type 500 error
            throw new Error('Failed to fetch user data');
          }

        }

        else {
          const data = await response.json();
          console.log(data.amount);
          this.chipAmount = data.amount;
          //this.firstPlayer();
        }

      } catch(e) {
        console.log(e);
      }
    },

    firstPlayer() {
      let newPlayer = {"username": this.name, "amount_bet": 400, "isFolded": false};
      this.peopleOnGame.push(newPlayer);
    },

    /*
    Function for logging out of the server
     */
    async leaveGame() {
      event.preventDefault();

      let amountInGame = 0

      for (let i = 0; i < this.peopleOnGame.length; i++) {
        if (this.peopleOnGame[i].username === this.name) {
          amountInGame = this.peopleOnGame[i].amount_bet
        }
      }

      const requestLeaveGame = {
        personLeaving: this.name,
        personWhoLeftThem: this.name,
        amountWon: amountInGame
      };

      try {
        const response = await fetch(`${API_BASE_URL}/backend/move/leavePlayer`,
            {
              method: "DELETE",
              body: JSON.stringify(requestLeaveGame),
              headers: {
                "Content-Type": "application/json",
              }
            }
        );


        if (!response.ok) {
          if (response.status === 401) {
            this.showRegisterError = true;
          } else {
            //handle type 500 error
            throw new Error('Failed to fetch user data');
          }

        } else {
          this.$router.push('/')
        }

      } catch (e) {
        console.log(e);
      }

    },

    /*
    Function for logging out of the server
     */
    async logOut() {
      event.preventDefault();

      let amountInGame = 0

      for (let i = 0; i < this.peopleOnGame.length; i++) {
        if (this.peopleOnGame[i].username === this.name) {
          amountInGame = this.peopleOnGame[i].amount_bet
        }
      }

      const requestLogOut = {
        username: this.name,
        amountWon: amountInGame
      };

      try {
        const response = await fetch(`${API_BASE_URL}/backend/auth`,
            {
              method: "DELETE",
              body: JSON.stringify(requestLogOut),
              headers: {
                "Content-Type": "application/json",
              }
            }
        );


        if (!response.ok) {
          if (response.status === 401) {
            this.showRegisterError = true;
          } else {
            //handle type 500 error
            throw new Error('Failed to fetch user data');
          }

        } else {
          this.$router.push('/')
        }

      } catch (e) {
        console.log(e);
      }

    },

    /*
    Changes if you see the QR or not
     */
    changeQR() {
      this.showQR = !this.showQR;
    },

    /*
    Starts a socket and checks for updates
     */
    startSocket() { //TODO check if it correctly fills in all data
      this.socket = new WebSocket(`${WS_BASE_URL}/chat`);
      this.socket.onopen = function (e) {
        alert("[open] Connection established");
        console.log(e);
        this.gameKey += 1;
      };
      this.socket.onerror = function (e) {
        console.log("error" + e + "has occured")
      };
      this.socket.onclose = function () {
        console.log("connection is closed")
      };
      this.socket.onmessage = async function (e) {
        if (e.isGame === true) {
          alert("something happened")
          this.peopleOnGame = e.peopleOnGame;
          this.host = e.host;
          this.turn = e.turn;
          this.pot = e.pot;

          this.gameKey += 1;
          // Wait for the change to get flushed to the DOM
          await this.$nextTick();
          this.isStartTurn();
        } else {
          alert(e.username + "has won an received " + e.amount_won + "chips");
          this.gameStarted = false;
        }

      };
    },

    /*
    Checks if it is your turn
     */
    isStartTurn() {
      if (this.name === this.turn) {
        this.isMyTurn = true;
      } else {
        this.isMyTurn = false;
      }
    },

    /*
    If a button is clicked for choosing an option, this sends the right amount to the backend
     */
    async chooseOption() {
      var option = event.srcElement.value;
      console.log("Player has chosen", option);
      if (option === "raise") {
        this.raise = true;
      } else {
        event.preventDefault();

        let chooseAmount = 0;
        if (option === "fold") {
          chooseAmount = -1;
        }

        const sendOption = {
          username: this.name,
          typeOfMove: option,
          amount_bet: 0
        };

        try {
          const response = await fetch(`${API_BASE_URL}/backend/move`,
              {
                method: "POST",
                body: JSON.stringify(sendOption),
                headers: {
                  "Content-Type": "application/json",
                }
              }
          );


          if (!response.ok) {
            if (response.status === 401) {
              //do nothing
            } else {
              //handle type 500 error
              throw new Error('Failed to fetch user data');
            }

          } else {
            console.log("move sent succesfully")
            this.isMyTurn = false;
          }


        } catch (e) {
          console.log(e);
        }
      }
    },

    /*
    if raise is chosen, then the value gets raised and said value gets send to the backend
     */
    async chooseRaise() {
      var option = event.srcElement.value;
      let raise_amount = 0;
      if (option === "return") {
        this.raise = false;
      } else if (option === "min") {
        this.raiseAmount = this.betAmount;
        raise_amount = 1;
      } else if (option === "half") {
        this.raiseAmount = this.betAmount * 1.5;
        raise_amount = this.betAmount * 0.5;
      } else if (option === "full") {
        this.raiseAmount = this.betAmount * 2;
        raise_amount = this.betAmount;
      } else if (option === "all") {
        this.raiseAmount = this.chipAmount;
        raise_amount = this.chipAmount;
      }
      if (option !== "return") {
        event.preventDefault();

        const sendOption = {
          username: this.name,
          typeOfMove: "raise",
          amount_bet: raise_amount
        };

        try {
          const response = await fetch(`${API_BASE_URL}/backend/move`,
              {
                method: "POST",
                body: JSON.stringify(sendOption),
                headers: {
                  "Content-Type": "application/json",
                }
              }
          );


          if (!response.ok) {
            if (response.status === 401) {
              //do nothing
            } else {
              //handle type 500 error
              throw new Error('Failed to fetch user data');
            }

          } else {
            console.log("move sent succesfully")
            this.isMyTurn = false;
          }


        } catch (e) {
          console.log(e);
        }
      }
    },

    /*
    Starts the game
     */
    async startGame() {
      event.preventDefault();

      const sendStart = {
        typeOfMove: "start",
      };

      try {
        const response = await fetch(`${API_BASE_URL}/backend/move`,
            {
              method: "POST",
              body: JSON.stringify(sendStart),
              headers: {
                "Content-Type": "application/json",
              }
            }
        );


        if (!response.ok) {
          if (response.status === 401) {
            //do nothing
          } else {
            //handle type 500 error
            throw new Error('Failed to fetch user data');
          }

        } else {
          this.isStartTurn();
          console.log("Game has started!")
          this.gameStarted = true;
        }


      } catch (e) {
        console.log(e);
      }
    },

    /*
    Changes if you see the logout popup or not
     */
    changeLogOutMenu() {
      this.logOutMenu = !this.logOutMenu;
    }
  }
}

</script>

<!------------------------------------------------------------------------------------------------->

<template>
  <div class="chip_count" style="width: calc(100vw - 180px); margin-left: 95px;">
    <div class="chips" v-bind:value="chipAmount">
      €${{ chipAmount }}
    </div>
  </div>
  <div class="userMenu" v-show="logOutMenu">
    <button type="button" @click="leaveGame">Leave Game</button>
    <button type="button" @click="logOut" style="color: red">Log Out</button>
  </div>

  <button type="button" class="user_icon"
          @click="changeLogOutMenu()">{{ name }}
  </button>


  <button type="button" class="interact-game-button"
          style="position: relative; margin-top: -53px; float: left" v-show="!showQR"
          @click="changeQR()">Share
  </button>

  <div class="qr_code" v-show="showQR">
    <p class="qr-text">To join, fill in this code:</p>
    <p class="code">{{ requiredCode }}</p>
    <p class="qr-text">or scan the QR-code below:</p>
    <img src="./assets/qr_code.png" alt="qr_code">
    <button type="button" class="interact-game-button" @click="changeQR()">Go back</button>
  </div>

  <div class="game" v-show="!showQR">
    <button type="button" class="start-game-button" v-show="!gameStarted" @click="startGame()">
      start game
    </button>
    <div class="gameboard">
      <GameBoard class="players" :peopleOnGame="peopleOnGame" :pot="pot" :host="host" :key="gameKey" :playerBets="playerBets"/>
    </div>
    <div class="wait" v-if="(!isMyTurn && gameStarted)">
      <div class="turnText">Wait for your turn</div>
      <div style="height:0; width: 90vw; margin-left: 5vw; margin-bottom: 10px; border-radius: 50%; border: 3px #3F3047 solid;"></div>
      <form class="wait_options">
        <input type="radio" id="fold" name="radio" v-model="turnChoice" value="fold" class="wait_options-radio">
        <label for="fold"> Fold </label><br>

        <input type="radio" id="call-check" name="radio" v-model="turnChoice" value="check" class="wait_options-radio">
        <label for="call-check"> Call/Check ({{ betAmount }})</label><br>

        <input type="radio" id="raise" name="radio" v-model="turnChoice" value="raise" class="wait_options-radio">
        <label for="raise"> Raise </label>
      </form>
    </div>

    <!------------------------------------------------------------------------------------------------->

    <div class="turn" v-else-if="isMyTurn">
      <div class="turnText">Your Turn!</div>
      <div style="height:0; width: 90vw; margin-left: 5vw; margin-bottom: 10px; border-radius: 50%; border: 3px #3F3047 solid;"></div>
      <div class="turnChoice" v-if="!raise">
        <button class="foldButton" id="interact-turn-button" type="button" value="fold" @click="chooseOption()">Fold</button>
        <button class="checkButton" id="interact-turn-button" type="button" value="check" @click="chooseOption()">Check ({{betAmount}}) </button>
        <button class="raiseButton" id="interact-turn-button" type="button" value="raise" @click="chooseOption()">Raise</button>
      </div>

      <div class="raiseChoice" v-else-if="raise">
        <button class="raiseButtonReturn" id="interact-turn-raise-button" type="button" value="return" @click="chooseRaise()">Return</button>
        <button class="raiseButtonMin" id="interact-turn-raise-button" type="button" value="min" @click="chooseRaise()">Min ({{betAmount}})</button>
        <button class="raiseButtonHalf" id="interact-turn-raise-button" type="button" value="half" @click="chooseRaise()">1.5x
          ({{1.5 * betAmount}})</button>
        <button class="raiseButtonFull" id="interact-turn-raise-button" type="button" value="full" @click="chooseRaise()">2x
          ({{2 * betAmount}})</button>
        <button class="raiseButtonAllIn" id="interact-turn-raise-button" style="background-color: red" type="button" value="all" @click="chooseRaise()">All in!</button>

        <div class="slide-container" style="margin-top: 80px">
          <input v-model="raiseAmount" type="range" :min="betAmount" :max="chipAmount" value="0" class="slider">
        </div>
        <input type="text" class="box-input" style="text-align: center; float: left; margin-left: calc(14vw - 27px); margin-top:10px; width: 50vw" v-model="raiseAmount">
        <button type="submit" class="interact-button" style="width: 130px; height: 70px; margin-left: calc(60vw + 0px);" name="amount" @click="chooseRaise()">Confirm</button>
      </div>
    </div>

  </div>

</template>

<style>

</style>