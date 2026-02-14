# Helpix - AI-Powered Healthcare Platform

Helpix is a futuristic, AI-powered mobile healthcare application built with modern Android development practices. It serves as a comprehensive platform for both patients and doctors, offering a wide range of features from real-time health monitoring to appointment booking.

## ✨ Features

- **Dual User Portals:** Separate, secure registration and login flows for both **Patients** and **Doctors**.
- **AI Health Scanner:** A central dashboard for initiating various health scans.
- **Smartwatch Integration:**
    - Real-time connection to smartwatches (simulated).
    - Live vitals dashboard displaying heart rate, SpO2, and more.
    - Persistent connection state across app sessions.
- **Appointment Booking System:**
    - Patients can browse a list of available doctors.
    - View detailed doctor profiles.
    - Book appointments with a date and time picker.
- **Doctor Dashboard:** A dedicated dashboard for doctors to view their upcoming appointments.
- **Patient Health Hub:**
    - **Vitals History:** View historical vitals data in a line chart.
    - **My Appointments:** Patients can see a list of their own scheduled appointments.
- **Diet Planner:** A feature to view daily meal plans, currently connected to a Firestore backend.
- **Multi-Language Support:** The app is fully internationalized to support both **English** and **Hindi**.
- **Location-Based Services:**
    - Find nearby hospitals and blood banks using Google Maps & Places API.

## 🛠️ Tech Stack & Architecture

- **UI:** 100% Jetpack Compose using Material 3 components for a modern and reactive user interface.
- **Architecture:** MVVM (Model-View-ViewModel) to ensure a clean separation of concerns and a scalable codebase.
- **Backend:** Serverless backend built entirely on **Google Firebase**.
    - **Database:** Cloud Firestore for scalable, real-time data storage (user profiles, vitals, appointments, etc.).
    - **Authentication:** Firebase Authentication for secure email/password login for both patients and doctors.
- **Asynchronous Programming:** Kotlin Coroutines and Flows are used extensively for managing background tasks and real-time data streams.
- **Dependency Injection:** Hilt for managing dependencies throughout the application.
- **Navigation:** Jetpack Navigation Compose for navigating between screens.

## 🚀 Setup Instructions

To build and run this project, you will need to set up a Firebase project.

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/CSrajput-ux/Helpix.git
    cd Helpix
    ```

2.  **Firebase Setup:**
    - Create a new project in the [Firebase Console](https://console.firebase.google.com/).
    - Add an Android app to your Firebase project with the package name `com.healthai.app`.
    - Download the `google-services.json` file and place it in the `app/` directory.
    - In the Firebase Console, enable the following services:
        - **Authentication:** Enable the "Email/Password" sign-in method.
        - **Firestore Database:** Create a new database.

3.  **Google Cloud (Maps) Setup:**
    - In the [Google Cloud Console](https://console.cloud.google.com/), select your Firebase project.
    - Enable the **Maps SDK for Android** and the **Places API**.
    - Create an API Key.
    - Open the `app/src/main/AndroidManifest.xml` file and replace `YOUR_GOOGLE_MAPS_API_KEY` with your actual API key.

4.  **Build and Run:**
    - Open the project in Android Studio.
    - Let Gradle sync the dependencies.
    - Build and run the app on an emulator or a physical device.

## 🤝 Contributions

Contributions, issues, and feature requests are welcome. Feel free to check the [issues page](https://github.com/CSrajput-ux/Helpix/issues) if you want to contribute.

---

This README file should give any developer a great starting point to understand and contribute to your project.
