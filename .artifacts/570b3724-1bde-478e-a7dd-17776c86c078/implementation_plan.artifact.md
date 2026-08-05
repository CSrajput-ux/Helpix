# Implementation Plan - Modern App-Style Profile Redesign

Redesign the Profile section (specifically the unauthenticated state) to match the clean, card-based layout of modern apps like the one shown in the user's reference image, while maintaining our established Healthcare Dark Theme.

## User Review Required

> [!IMPORTANT]
> This redesign replaces the centered login form with a discovery-focused layout. The "Sign In" button will now open a bottom sheet or a toggleable section to enter credentials, keeping the main profile tab clean and informative even for guests.

## Proposed Changes

### UI Redesign - Profile Section

#### [MODIFY] [ProfileScreen.kt](file:///C:/Users/jkgga/Music/Helpix.ai/Helpix/app/src/main/java/com/healthai/app/ui/screens/profile/ProfileScreen.kt)
- **New Header Card**: Replace the centered icon/text with a horizontal card containing a profile placeholder, "Welcome to HELPiX" text, and a primary action "Sign In" button.
- **Hub Card**: Add a card for role switching ("Switch to Doctor/Patient Hub") with a descriptive icon and chevron.
- **Promo Card**: Implement the "Spread the Word" card using a vibrant gradient and a large action button.
- **Section Headers & Items**: Add standard section headers (e.g., "Health Tools", "General") and list items for settings/features.
- **Interactive Auth**: Clicking "Sign In" will now toggle the visibility of the login/register inputs within the header card or as a separate section.

### Components

- Create a reusable `ProfileOptionCard` component for the list items.
- Create a `FeaturePromoCard` for the colorful referral-style section.

## Verification Plan

### Automated Tests
- Run `:app:compileDebugKotlin` to ensure no UI-breaking changes.

### Manual Verification
- Open the app and navigate to the **Profile** tab.
- Verify the header card matches the reference layout.
- Click "Sign In" and ensure the authentication inputs appear correctly.
- Verify the "Switch Hub" and "Promo" cards are visually appealing on the dark background.
