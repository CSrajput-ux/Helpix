package com.healthai.app.ui.screens.emergency;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a.\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0015H\u0007\u001a\u0010\u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u0017\u001a\u00020\u0018H\u0007\u001a8\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u001b2\u000e\b\u0002\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0015H\u0007\u001a \u0010\u001d\u001a\u00020\u000e2\u0006\u0010\u001e\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u001bH\u0007\"\u0013\u0010\u0000\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0002\u0010\u0003\"\u0013\u0010\u0005\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0006\u0010\u0003\"\u0013\u0010\u0007\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\b\u0010\u0003\"\u0013\u0010\t\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\n\u0010\u0003\"\u0013\u0010\u000b\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\f\u0010\u0003\u00a8\u0006 "}, d2 = {"AlertRed", "Landroidx/compose/ui/graphics/Color;", "getAlertRed", "()J", "J", "CardRedBg", "getCardRedBg", "EmergencyBgBottom", "getEmergencyBgBottom", "EmergencyBgTop", "getEmergencyBgTop", "NeonRed", "getNeonRed", "ContactRow", "", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "title", "", "subtitle", "onCallClick", "Lkotlin/Function0;", "EmergencyScreen", "navController", "Landroidx/navigation/NavController;", "EmergencyStatCard", "modifier", "Landroidx/compose/ui/Modifier;", "onClick", "PersonalDetailBox", "label", "value", "app_debug"})
public final class EmergencyScreenKt {
    private static final long EmergencyBgTop = 0L;
    private static final long EmergencyBgBottom = 0L;
    private static final long AlertRed = 0L;
    private static final long CardRedBg = 0L;
    private static final long NeonRed = 0L;
    
    public static final long getEmergencyBgTop() {
        return 0L;
    }
    
    public static final long getEmergencyBgBottom() {
        return 0L;
    }
    
    public static final long getAlertRed() {
        return 0L;
    }
    
    public static final long getCardRedBg() {
        return 0L;
    }
    
    public static final long getNeonRed() {
        return 0L;
    }
    
    @androidx.compose.runtime.Composable()
    public static final void EmergencyScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PersonalDetailBox(@org.jetbrains.annotations.NotNull()
    java.lang.String label, @org.jetbrains.annotations.NotNull()
    java.lang.String value, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void EmergencyStatCard(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String subtitle, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.graphics.vector.ImageVector icon, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ContactRow(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.graphics.vector.ImageVector icon, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String subtitle, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCallClick) {
    }
}