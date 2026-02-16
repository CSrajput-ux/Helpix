package com.healthai.app.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000>\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u001a\b\u0010\t\u001a\u00020\nH\u0007\u001a.\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\n0\u0012H\u0007\u001a\u0010\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u0015H\u0007\u001a\b\u0010\u0016\u001a\u00020\nH\u0007\u001a@\u0010\u0017\u001a\u00020\n2\u0006\u0010\u0018\u001a\u00020\u00192\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\n0\u001b2\u0006\u0010\u001c\u001a\u00020\r2\u0012\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\n0\u001bH\u0007\u001a&\u0010\u001e\u001a\u00020\n2\u0006\u0010\u001f\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\n0\u0012H\u0007\u001a$\u0010 \u001a\u00020\n2\u0006\u0010!\u001a\u00020\r2\u0012\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\n0\u001bH\u0007\"\u0013\u0010\u0000\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0002\u0010\u0003\"\u0013\u0010\u0005\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0006\u0010\u0003\"\u0013\u0010\u0007\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\b\u0010\u0003\u00a8\u0006#"}, d2 = {"DoctorThemeColor", "Landroidx/compose/ui/graphics/Color;", "getDoctorThemeColor", "()J", "J", "LightGrayBG", "getLightGrayBG", "TextGray", "getTextGray", "AboutDoctorSection", "", "DateSlotCard", "day", "", "date", "isSelected", "", "onClick", "Lkotlin/Function0;", "DoctorDetailsScreen", "navController", "Landroidx/navigation/NavController;", "DoctorProfileCard", "SchedulesSection", "selectedDateIndex", "", "onDateSelected", "Lkotlin/Function1;", "selectedTimeSlot", "onTimeSlotSelected", "TimeSlotChip", "time", "TimeSlotGrid", "selectedSlot", "onSlotSelected", "app_debug"})
public final class DoctorDetailsScreenKt {
    private static final long DoctorThemeColor = 0L;
    private static final long LightGrayBG = 0L;
    private static final long TextGray = 0L;
    
    public static final long getDoctorThemeColor() {
        return 0L;
    }
    
    public static final long getLightGrayBG() {
        return 0L;
    }
    
    public static final long getTextGray() {
        return 0L;
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void DoctorDetailsScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DoctorProfileCard() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void AboutDoctorSection() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SchedulesSection(int selectedDateIndex, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onDateSelected, @org.jetbrains.annotations.NotNull()
    java.lang.String selectedTimeSlot, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTimeSlotSelected) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DateSlotCard(@org.jetbrains.annotations.NotNull()
    java.lang.String day, @org.jetbrains.annotations.NotNull()
    java.lang.String date, boolean isSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.layout.ExperimentalLayoutApi.class})
    @androidx.compose.runtime.Composable()
    public static final void TimeSlotGrid(@org.jetbrains.annotations.NotNull()
    java.lang.String selectedSlot, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSlotSelected) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void TimeSlotChip(@org.jetbrains.annotations.NotNull()
    java.lang.String time, boolean isSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}