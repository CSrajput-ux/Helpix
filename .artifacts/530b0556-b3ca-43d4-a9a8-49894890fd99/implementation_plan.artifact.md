# Implementation Plan - Fix Dagger/MissingBinding for HelpixRepository

The project is failing to build because `HelpixRepository` is not properly integrated into the Hilt dependency injection graph. `HealthVaultViewModel` depends on it, but Hilt doesn't know how to provide an instance.

## Proposed Changes

### 1. Data Layer

#### [MODIFY] [HelpixRepository.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/data/remote/api/HelpixRepository.kt)
- Add `@Inject constructor(private val api: HelpixApi, @ApplicationContext private val context: Context)`.
- Remove the manual `api` instantiation via `lazy`.
- This allows Hilt to provide `HelpixRepository` using the `HelpixApi` already defined in `AppModule`.

### 2. Dependency Injection

#### [MODIFY] [AppModule.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/di/AppModule.kt)
- Add a `@Provides` method for `HelpixRepository` to ensure it's available as a singleton.
- This is cleaner than just relying on `@Inject` if we want to ensure it's a singleton and follows the project's existing pattern in `AppModule`.

### 3. ViewModels

#### [MODIFY] [ProfileViewModel.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/profile/ProfileViewModel.kt)
- Convert to `@HiltViewModel`.
- Change constructor to `@Inject constructor(private val repository: HelpixRepository, @ApplicationContext private val context: Context)`.
- Remove `AndroidViewModel` inheritance (use `ViewModel`).
- Replace `getApplication<Application>()` with the injected `context`.

### 4. UI Screens

#### [MODIFY] [ProfileScreen.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/profile/ProfileScreen.kt)
- Update `viewModel()` calls to `hiltViewModel()` to ensure Hilt handles the ViewModel creation.
- Add necessary imports.

#### [MODIFY] [EmergencyScreen.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/emergency/EmergencyScreen.kt)
- Update `viewModel()` calls to `hiltViewModel()`.
- Add necessary imports.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:hiltJavaCompileDebug` to verify that the Dagger/MissingBinding error is resolved.
- Run a full build: `./gradlew assembleDebug`.

### Manual Verification
- Deploy the app and verify that the Profile and Health Vault screens still function correctly.
- Verify that Login/Signup and File Uploads work, confirming the repository is correctly injected and functional.
