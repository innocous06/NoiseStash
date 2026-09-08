# NoiseStash

Hearing damage from environmental noise is cumulative and permanent. Most mobile sound meters simply report a momentary decibel number, leaving users to guess whether their exposure is actually dangerous over time.

NoiseStash is an Android application designed to track environmental sound exposure and calculate a real-time daily safe listening budget based on clinical NIOSH and WHO dosage standards. All audio processing and classification runs entirely on-device and offline.

---

## How It Works

### 1. Sound Pressure Level (SPL) Measurement
The app samples ambient audio using Android's `AudioRecord` API, requesting unprocessed audio where available to avoid distortion from device-level automatic gain control (AGC) or noise suppression. Peak and RMS values are computed in sub-100ms buffers and converted to decibels (dB SPL). A decibel trimmer in Settings allows users to calibrate readings against dedicated hardware meters when needed.

### 2. NIOSH Exposure Dose Model
Sound dosage is calculated using the National Institute for Occupational Safety and Health (NIOSH) standard:
- Baseline: 85 dBA for an 8-hour maximum permissible exposure.
- Exchange rate: 3 dB (every 3 dB increase halves allowable exposure time; for instance, 88 dB allows 4 hours, and 91 dB allows 2 hours).

The app continuously integrates sound levels over time to calculate:
- Cumulative daily dose percentage.
- Safe listening budget left (how long you can remain in the current sound environment before reaching 100% daily dose).
- Exposure ceiling multiplier (configurable from 70% to 130% for users with heightened sensitivity or differing clinical needs).

### 3. On-Device Sound Classification
NoiseStash classifies incoming sound into categories such as traffic corridors, vehicle horns and sirens, machinery/drills, headphone audio, and ambient quiet. Classification runs on a local model without transmitting any data over the network.

---

## Key Features

- **Live Exposure Dashboard**: Real-time sound level display paired with a circular dose gauge tracking daily noise consumption.
- **Dynamic Listening Budget**: Live countdown indicating how much safe listening time remains at current ambient noise levels.
- **Source Breakdown**: Donut chart and legend detailing what noise types contributed to exposure across daily, 7-day, and 30-day timelines. Tap any segment to inspect individual hazard percentages.
- **24-Hour Exposure Timeline**: Hourly bar charts illustrating when noise peaks occurred during the day.
- **Acoustic Hotspots**: Optional location logging to map recurring high-decibel areas.
- **Hardware Calibration**: Decibel trimmer (-6 dB to +6 dB) accessible directly in Settings.
- **Onboarding Assessment**: Simple questionnaire for environment and occupation to set appropriate default thresholds.
- **Theme Support**: Adaptive interface with light (amber) and dark (charcoal/silver) color palettes, along with a particle typography intro.

---

## Architecture & Dependencies

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose with Material 3
- **Asynchronous Execution**: Kotlin Coroutines and StateFlow
- **Audio Capture**: Android `AudioRecord` with `UNPROCESSED` audio source fallback
- **Local Storage**: Jetpack Room for time-series dose history and SharedPreferences for user settings
- **Minimum SDK**: Android 8.0 (API level 26)
- **Target SDK**: Android 15 (API level 35)

---

## Building from Source

### Prerequisites
- Android Studio Ladybug / Meerkat or command-line Android SDK tools
- Android SDK Platform 35
- JDK 17 or higher

### Build Commands
```bash
# Clone the repository
git clone https://github.com/innocous06/NoiseStash.git
cd NoiseStash

# Run unit tests
./gradlew testDebugUnitTest

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

Generated APKs will be located in:
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release.apk`

---

## Privacy

NoiseStash was built with an offline-first architecture:
- Audio buffers are analyzed in volatile memory to calculate sound pressure levels and discarded immediately.
- Audio is never recorded to disk, compressed into audio files, or transmitted over any network interface.
- No third-party tracking, crash analytics, or external telemetry libraries are included.
- Location access is used only locally to record coordinate stamps for sound peaks on your own device.

---

## License

This project is open source and available under the [MIT License](LICENSE).
