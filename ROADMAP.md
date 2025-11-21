# Farcaster Idle Game - Development Roadmap

## Overview

A conversational idle game where players generate "casts" through clicking and idle mechanics. Casts have quality tiers, can become "bangers" through likes, and can be minted as NFTs. The game progressively unlocks AI-generated content based on the player's actual Farcaster activity.

---

## Phase 1: MVP (CURRENT) ✅

**Goal**: Core idle game mechanics with canned casts, local storage persistence

### Features Implemented

#### Game Mechanics
- ✅ XP and leveling system
  - XP sources: manual clicks (1 XP), idle casts (0.5 XP), likes (5 XP), bangers (100 XP)
  - Level calculation: `level = √(XP / 100)`
  - Progression: 10 levels in ~1 month passive, or ~1 week with 2hrs/day active play
- ✅ Cast generation system
  - Click to generate casts manually
  - Idle generation every ~4 minutes
  - Quality tiers: Common (80%), Uncommon (15%), Rare (4%), Epic (0.9%), Legendary (0.1%)
- ✅ Cast lifespan system
  - Levels 1-10: 1 hour lifespan
  - Levels 11-20: 6 hour lifespan
  - Levels 21-30: 24 hour lifespan
  - Bangers: Never expire

#### Content
- ✅ 100 canned casts organized by category
  - Philosophical (20)
  - Crypto/Web3 (20)
  - Humor (20)
  - Relatable (20)
  - Wholesome (20)

#### UI/UX
- ✅ Player stats header (level, casts, bangers, active casts)
- ✅ XP progress bar
- ✅ Cast generator button with last cast preview
- ✅ Feed showing all active casts
- ✅ Cast cards with quality indicators, likes, and metadata
- ✅ Statistics panel (XP, activity, quality breakdown, time)
- ✅ Level features panel showing unlocked/locked features

#### Data Persistence
- ✅ LocalStorage for game state
- ✅ Auto-save every 30 seconds
- ✅ Firebase integration layer (stub implementation ready)

#### Technical
- ✅ ClojureScript + shadow-cljs setup
- ✅ Reagent for UI components
- ✅ Firebase and OpenAI dependencies added
- ✅ Game configuration system
- ✅ Modular namespace organization

### Known Limitations (MVP)
- All casts use canned content (no AI generation yet)
- Feed only shows player's own casts
- No multiplayer interactions
- No sharing or SVG generation
- No actual Firebase backend connection
- No Farcaster SDK integration

---

## Phase 2: Social & Multiplayer

**Goal**: Anonymous feed, NPC interactions, banger system, share functionality

### Features to Implement

#### Firebase Backend Integration
- [ ] Set up Firebase project and Firestore database
- [ ] Implement real-time data sync
- [ ] Collections: `players`, `casts`, `likes`, `replies`, `bangers`, `nfts`
- [ ] Cloud Functions for:
  - Cast generation triggers
  - Banger detection
  - NPC behavior
  - Feed aggregation

#### Anonymous Feed System
- [ ] Feed shows casts from players within ±2 levels
- [ ] Usernames hidden on in-game casts
- [ ] Real-time feed updates
- [ ] Cast filtering and sorting
- [ ] Pagination for large feeds

#### NPC System
- [ ] Simulated players at each level
- [ ] Auto-liking mechanism (10% probability every 30s)
- [ ] Canned reply generation from NPCs
- [ ] NPC activity scales with player level

#### Banger Detection
- [ ] Automatic promotion to banger status (10 likes, 5 from real users)
- [ ] Banger badge and special styling
- [ ] Permanent storage (no expiration)
- [ ] Banger leaderboard

#### Player Interactions
- [ ] Like other players' casts
- [ ] Reply with canned responses (editable before sending)
- [ ] View cast details (likes, replies)
- [ ] Report/flag inappropriate content

#### Share System
- [ ] SVG generation for "Level X" progress shares
  - 3 random recent casts displayed diagonally
  - Player stats box (semi-transparent)
  - Level, total casts, bangers count
- [ ] Banger share images
  - Cast text prominently displayed
  - Like count badge
  - "🔥 Banger" indicator
- [ ] Deep linking to mini app
- [ ] Farcaster Frame integration for shares

### Technical Improvements
- [ ] WebSocket connections for real-time updates
- [ ] Optimistic UI updates
- [ ] Error handling and retry logic
- [ ] Rate limiting
- [ ] Caching strategy

### Timeline
**Estimated: 3-4 weeks**

---

## Phase 3: AI Generation & Memory System

**Goal**: Personalized AI-generated casts based on player's Farcaster activity

### Features to Implement

#### Farcaster API Integration
- [ ] Fetch user's recent casts and replies
- [ ] Fetch engagement metrics (likes, recasts)
- [ ] Identify top-performing casts
- [ ] Extract themes and topics from user's content

#### Memory System
- [ ] Short-term memory: Recent N casts (10-50 based on level)
- [ ] Long-term memory: Top casts from last N weeks
- [ ] Topic extraction and categorization
- [ ] Memory visualization in UI
- [ ] Memory management (pruning old data)

#### AI Cast Generation (OpenAI)
- [ ] Levels 11-20: 30% AI-generated, 70% canned
- [ ] Levels 21-30: 50% AI-generated, 50% canned
- [ ] Levels 31+: 80% AI-generated, 20% canned
- [ ] Quality tier prompts:
  - **Common**: Pure canned (no AI)
  - **Uncommon**: Canned with player context
  - **Rare**: AI with cliche detector
  - **Epic**: AI with full short-term memory
  - **Legendary**: AI with full memory + topic analysis

#### AI Prompt Engineering
- [ ] System prompts for different quality tiers
- [ ] Context injection (memory, topics, style)
- [ ] Length and format constraints
- [ ] Toxicity filtering
- [ ] Brand voice consistency

#### Editorial Guidance
- [ ] Topic suggestions based on memory
- [ ] Style mimicry of player's best casts
- [ ] Trend awareness (from broader Farcaster activity)
- [ ] Personality consistency

### Level-based Feature Unlocking
- [ ] Level 11: Unlock AI generation (10 cast memory)
- [ ] Level 20: Expanded memory (20 casts)
- [ ] Level 30: Full AI with 50 cast memory + can cast to Farcaster

### Technical Requirements
- [ ] OpenAI API integration
- [ ] Token usage optimization
- [ ] Response caching for similar prompts
- [ ] Fallback to canned casts on API errors
- [ ] Cost monitoring and limits

### Timeline
**Estimated: 4-5 weeks**

---

## Phase 4: NFT Minting & Marketplace

**Goal**: Mint bangers as NFTs, bidding system, revenue splits

### Features to Implement

#### Smart Contract (Base Chain)
- [ ] ERC-721 contract for 1/1 NFT mints
- [ ] Metadata storage (IPFS or Arweave)
- [ ] On-chain banger verification
- [ ] Revenue split enforcement (90% creator, 10% platform)

#### Bidding System
- [ ] Open bidding for unminted bangers
- [ ] Minimum bid thresholds
- [ ] Bid history and notifications
- [ ] Automatic minting upon winning bid
- [ ] Escrow system

#### NFT Metadata
- [ ] Cast text as primary content
- [ ] Quality tier badge
- [ ] Like count at mint time
- [ ] Player level when created
- [ ] Timestamp and provenance
- [ ] Visual representation (generated artwork)

#### Wallet Integration
- [ ] Connect Ethereum wallet (via Farcaster SDK)
- [ ] View ETH balance
- [ ] Transaction signing
- [ ] Transaction history
- [ ] Gas estimation

#### Marketplace Features
- [ ] Browse all minted bangers
- [ ] Filter by quality, level, date
- [ ] Search by cast content
- [ ] Creator profiles
- [ ] Sales history and floor prices

#### Revenue Management
- [ ] Automatic split on sale
- [ ] Withdrawal to creator wallet
- [ ] Platform fee collection
- [ ] Transaction receipts

### Technical Requirements
- [ ] Solidity smart contract development
- [ ] Contract testing and auditing
- [ ] Base chain deployment
- [ ] Subgraph for indexing NFT data
- [ ] IPFS pinning service

### Timeline
**Estimated: 5-6 weeks**

---

## Phase 5: Advanced Features

**Goal**: Polish, social features, gamification, monetization

### Features to Implement

#### Advanced Social
- [ ] Follow other players
- [ ] Private messages
- [ ] Guilds/teams
- [ ] Team competitions
- [ ] Cast collaborations

#### Gamification
- [ ] Achievements system
- [ ] Daily/weekly quests
- [ ] Streak bonuses
- [ ] Seasonal events
- [ ] Limited-time casts

#### Monetization
- [ ] Premium subscription (faster XP, more memory)
- [ ] Cosmetic upgrades (themes, badges)
- [ ] Sponsored casts (ads)
- [ ] Affiliate program

#### Analytics
- [ ] Player dashboard with detailed stats
- [ ] Cast performance analytics
- [ ] Engagement metrics
- [ ] A/B testing for cast styles

#### Content Tools
- [ ] Cast editor with preview
- [ ] Style templates
- [ ] Emoji/reaction system
- [ ] Cast bookmarking

#### Mobile Optimization
- [ ] Progressive Web App (PWA)
- [ ] Touch gestures
- [ ] Offline mode
- [ ] Push notifications

### Timeline
**Estimated: 6-8 weeks**

---

## Technical Debt & Infrastructure

### Ongoing Tasks
- [ ] Performance optimization (bundle size, lazy loading)
- [ ] Comprehensive test coverage
- [ ] Error monitoring (Sentry)
- [ ] Analytics (Mixpanel/Amplitude)
- [ ] Documentation updates
- [ ] Security audits
- [ ] Accessibility improvements (WCAG compliance)
- [ ] Internationalization (i18n)

### DevOps
- [ ] CI/CD pipeline
- [ ] Staging environment
- [ ] Database backups
- [ ] Monitoring and alerts
- [ ] Load testing
- [ ] CDN setup

---

## Success Metrics

### Phase 1 (MVP)
- [ ] 100 players try the game
- [ ] Average session: 5+ minutes
- [ ] 50 players reach level 3

### Phase 2 (Social)
- [ ] 500 active players
- [ ] 1,000 casts generated daily
- [ ] 100 bangers achieved
- [ ] 50 shares to Farcaster

### Phase 3 (AI)
- [ ] 80% of players reach level 11 (AI unlock)
- [ ] 50% player satisfaction with AI casts
- [ ] 5,000 AI casts generated

### Phase 4 (NFT)
- [ ] 50 NFTs minted
- [ ] 10 ETH total sales volume
- [ ] 20 unique collectors

### Phase 5 (Scale)
- [ ] 5,000+ active players
- [ ] 50,000+ casts daily
- [ ] Sustainable revenue model
- [ ] Community-driven content

---

## Open Questions & Considerations

1. **Moderation**: How to handle inappropriate AI-generated content?
2. **Economics**: What's the right balance for NFT pricing and fees?
3. **Scalability**: Can Firebase handle 10k+ concurrent users?
4. **AI Costs**: How to manage OpenAI API costs at scale?
5. **Legal**: Terms of service for user-generated content and NFTs?
6. **Competition**: How to differentiate from other idle/social games?

---

## Next Actions (Immediate)

1. **Complete MVP testing**
   - Test all game mechanics locally
   - Fix any UI/UX issues
   - Verify localStorage persistence

2. **Firebase Setup**
   - Create Firebase project
   - Set up Firestore schema
   - Test read/write operations

3. **Deploy MVP**
   - Set up hosting (Vercel/Netlify)
   - Configure domain and SSL
   - Test in Farcaster mini app context

4. **Gather Feedback**
   - Share with small group of testers
   - Collect feedback on core mechanics
   - Iterate on UX

5. **Start Phase 2 Development**

---

Last Updated: 2025-11-21
