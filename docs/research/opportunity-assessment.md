# Market & Opportunity Assessment (T-101)

Date: 2026-10-14 · Author: Claude · Status: draft
**[F]** fact with source · **[H]** hypothesis · **[A]** assumption used only for illustration.

## 1. Market facts
- India gaming: 591M gamers, 148M paying users, $3.8B market in 2024 → projected $9.2B by 2029; time on casual games +20% YoY; 47% of users are ad-tolerant [F1].
- Only ~25% of Indian gamers spent money in games; 83% prefer UPI/wallets [F1].
- Ad rates: rewarded video in the US averages ~$16.5 eCPM (Android); interstitial ~$10 (US Android). eCPMs vary 4–10× by region, and India/SEA are "significantly lower" [F2]. **We could not find a reliable India eCPM number** → must measure ourselves.

## 2. What this means for the ₹50k–1L/month goal [H]
Ad revenue per user depends heavily on *where* players are. An India-only audience makes the goal much harder.
**Recommendation: build English-first, "language-light" (mostly visual) puzzles so the game works in India AND in higher-paying markets (US/UK/EU) without heavy translation.**

Illustrative math (NOT a forecast — every number below is an assumption to be replaced with real data):
```
monthly ad revenue ≈ DAU × ad views per DAU per day × 30 × eCPM / 1000
[A] 4 ad views/day, blended eCPM $3  → ~$0.36 per DAU per month → ~2,500 DAU ≈ $900/month gross
[A] 4 ad views/day, blended eCPM $1  → ~$0.12 per DAU per month → ~7,500 DAU ≈ $900/month gross
```
So the business question is really: **can we reach and keep a few thousand daily players cheaply?** That depends on retention (D1/D7) and organic sharing, which is why the MVP must measure those.

## 3. Opportunities [H]
1. **Fair-ads brain game** — top complaint of the category is ads (see competitor-analysis.md).
2. **Never-ending content** — incumbents ship sequels because levels run out; procedural families + daily challenge fix that.
3. **Shareable daily challenge** — Wordle-style result card for WhatsApp/Instagram; cheap organic growth, strong in India.
4. **Android-native tricks** — tilt, shake, flip phone, rotate, volume, light sensor, charger. Novel "wow" moments that need no art.
5. Short-form video (Reels/Shorts) of "can you solve this?" levels is a natural, low-cost marketing format for this genre.

## 4. Threats
- Very crowded genre with 100M-install leaders.
- Low India eCPM.
- **Google Play rule:** new personal developer accounts must run a closed test with **12 testers for 14 continuous days** before public release [F3]. This affects the 15-day timeline (see MVP.md).

## Sources
- [F1] Lumikai India gaming report via GamesBeat — https://gamesbeat.com/indias-game-market-could-grow-from-3-8b-to-9-2b-by-2029-lumikai/
- [F2] Playio rewarded ad benchmarks 2026 (cites MAF, Adnimation) — https://blog.playio.co/rewarded-ad-benchmarks-2026
- [F3] Google Play Console Help, testing requirements — https://support.google.com/googleplay/android-developer/answer/14151465
