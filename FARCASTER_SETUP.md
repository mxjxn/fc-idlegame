# Farcaster SDK Integration Guide

This guide covers the Farcaster Mini App SDK integration using `farcaster-cljs`.

## Integration Complete ✅

The app now uses `farcaster-cljs` from `github.com/mxjxn/farcaster-cljs` for Farcaster Mini App SDK integration.

## What's Implemented

### 1. Farcaster SDK Initialization
- Uses `farcaster-cljs.core/quick-start!` to initialize the SDK
- Retrieves user information:
  - Username via `fc/get-username`
  - FID (Farcaster ID) via `fc/get-fid`
  - Display name via `fc/get-display-name`

### 2. User Authentication Flow
1. App starts → Calls `init-farcaster!`
2. Farcaster SDK initializes → Gets user info
3. App state updated with real Farcaster user data
4. Firebase initialized (if config provided)
5. Player data loaded from Firebase (if available)

### 3. Fallback Behavior
- If Farcaster SDK fails to initialize, app falls back to mock data
- Allows development/testing without Farcaster environment
- Console logs indicate which mode is active

## Code Structure

### Core Integration (`src/fc_idlegame/core.cljs`)

```clojure
(ns fc-idlegame.core
  (:require [farcaster-cljs.core :as fc]
            [fc-idlegame.firebase :as firebase]
            [fc-idlegame.config :as config]))

(defn init-farcaster! []
  (go
    (try
      ;; Initialize Farcaster Mini App SDK
      (<! (fc/quick-start!))
      
      ;; Get user information
      (let [username (<! (fc/get-username))
            fid (<! (fc/get-fid))
            display-name (<! (fc/get-display-name))]
        
        ;; Update app state with real Farcaster data
        (swap! app-state ...)
        
        ;; Initialize Firebase and load player data
        ...))))
```

## Dependencies

### shadow-cljs.edn
```clojure
:dependencies [[farcaster-cljs "0.1.0" 
                :git/url "https://github.com/mxjxn/farcaster-cljs.git" 
                :sha "HEAD"]]
```

## Testing

### In Farcaster Mini App Environment
1. Deploy app to Farcaster-compatible hosting
2. Open in Farcaster client
3. SDK will automatically authenticate user
4. Check console for: `✅ Farcaster SDK initialized!`

### Local Development
- App falls back to mock data if SDK not available
- Check console for: `⚠️ Using mock data for development`
- All game features work with mock user data

## Next Steps (Phase 3)

When implementing AI generation (Phase 3), you can use Farcaster API to:
- Fetch user's recent casts: `firebase/fetch-user-casts!`
- Analyze cast patterns for AI personalization
- Use user's Farcaster activity to inform AI prompts

## Troubleshooting

### SDK Not Initializing
- Check that app is running in Farcaster Mini App context
- Verify `farcaster-cljs` dependency is installed
- Check browser console for errors

### User Data Not Loading
- Verify Farcaster SDK successfully initialized
- Check that `fc/get-username`, `fc/get-fid`, `fc/get-display-name` return values
- Fallback to mock data should work automatically

### Firebase Integration
- Firebase initializes after Farcaster authentication
- Player data loads from Firebase using FID as key
- If Firebase not configured, app continues with localStorage

## API Reference

### farcaster-cljs Functions Used
- `fc/quick-start!` - Initialize Farcaster Mini App SDK
- `fc/get-username` - Get current user's username
- `fc/get-fid` - Get current user's Farcaster ID
- `fc/get-display-name` - Get current user's display name

All functions return channels (use `<!` to await results).

