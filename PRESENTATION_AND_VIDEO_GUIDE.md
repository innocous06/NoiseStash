# SoundDose — Pitch Deck & Video Production Guide

> **Submission Assets**: Presentation (PPT), Video Demo/Pitch (3–5 min), and Working Prototype (Android App).  
> **Product Positioning**: Consumer Hearing Wellness & Cumulative Noise Tracker. (100% consumer-facing; zero hackathon jargon).

---

## Part 1: Slide-by-Slide Pitch Deck (PPT Outline)

### Slide 1: Title & Hook
- **Slide Title**: SoundDose
- **Subtitle**: The World's First Cumulative Hearing Health & Noise Dose Tracker.
- **Visual**: High-resolution mockup of the SoundDose app on a phone screen emerging from a pitch-black background with a soft emerald/amber glowing dose ring.
- **One-Liner**: *"Every decibel app tells you it's loud right now. SoundDose tells you how much permanent hearing damage you've banked today."*
- **Presenter Script**:  
  *"Think about how you track your health. You track your steps, your sleep, your heart rate, your screen time. But what about the single most vulnerable, non-regenerative sensory organ in your body — your hearing? Today, over 1.5 billion people worldwide are at risk of irreversible hearing loss. We built SoundDose to make acoustic health visible, actionable, and personal."*

---

### Slide 2: The Invisible Crisis
- **Slide Title**: The Silent Epidemic
- **Data Points / Infographics**:
  - **63+ Million**: People in India alone suffering from significant hearing impairment.
  - **6 of 10**: The world's noisiest metropolitan areas are in developing urban economies.
  - **150+ Million**: Urban gig workers, delivery partners, and daily commuters exposed to 85–105 dB for 8–12 hours daily.
  - **Irreversible**: Hair cells in the inner ear (stereocilia) do not grow back once damaged.
- **Presenter Script**:  
  *"Noise pollution isn't just an annoyance — it's an invisible occupational and lifestyle hazard. Metro commuters, delivery riders, factory workers, concertgoers, and headphone users are exposed to dangerous sound levels daily without any awareness of when safe exposure turns into permanent cellular destruction."*

---

### Slide 3: The Fatal Gap in Existing Solutions
- **Slide Title**: Why Current Decibel Meters Fail
- **Comparison Visual**:
  - **Left (Traditional dB Meters)**: Shows instantaneous number (`88 dB`). Subtitle: *"Instantaneous snapshot. Doesn't tell you risk or duration."*
  - **Right (SoundDose)**: Shows Cumulative Daily Dose (`68% consumed • 1h 40m safe budget remaining`). Subtitle: *"Continuous biological dose tracking based on medical standards."*
- **Core Insight Callout**:  
  > *"Telling someone 'it is 90 dB right now' is like a thermometer telling you the weather is warm without telling you whether you have a fever. What actually causes hearing loss is cumulative energy exposure over time."*
- **Presenter Script**:  
  *"There are hundreds of sound meter apps on the Play Store. Every single one fails at the same fundamental hurdle: they show an instantaneous number that bounces up and down. But audiology and medical science prove that hearing loss is not caused by an isolated loud second — it is caused by the cumulative acoustic energy dose absorbed by your ear throughout the day."*

---

### Slide 4: The Science — NIOSH Standards in Your Pocket
- **Slide Title**: Clinical Precision Powered by the NIOSH Standard
- **The Formula**:
  $$T_{\text{allowed}} = \frac{8}{2^{(L - 85)/3}} \text{ hours}$$
- **Key Table / Visual**:
  - **85 dB**: 8 Hours Safe Limit (Heavy traffic hum)
  - **88 dB**: 4 Hours Safe Limit (Traffic + engine revving)
  - **91 dB**: 2 Hours Safe Limit (Motorcycle ride)
  - **97 dB**: 30 Minutes Safe Limit (Construction zone)
  - **100 dB**: 15 Minutes Safe Limit (Vehicle horn cluster)
  - **106 dB**: < 4 Minutes Safe Limit (Loud concert / loudspeaker)
- **Presenter Script**:  
  *"For decades, organizations like OSHA and NIOSH have protected industrial factory workers using strict mathematical exposure laws. Every 3 dB increase doubles the acoustic energy and halves the safe exposure duration. SoundDose brings this exact scientific standard out of specialized industrial sound laboratories and into the hands of everyday consumers."*

---

### Slide 5: The Product — SoundDose Consumer Experience
- **Slide Title**: Minimalist. Ambient. Intelligent.
- **Visuals (3-Screen Showcase)**:
  1. **Live Gauge**: Pitch-black canvas with a glowing, smudged circular dose gauge, instant real-time sound response, and safe listening budget remaining.
  2. **Today's Source Breakdown**: Donut chart attributing dose to specific acoustic sources (Traffic, Horns, Machinery, Audio).
  3. **7-Day Longitudinal Risk**: Clean trendline tracking cumulative daily hearing load across the week with recovery indicators.
- **Presenter Script**:  
  *"SoundDose is designed from the ground up for consumer simplicity. No industrial clutter, no calibration hurdles. Open the app: a fluid, glowing ring reveals your exact daily dose. If you're safe, it glows in serene emerald. As you enter traffic or loud zones, it shifts to amber and crimson, alerting you through subtle haptic pulses before your ears suffer damage."*

---

### Slide 6: Intelligent Source Attribution
- **Slide Title**: Understanding *What* Causes the Harm
- **Key Features**:
  - **On-Device Acoustic Intelligence**: Categorizes sound events in real time without recording your private conversations.
  - **100% Private & Offline**: Zero audio uploaded to cloud servers; all acoustic classification is processed locally.
  - **Actionable Consumer Byproducts**:
    - *"Vehicle horns accounted for 48% of your dose today in just 18 minutes."*
    - *"Your commute uses 65% of your safe daily allowance."*
- **Presenter Script**:  
  *"We don't just calculate how much acoustic load you've taken — we tell you what caused it. Our local acoustic engine differentiates between general ambient hum, sharp horn blasts, construction drills, and loud audio. It gives users actionable recommendations, such as wearing earplugs during commute hours or relocating workspace desks away from street-facing windows."*

---

### Slide 7: Real-World Consumer Use Cases
- **Slide Title**: Who Uses SoundDose?
- **3 User Personas**:
  1. **Urban Commuters & Gig Workers (150M+)**: Delivery partners, cab drivers, and commuters navigating high-traffic corridors who need to track occupational exposure.
  2. **Music Lovers & Fitness Enthusiasts**: Concertgoers, clubbers, festival attendees, and gym-goers monitoring headphone and venue sound levels.
  3. **Parents & Desk Workers**: Ensuring home study and office environments stay within the healthy <60 dB cognitive focus zone.
- **Presenter Script**:  
  *"SoundDose addresses massive, diverse consumer segments: from the gig economy worker spending 10 hours navigating street traffic, to the young adult attending weekend music festivals, to parents ensuring their children's headphone habits aren't permanently destroying their hearing before age 25."*

---

### Slide 8: Technical Architecture & Performance
- **Slide Title**: Native. Low-Power. Privacy-First.
- **Architecture Highlights**:
  - **Unprocessed Raw Acoustic Ingestion**: Bypasses phone AGC distortion to measure true sound pressure levels up to 110+ dB.
  - **Sub-100ms Fast Response**: Captures transient musical and acoustic spikes while running continuous equivalent Leq integration.
  - **Ultra-Low Battery Footprint**: Intelligent intermittent sampling and local room storage consuming less than 2% battery over a full workday.
  - **Zero Cloud Dependency**: Operates entirely offline with zero data transmission.
- **Presenter Script**:  
  *"Building an acoustic health monitor on mobile requires overcoming aggressive phone audio compression. SoundDose accesses the raw acoustic hardware layer, bypassing automatic gain compression to capture true physical pressure. It runs with zero internet dependency, maintaining complete user privacy while sipping minimal battery."*

---

### Slide 9: Vision & Roadmap
- **Slide Title**: The Future of Hearing Wellness
- **Roadmap Milestones**:
  - **Q1**: Smartwatch & Wearable Integration (Continuous wrist monitoring).
  - **Q2**: Bluetooth Headphone Hardware Integration (Real-time in-ear volume tracking).
  - **Q3**: Community Acoustic Mapping (Aggregated, privacy-preserved city sound pollution maps).
  - **Q4**: Corporate & Gig Platform Wellness API (Integration into delivery & transit apps for worker safety).
- **Presenter Script**:  
  *"We envision SoundDose becoming the universal standard for auditory health. From your phone to your smartwatch, to direct in-ear headphone integration, SoundDose is building the proactive shield that preserves hearing for the next generation."*

---

### Slide 10: Conclusion & Call to Action
- **Slide Title**: Protect the Senses That Connect Us.
- **Key Takeaway**: SoundDose bridges clinical audiology and consumer wellness into one fluid experience.
- **Contact / Team Info**: SoundDose Team.
- **Presenter Script**:  
  *"Vision loss separates people from things; hearing loss separates people from people. With SoundDose, hearing preservation is no longer invisible. Thank you."*

---

## Part 2: Video Pitch & Demo Production Guide (3 to 4 Minutes)

### Recommended Video Format
- **Total Duration**: 3:00 to 3:45 minutes.
- **Format**: Dynamic split-screen / screen mirror with a clean talking-head presenter or voiceover + direct on-phone footage.
- **Tone**: High-energy, sleek, modern consumer tech launch (think Apple Health / Nothing / Oura launch video).

---

### Scene-by-Scene Video Script & Storyboard

```
Timeline Overview:
00:00 - 00:35  [Hook] The Invisible Hearing Problem & The Flaw of Traditional Meters
00:35 - 01:25  [Live Demo Part 1] The SoundDose Interface & Real Sound Response
01:25 - 02:15  [Live Demo Part 2] "The Aha Moment" — Pulling Sound Away
02:15 - 02:50  [Deep Dive] Source Attribution & 7-Day History
02:50 - 03:30  [Impact & Closing] Privacy, Use Cases, and The Big Vision
```

#### Scene 1: The Hook (00:00 – 00:35)
- **Visual**: Presenter speaking to camera or b-roll of a bustling city / traffic intersection / loud headphones. Cut to a screen recording of a traditional sound meter bouncing between 80 and 90 dB.
- **Audio/Voiceover**:  
  *"Every day, millions of us step into urban traffic, subway stations, loud gym classes, or plug in headphones. We track our steps and heart rate, but have you ever wondered: how much hearing damage did your ears absorb today? Traditional decibel apps show you a number that bounces around, but they fail to answer the only question that matters: When is your hearing in danger?"*
- **Text Overlay**: *"Traditional dB meters show loudness. They don't track damage."*

#### Scene 2: Introducing SoundDose & Live Gauge (00:35 – 01:25)
- **Visual**: Close-up of phone running SoundDose. Studio lighting on dark background. The app is in quiet standby (38 dB, 0.0% dose, serene emerald ring).
- **Action**: Tap **"Start Monitoring"**. Speak normally. The visualizer smoothly breathes with voice volume.
- **Audio/Voiceover**:  
  *"Meet SoundDose. SoundDose is a consumer hearing wellness companion that calculates your cumulative noise dose using clinical NIOSH standards. Right now, in this quiet room, sound is around 40 dB. The ambient dose ring is serene green, and our safe budget is 100%."*

#### Scene 3: The Live Stress Test & "The Aha Moment" (01:25 – 02:15)
- **Visual**: Bring a second phone or speaker playing loud urban traffic/music near the microphone.
- **Action**:
  - The live meter shoots up to **88–95 dB SPL**.
  - The live waveform visualizer dances energetically with glowing amber/crimson bars.
  - The Dose Ring smoothly sweeps clockwise, climbing: `8%... 18%... 32%`.
  - The AI source chip identifies: *"Traffic & Transit"*.
  - **THE CRITICAL MOMENT**: Cut or mute the sound source instantly.
  - The meter immediately drops back down to 42 dB.
  - **Highlight the Dose Ring**: The Dose Ring **stays at 32%**.
- **Audio/Voiceover**:  
  *"Watch what happens when loud traffic sound begins. SoundDose reads the acoustic pressure up past 90 dB in real time. Notice how the dose ring begins accumulating. Now, watch closely: when the noise stops, the decibel meter drops back to quiet ambient... but the dose ring stays banked. Because in human biology, acoustic damage does not undo itself. SoundDose records the true energy debt your ears have taken."*
- **Text Overlay**: *"Decibels drop. Damage stays. That is cumulative dose."*

#### Scene 4: Source Attribution & 7-Day Insights (02:15 – 02:50)
- **Visual**: Tap the **Today** tab on the bottom bar.
  - Show the clean, borderless donut chart breaking down exposure (e.g. 55% Traffic, 28% Horns, 17% Ambient).
  - Tap the **History** tab.
  - Show the 7-day bar chart with the red dashed 100% NIOSH threshold. Show Wednesday's bar crossing into the red zone and Sunday's low rest day.
- **Audio/Voiceover**:  
  *"Switching to Today's Breakdown reveals our acoustic source attribution. We can clearly see that traffic and sudden horn blasts accounted for over 80% of today's risk. And in our 7-day longitudinal history, a delivery rider or commuter can instantly see their weekly exposure patterns — identifying which days exceeded the medical ceiling and when acoustic recovery occurred."*

#### Scene 5: Privacy, Tech Depth & Closing (02:50 – 03:30)
- **Visual**: Return to the main screen. Show the phone in hand, sleek, glowing in low light.
- **Audio/Voiceover**:  
  *"All of this processing runs 100% on-device. No audio is ever recorded, stored, or sent to the cloud. Your private conversations remain completely private. 63 million people in India already suffer from hearing loss. SoundDose makes sure the next generation doesn't. SoundDose: Protect your ears, every single day."*
- **End Card**: SoundDose logo, tagline, and download call-to-action.

---

## Part 3: Video Recording & Editing Recommendations

1. **Screen Capture + Phone Recording**:
   - Use high-quality screen recording on the phone at 60 fps.
   - For physical shots of the phone, use low ambient lighting with subtle colored backlights (deep blue/amber) to make the OLED black screen and glowing gauge pop.
2. **Audio Setup**:
   - Use a dedicated lapel or podcast microphone for the voiceover/presenter speech.
   - When demonstrating loud sound, use a secondary speaker (Bluetooth speaker or second phone) held 10–15 cm from the testing phone so the audience hears the audio switch clearly.
3. **Pacing & Text Graphics**:
   - Keep cuts snappy (every 4–6 seconds).
   - Use clean, minimal sans-serif callout text in the bottom third of the screen to emphasize key stats (*"NIOSH Clinical Standard"*, *"Bypasses Hardware Compression"*, *"100% On-Device & Private"*).
4. **Free Tools You Can Use**:
   - **Editing**: CapCut (Desktop/Mobile) or DaVinci Resolve.
   - **Screen Mirroring for Laptop**: Use Office Kit or Scrcpy for capturing crisp 1080p/60fps phone screen video directly onto your computer.
