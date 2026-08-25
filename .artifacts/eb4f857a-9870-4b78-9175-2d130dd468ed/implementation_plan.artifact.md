# Implementation Plan - Differentiated Doctor Profile Experience

This plan refactors the Profile experience for Doctors, creating a clear distinction between their **Personal Identity** and their **Professional Identity**. It also makes the Doctor Details screen dynamic to show real information.

## User Review Required

> [!IMPORTANT]
> The `DoctorDetailsScreen` currently uses hardcoded data. I will modify it to accept a `doctorId` and fetch data from the repository, ensuring that when a patient clicks a doctor, they see the information the doctor has actually set up.

## Proposed Changes

### 1. Data Layer

#### [MODIFY] [HelpixApi.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/data/remote/api/HelpixApi.kt)
- Add a `bio` field to `UserProfile` and `UpdateProfileRequest` to allow doctors to describe their expertise.

#### [MODIFY] [UserRepository.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/data/repository/UserRepository.kt)
- Update `updateProfile` to include the `bio` field.

### 2. UI Layer - Profile Redesign

#### [MODIFY] [ProfileScreen.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/profile/ProfileScreen.kt)
- **Doctor Role Detected**: If the user is a doctor, the screen will switch to a "Professional Theme" (Emerald/Indigo).
- **Tabs/Sections**: Introduce two main sections for Doctors:
    - **Professional Identity**: Specialization, Experience, Bio, Clinic, Radius, and a **"Patient View Preview"**.
    - **Personal Account**: Name, Email, Age, Health Vault, and Logout.
- **Hide Patient Redundancy**: Hide the "Emergency Card" and "Switch to Doctor Hub" card for verified doctors.

### 3. UI Layer - Dynamic Doctor Details

#### [MODIFY] [DoctorDetailsScreen.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/DoctorDetailsScreen.kt)
- Refactor the screen to fetch doctor data by ID using the repository.
- Use `UserProfile` fields (bio, consultation_fee, location, specialization) instead of hardcoded strings.

## Verification Plan

### Automated Tests
- Build check: `./gradlew assembleDebug`.

### Manual Verification
- **Doctor Mode**: Login as doctor -> Verify the profile looks professional and has a "Bio" field.
- **Patient Mode**: Login as patient -> Go to "Book Appointment" -> Click a doctor -> Verify the `DoctorDetailsScreen` shows the info entered by that doctor.
