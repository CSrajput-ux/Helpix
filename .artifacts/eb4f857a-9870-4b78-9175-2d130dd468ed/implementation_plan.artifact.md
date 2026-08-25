# Implementation Plan - Fix Hilt Missing Binding for HelpixRepository

The build is failing because `HelpixRepository` is being injected into `HealthVaultViewModel`, but Hilt does not know how to provide an instance of `HelpixRepository`. Additionally, `ProfileViewModel` is manually instantiating `HelpixRepository`, which is not ideal in a Hilt-managed project.

## Proposed Changes

### 1. Data Layer

#### [MODIFY] [HelpixRepository.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/data/remote/api/HelpixRepository.kt)
- Add `@Singleton` and `@Inject constructor`.
- Inject `HelpixApi` directly instead of building it internally.
- Use `@ApplicationContext` for the `Context` dependency.
- Remove the lazy `api` property as it will be injected.

### 2. UI Layer

#### [MODIFY] [ProfileViewModel.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/profile/ProfileViewModel.kt)
- Annotate with `@HiltViewModel`.
- Inject `HelpixRepository` via constructor.
- Remove manual instantiation of `repository`.
- Change base class to `ViewModel` (Hilt handles `Application` injection via `@ApplicationContext` if needed, but here it was only used for the repository).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:hiltJavaCompileDebug` to ensure Hilt dependency graph is valid.
- Run a full build: `./gradlew assembleDebug`.

### Manual Verification
- Deploy the app and verify that the Profile and Health Vault screens still function correctly.
