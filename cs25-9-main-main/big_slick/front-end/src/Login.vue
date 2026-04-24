<script>
import { API_BASE_URL } from '@/config'

export default {
  data() {
    return {
      username: '',
      password: '',
      usernameRegister: '',
      passwordRegister: '',
      repeat: '',
      showLoginError: false,
      showRegisterError: false,
      isLogin: true,
    }
  },

  methods: {
    /*
      Gets called when the log-in button is pressed.
      Checks if the username and password exist in the database and brings the user to the homepage
      if this is true.
      If the username and password do not exist, an error is shown.
     */
    async handleLogin() {
      event.preventDefault();

      const requestLogin = {
        username: this.username,
        password: this.password
      };

      try {
        const response = await fetch(`${API_BASE_URL}/backend/auth/login`,
            {
              method: "POST",
              body: JSON.stringify(requestLogin),
              headers: {
                "Content-Type": "application/json",
              }
            }
        );


        if (!response.ok) {
          if (response.status === 401) {
            this.showLoginError = true;
          } else {
            //handle type 500 error
            throw new Error('Failed to fetch user data');
          }

        } else {
          const data = await response.text();
          console.log(data.username);
          this.showLoginError = false;

          const newRoute = '/homepage/' + this.username;

          this.$router.push(newRoute);
          this.username = '';
          this.password = '';
        }


      } catch (e) {
        console.log(e);
      }
    },

    /*
    Gets called if the register button is pressed.
    Checks if the password and repeated password are the same, if they are sends user to homepage.
    Otherwise, it shows an error.
     */

    async handleRegister() {
      if (this.passwordRegister === this.repeat) {
        event.preventDefault();

        const requestRegister = {
          username: this.usernameRegister,
          password: this.passwordRegister
        };

        try {
          const response = await fetch(`${API_BASE_URL}/backend/auth/sign-in`,
              {
                method: "POST",
                body: JSON.stringify(requestRegister),
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
            const data = await response.text();
            console.log(data);
            this.showRegisterError = false;

            const newRoute = '/homepage/' + this.usernameRegister;

            this.$router.push(newRoute);
            this.usernameRegister = '';
            this.passwordRegister = '';
          }

        } catch (e) {
          console.log(e);
        }
      } else {
        this.showRegisterError = true;
      }
    },

    /*
    Changes if you see the login section or the register section.
     */
    changeLogin() {
      this.isLogin = !this.isLogin;
    },

    /*
    Hides the error if it is shown.
     */
    hideError() {
      this.showLoginError = false;
      this.showRegisterError = false;
    }
  }
}

</script>

<!------------------------------------------------------------------------------------------------->

<template>
  <div class="login-box">
    <!-- if you are on the login screen -->
    <form v-if="isLogin">
      <label for="username" class="box-name">Username</label>
      <input type="text" id="username" name="username" v-model="username" class="box-input">

      <label for="password" class="box-name">Password</label>
      <input type="password" id="password" name="password" v-model="password" class="box-input">

      <button type="button" class="interact-button" @click="handleLogin()">Login</button>
    </form>
    <button type="button" class="login-button" @click="changeLogin()" v-if="isLogin">no account?
      register here!
    </button>

    <!-- if you are on the register screen -->
    <form v-if="!isLogin">
      <label for="username" class="box-name">Username</label>
      <input type="text" id="username" name="username" v-model="usernameRegister" class="box-input">

      <label for="password" class="box-name">Password</label>
      <input type="password" id="password" name="password" v-model="passwordRegister"
             class="box-input">

      <label for="repeat" class="box-name" style="width: 170px">Repeat Password</label>
      <input type="password" id="repeat" name="repeat" v-model="repeat" class="box-input">

      <button type="button" class="interact-button" @click="handleRegister()">Register</button>
    </form>
    <button type="button" class="login-button" @click="changeLogin()" v-if="!isLogin">have an
      account? login here!
    </button>

  </div>

  <!-- Error shown after login box -->
  <div id="errorBox" class="error-box" v-show="showLoginError">
    <button type="button" @click="hideError()">×</button>
    Invalid username or password.
  </div>

  <!-- Error shown after login box -->
  <div id="errorBox" class="error-box" v-show="showRegisterError">
    <button type="button" @click="hideError()">×</button>
    Password and repeated password are not the same
  </div>

</template>

<style>

</style>