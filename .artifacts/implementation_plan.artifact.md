# Replace Doctor Hub Profile Section with Financial Vault

This plan replaces the "Profile" quick action in the Doctor Dashboard with a "Vault" section focused on earnings, payments, and withdrawals.

## Proposed Changes

### 1. Navigation & Routing
- **[MODIFY] [NavRoutes.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/navigation/NavRoutes.kt)**: Rename `DoctorWallet` to `DoctorVault`.
- **[MODIFY] [AppNavGraph.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/navigation/AppNavGraph.kt)**: Update the route for the new Vault screen.

### 2. UI: Doctor Dashboard Update
- **[MODIFY] [DoctorDashboardScreen.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/doctor/DoctorDashboardScreen.kt)**:
    - Change the 4th item in the Quick Services grid from "Profile" to "Vault".
    - Update the icon to `Icons.Default.AccountBalanceWallet`.
    - Change the destination to `NavRoutes.DoctorVault`.

### 3. Feature: Financial Vault Screen
- **[NEW] [DoctorVaultScreen.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/doctor/DoctorVaultScreen.kt)** (Renamed and updated from `DoctorWalletScreen.kt`):
    - Title: "Doctor Financial Vault".
    - Display current earnings balance.
    - Add a "Withdraw Funds" button.
    - Show a list of recent payment transactions (consultation fees earned).
    - Add a "Vault Stats" section showing Total Earned vs. Withdrawn.

### 4. Logic: Vault Data Integration
- **[MODIFY] [DoctorDashboardViewModel.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/doctor/DoctorDashboardViewModel.kt)**:
    - Add states for `walletBalance` and `transactions`.
    - Implement `loadVaultData()` using `userRepository.getWalletSummary()`.
    - Implement `requestWithdrawal(amount)`.

## Verification Plan

### Automated Tests
- Build check: `./gradlew :app:compileDebugKotlin`.

### Manual Verification
1. Open the **Doctor Hub**.
2. Verify the 4th quick action is now labeled **Vault** with a wallet icon.
3. Click on **Vault**.
4. Verify you see the financial summary, transactions, and a withdrawal option.
5. Check if the **Profile Management** is still accessible via the top Practice Card (as it should be).
