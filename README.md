# Chelas Poker Dice

**Chelas Poker Dice** is a modern two-player Poker Dice game running on a single Android device. Developed as the Practical Assignment (Option A) for the Mobile Devices Programming course (Winter Semester 2025/2026), this app brings the classic dice game to your mobile screen with a sleek interface and smart mechanics[cite: 3].

## Key Features

* **Game Modes:** Support for both Human vs Human and Human vs AI gameplay modes[cite: 3].
* **Smart AI Opponent:** The computer opponent evaluates current dice combinations and strategically decides which dice to hold or re-roll[cite: 2, 3].
* **Classic Rules:** Matches consist of an odd number of rounds where players get up to three rolls per turn, holding and releasing dice to build the strongest poker hand[cite: 3].
* **Persistent Statistics:** The app tracks and securely stores games played, victories, win ratios, and the frequency of each hand rank directly on the device using Android DataStore[cite: 2, 3].
* **Modern Interface:** A fully declarative UI built entirely with Jetpack Compose and Material 3, ensuring smooth state transitions and animations.

## Technologies & Architecture

This project strictly adheres to modern Android development standards:
* **Language:** Kotlin.
* **UI Toolkit:** Jetpack Compose (with Material 3 components).
* **Architecture:** MVVM (Model-View-ViewModel) leveraging `StateFlow` for reactive UI updates.
* **Navigation:** Jetpack Navigation Compose.
* **Local Storage:** Android DataStore Preferences[cite: 2].

## Hand Rankings

Dice combinations are ranked in descending order of strength[cite: 3]:
1. **Five of a Kind**
2. **Four of a Kind**
3. **Full House**
4. **Straight**
5. **Three of a Kind**
6. **Two Pair**
7. **One Pair**
8. **Bust** (Evaluated by the highest dice)

## How to Run the Project

1. **Clone the repository:**
```bash
git clone [https://github.com/YOUR-USERNAME/poker-dice.git](https://github.com/YOUR-USERNAME/poker-dice.git)
```
2. **Open the project:**
Open the cloned directory in Android Studio.

3. **Sync and Build:**
Allow Gradle to sync the project dependencies

4. **Run the App:**
Deploy the app to an Android Emulator or a physical Android device running API 24 or higher


## Note for evaluation: Watch our full application showcase, including match setup, gameplay, AI interaction, and statistics tracking here:


## Authors

Luka Roca
Tiago Estrela
Daniel Pereira

   
