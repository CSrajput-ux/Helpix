# Fix Dagger/MissingBinding for HelpixRepository

The project is failing to build because `HelpixRepository` is being injected into `HealthVaultViewModel` (which is a `@HiltViewModel`), but Hilt doesn't know how to provide an instance of `HelpixRepository`.

## Proposed Changes

I will enable Hilt to provide `HelpixRepository` by adding an `@Inject` constructor to it and ensuring its dependencies (like `Context`) are correctly qualified with `@ApplicationContext`.

### Data Layer

#### [MODIFY] [HelpixRepository.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/data/remote/api/HelpixRepository.kt)
- Add `@Inject` to the constructor.
- Add `@ApplicationContext` to the `context` parameter.
- Import necessary Dagger/Hilt annotations (`javax.inject.Inject`, `dagger.hilt.android.qualifiers.ApplicationContext`).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:hiltJavaCompileDebug` to ensure the Dagger dependency graph is correctly resolved.

### Manual Verification
- None required as this is a compile-time DI issue.
