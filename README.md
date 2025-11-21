# Farcaster Idle Game

An idle clicker game built as a Farcaster Mini App using ClojureScript and the farcaster-cljs library.

## Project Structure

```
fc-idlegame/
├── src/
│   └── fc_idlegame/
│       ├── core.cljs       # Main app initialization & Farcaster integration
│       ├── game.cljs       # Game logic & state management
│       └── views.cljs      # Reagent UI components
├── public/
│   ├── .well-known/
│   │   └── farcaster.json  # Farcaster mini app manifest
│   ├── index.html          # HTML entry point
│   └── styles.css          # Game styling
├── shadow-cljs.edn         # ClojureScript build config
└── package.json            # Node dependencies
```

## Setup

### Prerequisites

- Node.js 22.11.0 or higher
- Java JDK 11 or higher (for ClojureScript compiler)

### Installation

1. **Install dependencies:**
   ```bash
   npm install
   ```

2. **Add farcaster-cljs dependency:**

   To use the actual farcaster-cljs library, you'll need to either:
   - Publish it to Clojars, or
   - Add it as a git dependency in `shadow-cljs.edn`

   For now, the code has placeholders for Farcaster SDK calls.

## Development

### Start development server:

```bash
npm run watch
```

This will:
- Start shadow-cljs in watch mode
- Serve the app at http://localhost:8080
- Auto-reload on file changes

### Build for production:

```bash
npm run release
```

## Game Mechanics

### Current Features:

1. **Click to earn points** - Manual clicking generates 1 point per click
2. **Auto-generation** - Buildings generate points automatically
3. **Buildings** - Purchase buildings to increase passive income:
   - Auto Clicker (1 pt/s)
   - Point Generator (5 pt/s)
   - Point Factory (25 pt/s)
   - Mega Factory (100 pt/s)
4. **Statistics tracking** - Tracks clicks, points earned, and playtime

### Planned Features:

- Full Farcaster SDK integration (user profiles, sharing casts, etc.)
- Upgrades system
- Prestige/reset mechanics
- Leaderboards
- Social features (compete with other Farcasters)
- NFT rewards or achievements

## Integrating farcaster-cljs

The game is scaffolded to use `farcaster-cljs`. To integrate it:

1. **Add the dependency to `shadow-cljs.edn`:**
   ```clojure
   :dependencies [[farcaster-cljs "0.1.0"]]
   ```

2. **Uncomment the SDK calls in `src/fc_idlegame/core.cljs`:**
   ```clojure
   (ns fc-idlegame.core
     (:require [farcaster-cljs.core :as fc]
               ...))

   (defn init-farcaster! []
     (go
       (<! (fc/quick-start!))
       (let [username (<! (fc/get-username))
             fid (<! (fc/get-fid))
             display-name (<! (fc/get-display-name))]
         (swap! app-state assoc
                :initialized? true
                :farcaster {:username username
                            :fid fid
                            :display-name display-name}))))
   ```

3. **Add Farcaster-specific features:**
   - Share progress to cast
   - View other players' profiles
   - Leaderboards based on FID
   - Ethereum wallet integration for purchases

## Deployment

To deploy as a Farcaster Mini App:

1. Build for production: `npm run release`
2. Deploy the `public/` folder to your hosting (Vercel, Netlify, etc.)
3. Update `public/.well-known/farcaster.json` with your actual domain URLs
4. Ensure HTTPS is enabled
5. Test using Farcaster's mini app testing tools

## License

MIT
