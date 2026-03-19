package com.healthai.app.ui.screens.hospitals;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\"\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u0006\u0010\u0007\u001a\u001e\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0007\u001a\u0010\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000fH\u0007\u001a&\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0002\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0016"}, d2 = {"Badge", "", "text", "", "color", "Landroidx/compose/ui/graphics/Color;", "Badge-4WTKRHQ", "(Ljava/lang/String;J)V", "HospitalListItem", "hospital", "Lcom/healthai/app/ui/screens/hospitals/Hospital;", "onClick", "Lkotlin/Function0;", "NearbyHospitalsScreen", "navController", "Landroidx/navigation/NavController;", "fetchLocationAndHospitals", "context", "Landroid/content/Context;", "fusedLocationClient", "Lcom/google/android/gms/location/FusedLocationProviderClient;", "onComplete", "app_debug"})
public final class NearbyHospitalsScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void NearbyHospitalsScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController) {
    }
    
    private static final void fetchLocationAndHospitals(android.content.Context context, com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient, kotlin.jvm.functions.Function0<kotlin.Unit> onComplete) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void HospitalListItem(@org.jetbrains.annotations.NotNull()
    com.healthai.app.ui.screens.hospitals.Hospital hospital, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}