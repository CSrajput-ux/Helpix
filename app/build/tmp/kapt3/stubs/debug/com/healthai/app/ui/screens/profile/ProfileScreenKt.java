package com.healthai.app.ui.screens.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000^\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\"\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u0006\u0010\u0007\u001a\b\u0010\b\u001a\u00020\u0001H\u0007\u001a\b\u0010\t\u001a\u00020\u0001H\u0007\u001a\b\u0010\n\u001a\u00020\u0001H\u0007\u001a,\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000e2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\u0010H\u0007\u001a\u0018\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0007\u001a\u0010\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0018H\u0007\u001a\u0010\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u0018H\u0007\u001a\u001a\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u001eH\u0007\u001a\u001e\u0010\u001f\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\u001c2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00010!H\u0007\u001a0\u0010\"\u001a\u00020\u00012\u0006\u0010#\u001a\u00020\u00032\u0006\u0010$\u001a\u00020%2\b\b\u0002\u0010&\u001a\u00020\'2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00010!H\u0007\u001a\u0010\u0010)\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\u001cH\u0007\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006*"}, d2 = {"Badge", "", "text", "", "color", "Landroidx/compose/ui/graphics/Color;", "Badge-4WTKRHQ", "(Ljava/lang/String;J)V", "BloodGroupDropdown", "DangerZone", "GenderDropdown", "InfoTextField", "label", "value", "Landroidx/compose/ui/text/input/TextFieldValue;", "onValueChange", "Lkotlin/Function1;", "LanguageSwitcher", "languageManager", "Lcom/healthai/app/services/LanguageManager;", "context", "Landroid/content/Context;", "PersonalInformationSection", "user", "Lcom/healthai/app/domain/model/User;", "ProfileHeaderCard", "ProfileScreen", "navController", "Landroidx/navigation/NavController;", "viewModel", "Lcom/healthai/app/ui/screens/profile/ProfileViewModel;", "ProfileTopHeader", "onLogout", "Lkotlin/Function0;", "SettingsCard", "title", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "modifier", "Landroidx/compose/ui/Modifier;", "onClick", "SettingsGrid", "app_debug"})
public final class ProfileScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ProfileScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.NotNull()
    com.healthai.app.ui.screens.profile.ProfileViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ProfileTopHeader(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLogout) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ProfileHeaderCard(@org.jetbrains.annotations.NotNull()
    com.healthai.app.domain.model.User user) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void LanguageSwitcher(@org.jetbrains.annotations.NotNull()
    com.healthai.app.services.LanguageManager languageManager, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PersonalInformationSection(@org.jetbrains.annotations.NotNull()
    com.healthai.app.domain.model.User user) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void InfoTextField(@org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.text.input.TextFieldValue value, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super androidx.compose.ui.text.input.TextFieldValue, kotlin.Unit> onValueChange) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void GenderDropdown() {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void BloodGroupDropdown() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SettingsGrid(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SettingsCard(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.graphics.vector.ImageVector icon, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DangerZone() {
    }
}