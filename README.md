# NoiseStash 🎧

> *"Hearing is non-renewable."*

**NoiseStash** is an intelligent, privacy-first personal hearing preservation platform for Android. It converts invisible, damaging environmental noise into an intuitive daily safe listening budget in real time.

---

## 🌟 Overview

Built for daily commuters, students, music lovers, and industrial workers navigating loud environments, **NoiseStash** shields human hearing before irreversible inner-ear stereocilia damage occurs. 

By capturing raw sound pressure levels directly from the device microphone—bypassing smartphone AGC distortion via an ultra-low-latency (<100ms) sampling engine—NoiseStash pairs real-time on-device acoustic classification with clinical NIOSH/WHO dosage models. All audio processing is conducted **100% offline and on-device**, ensuring complete user privacy while delivering actionable inner-ear recovery recommendations.

---

## 🚀 Key Features

### 1. Generative Particle Swarm Intro
- Atmospheric particle canvas that organically condenses into a crisp dot-matrix typography spelling **NoiseStash**, accompanied by the signature tagline *"hearing is non-renewable"*.
- Adapts dynamically to light and dark theme palettes.
- Replayable anytime via the Settings menu.

### 2. True SPL Engine & Hardware Calibration
- Bypasses hardware Automatic Gain Control (AGC) and noise cancellation filters to capture genuine physical Sound Pressure Levels (dB SPL).
- Sub-100ms peak detection window with fast attack and acoustic decay weighting.
- Hardware microphone decibel trimmer (`-6 dB` to `+6 dB`) accessible in top-right settings.

### 3. Centerpiece Hero Dose Ring & Safe Listening Budget
- Real-time NIOSH sound dose accumulation gauge with fluid radial track ticks and status glow.
- **Safe Listening Budget Left**: Dynamic countdown reflecting remaining exposure time at the current sound level.
- Interactive ceiling multiplier slider (`70%` to `130%`) allowing users to customize their daily noise budget based on audiological sensitivity.

### 4. Edge Acoustic Classification (Offline)
- Classifies ambient audio streams into actionable categories:
  - 🚗 **Vehicle Horns & Alarms**
  - 🚦 **Traffic Corridor**
  - 🏗️ **Machinery & Drills**
  - 🎵 **Headphone Audio / Media**
  - 🌿 **Ambient & Indoor Quiet**

### 5. Interactive Source Decomposition
- Segmented donut chart with tap-detectable slices.
- Tapping any slice highlights the segment with an expanded stroke and updates the center display to reveal that hazard's percentage and title in its distinct color.
- Synchronized with interactive monospace legend rows.
- Realistic, unique source breakdowns across Today, the 7-day week, and 30-day monthly timeline.

### 6. Battery-Style 24-Hour Timeline & GPS Hotspots
- Apple-style 24-hour cylinder pill bar chart showing hourly exposure distribution.
- GPS acoustic hotspot logging identifying geographic zones with hazardous peak decibels.
- Classified acoustic stream waveform visualization ("What made it loud?").

### 7. Dual-Section Onboarding & Audiology Profiling
- Streamlined setup asking for **Daily Acoustic Environment** and **Primary Occupation/Lifestyle**.
- Automatically configures personalized baseline thresholds and protective listening ceilings.

### 8. Peer Hearing League (Friends Circle)
- Gamified community sound conservation ranking: *Lowest Dose Wins*.
- Resilient streak badges and inner-ear rest nudges.

---

## 🛠️ Architecture & Tech Stack

- **UI Framework**: Modern Jetpack Compose with Material 3
- **Language**: Kotlin 2.0+ (100% Coroutines & StateFlow)
- **Audio Capture Engine**: Android `AudioRecord` with `MediaRecorder.AudioSource.UNPROCESSED` fallback
- **Acoustic Dosage Math**: NIOSH clinical standard (3 dB exchange rate above 85 dB SPL baseline)
- **Local Persistence**: Jetpack Room Database & Encrypted SharedPreferences
- **Design Language**: Custom smudged cards with theme-adaptive amber/charcoal color tokens

---

## 📱 Build & Installation

### Prerequisites
- Android Studio Ladybug / Meerkat or newer
- Android SDK 35 (Android 15)
- JDK 17+

### Build from Terminal
```bash
# Clone the repository
git clone https://github.com/innocous06/NoiseStash.git
cd NoiseStash

# Run unit tests
./gradlew testDebugUnitTest

# Assemble Release and Debug APKs
./gradlew assembleRelease assembleDebug
```

Compiled APK files will be generated at:
- `app/build/outputs/apk/release/app-release.apk`
- `app/build/outputs/apk/debug/app-debug.apk`

---

## 🔒 Privacy & Permissions

- **Microphone**: Used exclusively for real-time instantaneous SPL calculation and local classification. Audio buffers are discarded in memory after feature extraction and are **never recorded, stored, or transmitted**.
- **Location**: Used for coarse GPS exposure hotspot logging on-device only.
- **Offline Guarantee**: Zero cloud analytics, zero remote telemetry, 100% offline edge execution.

---

## 📄 License
MIT License. Developed for hearing preservation and acoustic wellness.
