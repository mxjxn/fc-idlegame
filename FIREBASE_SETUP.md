# Firebase Setup Guide

This guide will help you set up Firebase for the Farcaster Idle Game.

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: `fc-idlegame` (or your preferred name)
4. Disable Google Analytics (optional)
5. Click "Create project"

## Step 2: Get Firebase Configuration

1. In Firebase Console, click the gear icon ⚙️ next to "Project Overview"
2. Select "Project settings"
3. Scroll down to "Your apps" section
4. Click the web icon `</>` to add a web app
5. Register app with nickname: "Farcaster Idle Game"
6. Copy the Firebase configuration object

It will look like:
```javascript
const firebaseConfig = {
  apiKey: "AIza...",
  authDomain: "your-project.firebaseapp.com",
  projectId: "your-project-id",
  storageBucket: "your-project.appspot.com",
  messagingSenderId: "123456789",
  appId: "1:123456789:web:abc123"
};
```

## Step 3: Set Up Firestore Database

1. In Firebase Console, go to "Firestore Database"
2. Click "Create database"
3. Start in **test mode** (we'll add security rules later)
4. Choose a location (closest to your users)
5. Click "Enable"

## Step 4: Configure Environment Variables

Create a `.env` file in the project root (or set environment variables):

```bash
FIREBASE_API_KEY=AIza...
FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_STORAGE_BUCKET=your-project.appspot.com
FIREBASE_MESSAGING_SENDER_ID=123456789
FIREBASE_APP_ID=1:123456789:web:abc123
```

## Step 5: Load Firebase SDK

### Option A: Via CDN (Recommended for development)

Add to `public/index.html` before the main script:

```html
<!-- Firebase SDK -->
<script type="module">
  import { initializeApp } from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-app.js';
  import { getFirestore, collection, doc, setDoc, getDoc, addDoc, query, where, orderBy, limit, onSnapshot, serverTimestamp } from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-firestore.js';
  
  // Make Firebase functions globally available
  window.firebase = {
    app: { initializeApp },
    firestore: {
      getFirestore,
      collection,
      doc,
      setDoc,
      getDoc,
      addDoc,
      query,
      where,
      orderBy,
      limit,
      onSnapshot,
      serverTimestamp
    }
  };
</script>
```

### Option B: Via npm (For production)

Firebase is already in `package.json`. You'll need to configure Shadow-CLJS to bundle it.

## Step 6: Initialize Firebase in App

Firebase will be initialized automatically when the app starts if config is provided.

The app will fall back to localStorage if Firebase config is not available.

## Step 7: Firestore Security Rules

Once you're ready for production, update Firestore security rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Players can read/write their own data
    match /players/{fid} {
      allow read: if request.auth != null && request.auth.uid == fid;
      allow write: if request.auth != null && request.auth.uid == fid;
    }
    
    // Anyone can read casts, authenticated users can write
    match /casts/{castId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Likes are public read, authenticated write
    match /likes/{likeId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Replies are public read, authenticated write
    match /replies/{replyId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Bangers are public read
    match /bangers/{bangerId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

## Step 8: Test Firebase Connection

1. Start the app: `npm run watch`
2. Open browser console
3. Look for: `✅ Firebase initialized successfully`
4. If you see `⚠️ Firebase config not provided`, check your environment variables

## Troubleshooting

### Firebase not initializing
- Check browser console for errors
- Verify Firebase config values are correct
- Ensure Firebase SDK is loaded before app initialization

### Firestore permission errors
- Check Firestore security rules
- Verify you're in test mode for development

### Module not found errors
- Ensure Firebase SDK is loaded via CDN or bundled correctly
- Check that Firebase v10.7.1+ is being used

## Next Steps

Once Firebase is set up:
1. Test saving/loading player data
2. Test cast creation and feed loading
3. Set up real-time listeners for feed updates
4. Configure security rules for production

