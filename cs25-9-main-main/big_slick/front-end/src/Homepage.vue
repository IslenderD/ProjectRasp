<script>
  import { API_BASE_URL } from '@/config'

  export default {
    data() {
      return {
        joinGame: true,
        code: '',
        chipCount: null,
        logOutMenu: false,
        requiredCode: "B3QPA7",
        createdGame: false,

        /* Change after backend is connected */
        name: '',
        chipAmount: 0,
      }
    },

    async mounted() { /* fetchWallet() doesn't work in mounted, so try to force a page update instead */
      await this.fetchWallet(); /* WHY DOESN'T IT TRIGGER >:( */
    },

    // updated() {
    //   this.fetchWallet();
    // },

    methods: {
      /*
    Sleep function that makes sure everything is properly loaded before a function is called
     */
      sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms))
      },

  /*
  Function for logging out of the server
   */
      async logOut(){
        event.preventDefault();

        const requestLogOut = {
          username: this.name,
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
            if (response.status == 401) {
              this.showRegisterError = true;
            }
            else {
              //handle type 500 error
              throw new Error('Failed to fetch user data');
            }

          }

          else {
            this.$router.push('/')
          }

        } catch(e) {
          console.log(e);
        }

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
           }

         } catch(e) {
           console.log(e);
         }
       },

      /*
      Lets the player join a game if the code is correct
       */
      async handleJoinGame() {
        if ((this.code === this.requiredCode) || this.createdGame) {
          event.preventDefault();

          this.name = this.$route.params.name[0];

          const requestJoinGame = {
            username: this.name,
            isFolded: false,
            amount_bet: 400
          };

          try {
            const response = await fetch(`${API_BASE_URL}/backend/move/joinPlayer`,
                {
                  method: "POST",
                  body: JSON.stringify(requestJoinGame),
                  headers: {
                    "Content-Type": "application/json",
                  }
                }
            );


            if (!response.ok) {
              if (response.status == 401) {
                //do nothing
              }
              else {
                //handle type 500 error
                throw new Error('Failed to fetch user data');
              }

            }

            else {
              const newRoute = '/ingame/' + this.name;
              this.$router.push(newRoute);
            }


          } catch(e) {
            console.log(e);
          }
        }
      },

      /*
      Lets the player create a game
       */
      async handleCreateGame() {
        event.preventDefault();

        this.name = this.$route.params.name[0];

        const requestCreateGame = {
          username: this.name,
          maxBuyIn: this.chipCount
        };

        try {
          const response = await fetch(`${API_BASE_URL}/backend/move/createGame`,
              {
                method: "POST",
                body: JSON.stringify(requestCreateGame),
                headers: {
                  "Content-Type": "application/json",
                }
              }
          );


          if (!response.ok) {
            if (response.status == 401) {
              //do nothing
            }
            else {
              //handle type 500 error
              throw new Error('Failed to fetch user data');
            }

          }

          else {
            const newRoute = '/ingame/' + this.name;
            this.$router.push(newRoute);
          }

        } catch(e) {
          console.log(e);
        }

      },

      /*
      Function for choosing join game or create game
       */
      changeJoinGame() {
        this.joinGame = !this.joinGame;
      },
      changeLogOutMenu() {
        this.logOutMenu = !this.logOutMenu;
      },

    }
  }
</script>

<!------------------------------------------------------------------------------------------------->

<template>
  <div class="chip_count">
    <div class="chips" v-bind:value="chipAmount">
      €${{chipAmount}}
    </div>
  </div>

  <div class="userMenu" v-show="logOutMenu">
    <button type="button" @click="logOut" style="color: red">Log Out</button>
  </div>

  <Button class="user_icon" v-bind:value="name" @click="changeLogOutMenu">
    {{name}}
  </Button>

  <div class="logo">
    <img src="./assets/bigslick.png" alt="image of big slick logo">
  </div>

  <div class="join_game" v-if="joinGame">
    <button type="button" class="interact-button" style="margin-bottom: 30px;" @click="changeJoinGame()">Create game</button>
    <input type="text" id="code" name="code" v-model="code" class = "box-input" style="margin-bottom: 10px">
    <button type="submit" class="interact-button" @click="handleJoinGame()">Join game</button>
  </div>

  <div class="create_game" v-if="!joinGame">
    <button type="button" class="interact-button" style="margin-bottom: 30px;" @click="changeJoinGame()">Go Back</button>
    <div class="slide-container">
      <input v-model="chipCount" type="range" min="200" :max="Math.min(10000, this.chipAmount)" value="250" class="slider">
    </div>
    <input type="text" class="box-input" style="text-align: center" v-model="chipCount">
    <button type="submit" class="interact-button" @click="handleCreateGame()">Create game!</button>
  </div>
</template>

<!------------------------------------------------------------------------------------------------->

<style>

</style>