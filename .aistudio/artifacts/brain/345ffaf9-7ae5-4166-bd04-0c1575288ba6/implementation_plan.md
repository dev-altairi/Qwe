# VoxFree: Production Bug Fixes & Device Hardening

Comprehensive resolution for the 3 identified production issues in VoxFree: cold-start persistence for custom WebSocket URLs and settings, explicit runtime permission flow with a dedicated permission rationale screen, and universal hardware adaptation with automatic OEM ROM audio routing fallbacks.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions have been aligned based on your preferences:
> - **Settings Persistence**: Unified `NetworkSettingsRepository` backed by Android `SharedPreferences`, automatically loaded before the first WebSocket connection is created on cold start.
> - **Runtime Permissions Flow**: Dedicated permission screen presented when microphone permission is not yet granted, explaining the voice communication requirement with one-tap "Grant Permission" and system app-settings link.
> - **Hardware & OEM Fallback**: `WebRtcAudioManager` hardened with try-catch validation for `MODE_IN_COMMUNICATION` with dynamic fallback to `MODE_IN_CALL` and routing tests across Samsung, Xiaomi (MIUI/HyperOS), Huawei (EMUI), and stock Android 8.0 through 15+.
> - **Notification Permissions**: Automatic runtime request for `android.permission.POST_NOTIFICATIONS` on Android 13+ (API 33+) to guarantee incoming call heads-up alerts.

---

### 1. Root Cause Analysis & Detailed Fix Strategy

#### Issue 1: Custom WebSocket URL and Settings Not Saved Across App Restarts
- **Root Cause**: `WebRtcSignalingClient` initialized its URL with a hardcoded default string (`wss://signaling.voxfree.org/webrtc`) in memory. When the user updated the URL in `NetworkScreen`, the state was kept only in the transient `SignalingClient` instance and was not saved to durable storage or reloaded on cold start.
- **Fix**:
  1. Create `data/repository/NetworkSettingsRepository.kt` managing:
     - `signalingUrl`: Defaulting to `wss://signaling.voxfree.org/webrtc`, saving changes immediately to `SharedPreferences`.
     - `audioRoutingMode`: "Auto", "Communication", "In-Call".
     - `stunServerUrl`: `stun:stun.l.google.com:19302`.
  2. In `VoxFreeViewModel`, read `NetworkSettingsRepository.getSignalingUrl()` upon initialization and pass it to `WebRtcSignalingClient` before invoking `connect()`.

#### Issue 2: Comprehensive Runtime Permissions Handling
- **Root Cause**: Permissions were requested only on the dial button click, which could leave users stranded if denied, or cause incoming call ringtone alerts to fail on Android 13+ due to missing `POST_NOTIFICATIONS`.
- **Fix**:
  1. Add `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />` in `AndroidManifest.xml`.
  2. Create `ui/components/PermissionRationaleScreen.kt`:
     - Clean, focused interface displayed when `RECORD_AUDIO` is missing.
     - Explains that VoxFree is a P2P voice calling app requiring microphone hardware access for Opus 48 kHz voice transmission.
     - Provides a prominent "Grant Permission" button triggering `rememberLauncherForActivityResult(RequestMultiplePermissions())`.
     - Provides a fallback button to open Android System App Settings if permanently denied ("Don't ask again").
  3. Seamlessly unblocks the full application interface as soon as permissions are granted.

#### Issue 3: Universal Device Compatibility & Hardware Adaptation
- **Root Cause**: Some OEM vendor Android ROMs (particularly Xiaomi MIUI/HyperOS, Huawei EMUI, and certain Samsung Exynos devices) exhibit bugs where `AudioManager.MODE_IN_COMMUNICATION` either fails silently or fails to route speakerphone streams properly without first checking `isSpeakerphoneOn`.
- **Fix**:
  1. In `WebRtcAudioManager.kt`:
     - Wrap mode assignment in a resilient try/catch ladder: attempt `AudioManager.MODE_IN_COMMUNICATION`. If `audioManager.mode` does not reflect the change or throws an exception, fall back to `AudioManager.MODE_IN_CALL`.
     - In speakerphone toggling, verify both `am.isSpeakerphoneOn = enable` and apply audio routing flags.
     - Support Android 8.0 (API 26) through Android 15+ with proper `AudioAttributes` for voice communication focus.
  2. In `WebRtcManager.kt`:
     - Ensure audio-only WebRTC initialization has zero dependency on video decoder hardware wrappers.

---

### 2. User Experience & Visual Design

#### Permission Rationale Screen
- High-contrast visual identity matching VoxFree's dark obsidian theme (`#0B0F19`).
- Centered glowing microphone badge (`#10B981` Emerald).
- Clear, plain-language description: *"Microphone access is required to transmit your voice directly to your peers over encrypted WebRTC media streams."*
- Primary Action: **Grant Microphone Access** button.
- Secondary Action: **Open App Settings** (for permanently denied permissions).

#### Network Settings Persistence
- Saved WebSocket server URL persists across process kills and reboots.
- Instant "LAN Preset" button (`ws://192.168.1.100:8080/ws`) and "Cloud Preset" button (`wss://signaling.voxfree.org/webrtc`) with automatic local persistence.
- Immediate feedback toast: *"Signaling URL saved and reconnected"*.

---

### 3. Technical Architecture & File Modifications

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Application Startup                             │
│                                                                        │
│   1. NetworkSettingsRepository (SharedPreferences)                     │
│      └── Loads saved WebSocket URL (e.g. ws://192.168.1.100:8080)      │
│                                                                        │
│   2. Permission Check (RECORD_AUDIO & POST_NOTIFICATIONS)              │
│      ├── Missing  ──► Show PermissionRationaleScreen                   │
│      └── Granted  ──► Show VoxFreeApp (Dialer, Contacts, etc.)         │
│                                                                        │
│   3. WebRtcSignalingClient                                             │
│      └── Connects with persisted URL + 15s keepalive                   │
│                                                                        │
│   4. WebRtcAudioManager                                                │
│      └── MODE_IN_COMMUNICATION with automatic MODE_IN_CALL fallback    │
└────────────────────────────────────────────────────────────────────────┘
```

#### Detailed File Action Plan
1. **`data/repository/NetworkSettingsRepository.kt`** (NEW):
   - Encapsulates `SharedPreferences` for WebSocket URL, STUN server, and audio routing modes with StateFlow updates.
2. **`ui/components/PermissionRationaleScreen.kt`** (NEW):
   - M3 permission onboarding screen with rationale and settings deep link.
3. **`telephony/webrtc/WebRtcAudioManager.kt`** (UPDATE):
   - Resilient audio mode initialization (`MODE_IN_COMMUNICATION` -> `MODE_IN_CALL` fallback).
   - Audio focus handling compatible across API 26 to API 36.
4. **`telephony/webrtc/WebRtcSignalingClient.kt`** (UPDATE):
   - Accept initial URL loaded from `NetworkSettingsRepository`.
5. **`ui/screens/NetworkScreen.kt`** (UPDATE):
   - Ensure saving edits immediately updates `NetworkSettingsRepository`.
6. **`ui/VoxFreeViewModel.kt`** (UPDATE):
   - Inject `NetworkSettingsRepository`, initialize signaling with saved URL.
7. **`MainActivity.kt`** (UPDATE):
   - Check permissions at launch and show `PermissionRationaleScreen` if mic permission is missing.
