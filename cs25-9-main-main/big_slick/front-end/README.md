# Big Slick Frontend

This package contains the Vue 3 + Vite client for the Big Slick poker game.

## Prerequisites

- Node.js 20+ (the project currently targets Node 20/22)
- npm 10+

## Environment variables

- `VITE_API_BASE_URL` (default: `http://192.168.222.172:8081`)
- `VITE_WS_BASE_URL` (default: `ws://192.168.222.172:8081`)
- `VITE_DEV_PROXY_TARGET` (dev proxy target used by `vite.config.js`)

## Install

```sh
npm install
```

## Run in development mode

```sh
npm run dev
```

## Build for production

```sh
npm run build
```

## Preview production build locally

```sh
npm run preview
```

For full-stack setup (backend + frontend + optional Raspberry Pi scripts), see the root `README.md`.

