<script>
import { API_BASE_URL } from '@/config'

export default {
  props: {
    peopleOnGame: { type: Object },
    pot: Number,
    host: String,
    playerBets: [Number],
  },
  data() {
    return {
      stringForClass: 'player_box',
      kickMenu0: false,
      kickMenu1: false,
      kickMenu2: false,
      kickMenu3: false,
      kickMenu4: false,
      kickMenu5: false,
      currentID: 0,
      name: ''
    }
  },

  methods: {
    /*
    Checks which kick menus need to be shown(only for host)
     */
    kickMenu(id) {
      if(this.name === this.host) {
        this.checkID();
        if (id === `0`) {
          this.kickMenu0 = !this.kickMenu0;
        } else if (id === `1`) {
          this.kickMenu1 = !this.kickMenu1;
        } else if (id === `2`) {
          this.kickMenu2 = !this.kickMenu2;
        } else if (id === `3`) {
          this.kickMenu3 = !this.kickMenu3;
        } else if (id === `4`) {
          this.kickMenu4 = !this.kickMenu4;
        } else {
          this.kickMenu5 = !this.kickMenu5;
        }
      }
    },

    /*
    Checks if the player box with the given id should display their kick menu or not
     */
    display(id){
      this.checkID();
      if(this.peopleOnGame[id].username !== this.name) {
        if (id === `0`) {
          return this.kickMenu0;
        } else if (id === `1`) {
          return this.kickMenu1;
        } else if (id === `2`) {
          return this.kickMenu2;
        } else if (id === `3`) {
          return this.kickMenu3;
        } else if (id === `4`) {
          return this.kickMenu4;
        } else {
          return this.kickMenu5;
        }
      }
    },

    async kick(id){
      event.preventDefault();

      const requestLeaveGame = {
        personLeaving: this.peopleOnGame[id].username,
        personWhoLeftThem: this.name,
        amountWon: this.peopleOnGame[id].amount_bet
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
          if (response.status == 401) {
            this.showRegisterError = true;
          }
          else {
            //handle type 500 error
            throw new Error('Failed to fetch user data');
          }

        }

        else {
          console.log("person successfully kicked")
        }

      } catch(e) {
        console.log(e);
      }
    },

    checkID(){
      this.name = this.$route.params.name[0];
      for(let i = 0; i < 6; i++){
        if(this.peopleOnGame.username === this.name){
          this.currentID = i;
          break;
        }
      }
    }
  }
}
</script>

<template>
  <div class="board">
    Pot
    <div class="pot">{{ pot }}</div>
  </div>
  <div class="flex-container">
    <div :id="`peopleOnGame-${peopleOnGame.username}-info`">
      <div class="players">
        <figure v-for="(x, index) in peopleOnGame">
          <div :class="`player_box-${index}`" :id="`player_box`" :style=" x.isFolded ? 'border-color: #444444; color: #444444; background: #7085a6; opacity: 0.8': 'border-color: black'">
            <button type="button" @click="kickMenu(`${index}`)" :disabled="1 === 0" class="player_icon" :style=" x.isFolded ? 'border-color: #444444; color: #444444; background: #3a873a' : 'border-color: black'">
              {{ x.username }}
            </button>
            <div v-show="!(display(`${index}`))">
              <h1 class="player-name">{{ x.username }}</h1>
              <div class="player-chips">€${{ x.amount_bet }}</div>
            </div>
            <button type="button" @click="kick(`${index}`)" class="kickOption" v-show="display(`${index}`)">Kick Player</button>
          </div>
          <div :class="`player_bets-${index}`" id="player_bets">
            {{ playerBets[index] }}
          </div>
        </figure>
      </div>
    </div>
  </div>
</template>

<style></style>