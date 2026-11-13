# 📱 Helpix AI — Android App

> **Modern AI healthcare app built with 100% Jetpack Compose.**
> Part of the Helpix AI Platform — an intelligent, full-stack healthcare system.

[![Android](https://img.shields.io/badge/Android-API%2026+-3DDC84?style=flat-square&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.12-4285F4?style=flat-square&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![AGP](https://img.shields.io/badge/AGP-8.6.1-orange?style=flat-square)](https://developer.android.com/build)
[![TFLite](https://img.shields.io/badge/TensorFlow%20Lite-2.16.1-FF6F00?style=flat-square&logo=tensorflow)](https://tensorflow.org/lite)

---

## 📺 Demo

🎬 [**View Demo Video on Google Drive**](https://drive.google.com/file/d/1_iEXYnoIR3V-6Gl4gr-1QDDQGnT1YpZl/view?usp=sharing)

---

## ✨ Features

### 🧑‍🤝‍🧑 Dual User Portals
- Separate, secure registration and login flows for **Patients** and **Doctors**
- Google OAuth one-tap sign-in support
- Role-based navigation (Doctor Dashboard vs Patient Dashboard)

### 🤖 AI-Powered Diagnostics
- **Skin Scanner** — CameraX capture → on-device TFLite inference + cloud model
- **Cough TB Analyzer** — Audio recording → TB risk assessment
- **Prescription Reader** — Camera scan → OCR text extraction and AI analysis

### 📊 Health Monitoring
- **Vitals Sync** — Heart rate, blood pressure, SpO2, temperature tracking
- **Smartwatch Integration** — Real-time vitals from connected wearables
- **Health Vault** — AES-encrypted storage for lab reports, X-rays, prescriptions
- **Health Score** — Personalised wellness score based on vitals + history
- **Vaccinations** — Immunization history and schedule tracking

### 👨‍⚕️ Doctor–Patient Connection
- **Find Doctors** — GPS-based discovery within configurable radius
- **Book Appointments** — Full scheduling flow with date/time picker
- **My Appointments** — Patient and doctor appointment management
- **Doctor Dashboard** — Schedule, patient list, earnings summary

### 🛠 Smart Tools
- **Medicine Reminders** — Notification-based medicine scheduler
- **Emergency SOS** — One-tap GPS emergency alert

### 🌍 Accessibility & Localisation
- Full **English + Hindi (हिंदी)** support
- **Kids Mode** — Simplified UI for children's health tracking
- **Senior Mode** — Accessibility-first design for elderly users
- **Rural Mode** — Offline-capable simplified mode for low-connectivity areas

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────┐
│                    UI LAYER                          │
│         Jetpack Compose + Material 3                 │
│         (Screens, Composables, Theme)                │
└─────────────────┬───────────────────────────────────┘
                  │ StateFlow / UiState
┌─────────────────▼───────────────────────────────────┐
│               VIEWMODEL LAYER                        │
│     HiltViewModel + Kotlin Coroutines + Flows        │
└─────────────────┬───────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────┐
│              REPOSITORY LAYER                        │
│   UserRepository · AppointmentRepository · etc.     │
└────────┬────────────────────┬────────────────────────┘
         │                    │
┌────────▼──────┐    ┌────────▼──────────────┐
│  Remote API   │    │   Local DB (Room)      │
│  (Retrofit +  │    │   (offline caching)    │
│   OkHttp)     │    └───────────────────────┘
└────────┬──────┘
         │ JWT + HTTPS
┌────────▼──────────────────────────────────┐
│       Helpix FastAPI Backend              │
└───────────────────────────────────────────┘
```

**Pattern**: MVVM + Repository + Clean Architecture  
**DI**: Hilt (compile-time, zero reflection overhead)  
**Async**: Kotlin Coroutines + StateFlow + Flow

---

## 🛠 Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| Language | Kotlin | 2.1.0 |
| UI Toolkit | Jetpack Compose | BOM 2024.12.01 |
| Design | Material 3 | Latest via BOM |
| DI | Hilt (Dagger) | 2.51.1 |
| Navigation | Navigation Compose | 2.8.8 |
| Networking | Retrofit | 2.11.0 |
| HTTP Client | OkHttp | 4.12.0 |
| Local DB | Room | 2.6.1 |
| Camera | CameraX | 1.4.1 |
| On-Device ML | TensorFlow Lite | 2.16.1 |
| TFLite Support | TFLite Support Library | 0.4.4 |
| Auth | Firebase Auth | via BOM 34.11.0 |
| Image Loading | Coil Compose | 2.6.0 |
| Maps | Google Maps SDK | 19.0.0 |
| Location | Play Services Location | 21.3.0 |
| Places | Google Places API | 4.1.0 |
| Storage | DataStore Preferences | 1.1.2 |
| Biometric | AndroidX Biometric | 1.2.0-alpha05 |
| Health | Health Connect | 1.1.0-alpha11 |
| Annotation | KSP | 2.1.0-1.0.29 |
| Build | AGP | 8.6.1 |

---

## 🚀 Setup

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17**
- **Android SDK** API 35

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/CSrajput-ux/Helpix.git
cd Helpix
```

**2. Build and run**
```bash
# Debug build
./gradlew assembleDebug
```

---

> *Helpix AI — Aapki Sehat, Hamari Zimmedari* 🇮🇳
