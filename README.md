# 💬 Farcaster Idle Game

A conversational idle game where you generate "casts" through clicking and idle mechanics. Build your way from canned responses to AI-powered content generation, achieve bangers, and mint your best casts as NFTs.

## 🎮 Game Concept

Every click or idle tick generates an in-game "cast" (not posted to Farcaster). Casts have quality tiers and can accumulate likes from NPCs and real players. When a cast gets enough engagement, it becomes a **banger** that can be shared to Farcaster and minted as a 1/1 NFT.

### Progression System

- **Levels 1-10**: Pure canned casts, learn the mechanics
- **Levels 11-20**: AI starts mixing in based on your actual Farcaster activity (30%)
- **Levels 21-30**: Mostly AI-generated (80%), unlock casting to Farcaster
- **Levels 31+**: Full AI with expanded memory and editorial guidance

### Key Features

#### Cast Generation
- **Manual Clicks**: Generate casts instantly (+1 XP)
- **Idle Generation**: Auto-generate casts every ~4 minutes (+0.5 XP)
- **Quality Tiers**: Common, Uncommon, Rare, Epic, Legendary (weighted probabilities)

#### Social Mechanics (Phase 2)
- **Anonymous Feed**: See casts from players within ±2 levels (usernames hidden)
- **Likes & Replies**: Interact with canned or custom responses
- **Bangers**: 10 likes (5 from real users) = permanent banger status
- **NPC Actors**: Simulated engagement to bootstrap activity

#### AI Generation (Phase 3)
- **Memory System**: Short-term and long-term memory based on level
- **Personalization**: AI learns from your actual Farcaster casts
- **Quality Variants**: Higher quality = more sophisticated AI prompts

#### NFT System (Phase 4)
- **Mint Bangers**: 1/1 NFTs on Base chain
- **Bidding**: Highest bidder gets the NFT
- **Revenue Split**: 90% creator, 10% platform

## 🚀 Quick Start

### Prerequisites

- Node.js 22.11.0 or higher
- Java JDK 11+ (for ClojureScript compiler)

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run watch
```

Visit `http://localhost:8080` to play!

### Production Build

```bash
npm run release
```

## 📁 Project Structure

```
fc-idlegame/
├── src/
│   └── fc_idlegame/
│       ├── core.cljs           # Main app & initialization
│       ├── game.cljs           # Game logic (XP, casts, feed)
│       ├── views.cljs          # UI components (Reagent)
│       ├── config.cljs         # Game constants & configuration
│       ├── canned_casts.cljs   # 100 pre-written casts
│       └── firebase.cljs       # Backend integration (stub)
├── public/
│   ├── .well-known/
│   │   └── farcaster.json      # Farcaster mini app manifest
│   ├── index.html              # HTML entry point
│   └── styles.css              # Game styling
├── shadow-cljs.edn             # ClojureScript build config
├── package.json                # Node dependencies
├── ROADMAP.md                  # Development roadmap
└── README.md                   # This file
```

## 🎯 Current Status: Phase 1 (MVP)

The MVP is complete with:
- ✅ Core idle mechanics (clicking, idle generation)
- ✅ XP and leveling system (1-30+)
- ✅ 100 canned casts across 5 categories
- ✅ Cast quality tiers with weighted probabilities
- ✅ Cast lifespan system
- ✅ Player statistics and progress tracking
- ✅ LocalStorage persistence with auto-save
- ✅ Polished UI with cast cards and feed

See [ROADMAP.md](./ROADMAP.md) for the complete development plan.

## 🛠️ Technology Stack

- **Frontend**: ClojureScript + Shadow CLJS
- **UI**: Reagent (React wrapper)
- **Backend** (Phase 2): Firebase Firestore
- **AI** (Phase 3): OpenAI API
- **Blockchain** (Phase 4): Base (Ethereum L2)
- **Farcaster**: Mini App SDK + farcaster-cljs wrapper

## 🎨 Game Mechanics Deep Dive

### XP Sources & Leveling

| Action | XP Gained |
|--------|-----------|
| Manual Click | 1 XP |
| Idle Cast | 0.5 XP |
| Like Received | 5 XP |
| Banger Achieved | 100 XP |

**Level Formula**: `level = √(XP / 100)`

**Progression Timeline**:
- **Passive Play**: 10 levels in ~1 month
- **Active Play**: 10 levels in ~1 week (2 hrs/day @ 120 clicks/min)

### Cast Quality Distribution

| Quality | Weight | Emoji | Description |
|---------|--------|-------|-------------|
| Common | 80% | ⚪ | Standard canned casts |
| Uncommon | 15% | 🟢 | Slightly enhanced |
| Rare | 4% | 🔵 | AI with cliche detection |
| Epic | 0.9% | 🟣 | Full short-term memory |
| Legendary | 0.1% | 🟡 | Full memory + topic analysis |

### Cast Lifespan

- **Levels 1-10**: 1 hour
- **Levels 11-20**: 6 hours
- **Levels 21-30**: 24 hours
- **Bangers**: Permanent

## 🔮 Future Features

### Phase 2: Social & Multiplayer (3-4 weeks)
- Anonymous feed with real players
- NPC auto-liking and replies
- Banger detection system
- SVG share images for Farcaster

### Phase 3: AI Generation (4-5 weeks)
- Fetch user's real Farcaster casts
- Memory system (short-term + long-term)
- AI-generated casts with OpenAI
- Progressive personalization

### Phase 4: NFT Minting (5-6 weeks)
- Smart contract on Base
- Bidding system for bangers
- 1/1 NFT mints
- Revenue split (90/10)

### Phase 5: Advanced Features (6-8 weeks)
- Guilds and team competitions
- Achievements and quests
- Premium features
- Mobile PWA

## 🎯 Success Metrics

**MVP** (Phase 1):
- 100 players try the game
- Average 5+ minute sessions
- 50 players reach level 3

**Social** (Phase 2):
- 500 active players
- 1,000 casts generated daily
- 100 bangers achieved

**AI** (Phase 3):
- 80% reach level 11 (AI unlock)
- 50% satisfaction with AI casts

**NFT** (Phase 4):
- 50 NFTs minted
- 10 ETH total volume

## 🤝 Contributing

This is currently in active development. Feedback and bug reports welcome!

## 📝 Development Notes

### Integrating farcaster-cljs

To add actual Farcaster SDK functionality:

1. Add dependency to `shadow-cljs.edn`:
```clojure
:dependencies [[farcaster-cljs "0.1.0"]]
```

2. Uncomment SDK calls in `src/fc_idlegame/core.cljs`:
```clojure
(ns fc-idlegame.core
  (:require [farcaster-cljs.core :as fc]))

(defn init-farcaster! []
  (go
    (<! (fc/quick-start!))
    (let [username (<! (fc/get-username))
          fid (<! (fc/get-fid))]
      ;; ... update app state
      )))
```

### Environment Variables

For production deployment, set:

```bash
FIREBASE_CONFIG=<your-firebase-config-json>
OPENAI_API_KEY=<your-openai-key>
```

### Testing Locally

The game works entirely in the browser for MVP. No backend required yet!

## 📄 License

MIT

## 🔗 Links

- [Farcaster](https://www.farcaster.xyz/)
- [farcaster-cljs](https://github.com/mxjxn/farcaster-cljs)
- [Base](https://base.org/)
- [ClojureScript](https://clojurescript.org/)

---

**Status**: 🚧 Phase 1 (MVP) Complete - Phase 2 Starting Soon

Built with ❤️ for the Farcaster community
