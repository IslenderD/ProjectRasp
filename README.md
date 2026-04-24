# Big Slick

Big Slick is a multiplayer poker project split across three parts:

- a Java backend (REST + WebSocket) for user accounts, wallet management, and game communication
- a Vue frontend for login, lobby, and in-game UI
- a Python service used as game logic / hardware-facing bridge (Raspberry Pi side)

## Repository layout

- `pom.xml`: Maven configuration for the Java backend build
- `big_slick/src/main/java`: Java backend source code
- `big_slick/src/main/webapp`: Java web resources (`web.xml`, JSP)
- `big_slick/front-end`: Vue 3 + Vite client
- `big_slick/pi-backend`: Python service and game logic

## Tech stack

- Java (Jakarta REST / Jersey, WebSocket, MariaDB driver)
- Vue 3 + Vue Router + Vite
- Python 3 (Flask, requests, waitress)

## Prerequisites

- Git
- Java 25
- Maven 3.9+
- Node.js 20+ and npm
- Python 3.10+
- MariaDB (for account and wallet persistence)
- A servlet container compatible with Jakarta EE 10 (for WAR deployment), such as Tomcat 10.1+

## Configuration notes

Frontend endpoint configuration:

- `VITE_API_BASE_URL` (default: `http://192.168.222.172:8081`)
- `VITE_WS_BASE_URL` (default: `ws://192.168.222.172:8081`)
- `VITE_DEV_PROXY_TARGET` (used by Vite dev proxy)

Python service endpoint configuration:

- `BACKEND_BASE_URL` (default: `http://192.168.222.172:8081`)

## Backend setup (Java)

Build the WAR:

```sh
git clone <your-github-url>
cd <project-folder>
mvn -f pom.xml clean package
```

Expected output WAR:

- `target/big-slick.war`

Deploy `target/big-slick.war` to your servlet container.

API base path is configured as:

- `/backend/*` (see `big_slick/src/main/java/endpoints/MainPath.java`)

### Database

The backend uses MariaDB through `big_slick/src/main/java/database/DatabaseHandler.java`.

Set database connection values through environment variables:

- `BIGSLICK_DB_URL` (example: `jdbc:mariadb://localhost:3306/my_db`)
- `BIGSLICK_DB_NAME`
- `BIGSLICK_DB_SCHEMA`
- `BIGSLICK_DB_USER`
- `BIGSLICK_DB_PASSWORD`

## Frontend setup (Vue)

```sh
cd "big_slick/front-end"
npm install
npm run dev
```

Production build:

```sh
cd "big_slick/front-end"
npm run build
npm run preview
```

## Raspberry Pi / Python service setup

Install dependencies:

```sh
cd "big_slick/pi-backend"
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

Run the service:

```sh
cd "big_slick/pi-backend"
python3 receiveInformation.py
```

## Tests

Java tests:

```sh
mvn -f pom.xml test
```

Python test harness:

```sh
cd "big_slick/pi-backend"
python3 gameClassTests.py
```
