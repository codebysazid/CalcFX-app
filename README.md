# CalcFX 📱

An advanced, minimalist, AMOLED-themed Scientific Calculator app for Android built with **Jetpack Compose** and **Material 3**.

Inspired by classic Casio FX-991EX scientific calculators, modern Android Material You aesthetics, and fluid gesture ergonomics.

---

## ✨ Features

- 🌑 **Minimalist AMOLED Black Theme**: High-contrast, pure black (`#000000`) background with floating squircle key islands and customizable accent colors.
- 🎨 **Dynamic Material You & Custom Accents**: Seamless integration with Android 12+ wallpaper dynamic theming + curated AMOLED accents (Cyan, Emerald, Orange, Violet, Rose, Amber, Blue).
- 🔢 **Full Casio FX Layout**: 
  - Complete 24 scientific functions (`CALC`, `∫dx`, `x⁻¹`, `log`, `ln`, `CONST`, `a b/c`, `√`, `x²`, `^`, `(-)`, `° ' "`, `hyp`, `sin`, `cos`, `tan`, `RCL`, `ENG`, `(`, `)`, `,`, `S<=>D`, `M+`, `Abs`).
  - Full numeric key block with SHIFT and ALPHA modifiers.
- 🧮 **Advanced Math Engine**:
  - **Natural Textbook Fraction Display**: Exact fractions (`a b/c`, `d/c`), mixed fractions, and root radical simplifications (`2√3`, `√2/2`).
  - **Calculus**: 5-point stencil numerical differentiation & Simpson's 1/3 rule numerical integration.
  - **Equation Solvers (EQN)**: 2x2 & 3x3 simultaneous linear equations, quadratic & cubic polynomial solvers (real and complex roots).
  - **Matrix Calculator (MATRIX)**: Inversion, determinant, addition, multiplication up to 4x4.
  - **Vector Math (VECTOR)**: Dot product, cross product, magnitude, unit vectors.
  - **Programmer Mode (BASE-N)**: Real-time synchronous base conversion (HEX, DEC, OCT, BIN) with bitwise operations (`NOT`, `SHL`, `SHR`).
  - **Statistics (STAT)**: 1-variable summary stats and 2-variable linear regression ($a + bx$, correlation $r$).
  - **Function Table Generator (TABLE)**: Generates $f(x)$ data tables with customizable range and step size.
  - **Scientific Constants & Metric Converter**: 40+ CODATA scientific constants and 40 metric unit conversions.
- 📜 **History Tape & Memory Registers**: Full calculation history tape with item recall and double-precision memory store (`M+`, `M-`, `RCL`, `STO`).
- 🧪 **Unit-Tested Math Engine**: 19+ automated JUnit unit tests verifying operator precedence, calculus, matrices, stats, and edge cases.

---

## 🛠 Tech Stack

- **Language**: Kotlin 2.0
- **UI Toolkit**: Jetpack Compose, Material 3
- **Architecture**: MVVM with Kotlin Coroutines & `StateFlow`
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 15 (API 35)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1+ or command-line Gradle
- JDK 17+
- Android SDK (API 35)

### Build and Run
```bash
# Clone the repository
git clone https://github.com/codebysazid/CalcFX-app.git
cd CalcFX-app

# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest

# Install to connected device
./gradlew installDebug
```

---

## 📄 License
MIT License. Open source and free to use.
