package com.healthai.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000H\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u001a$\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\bH\u0007\u001a*\u0010\t\u001a\u00020\u00012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000b2\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\bH\u0007\u001a\b\u0010\f\u001a\u00020\u0001H\u0007\u001a0\u0010\r\u001a\u00020\u00012\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\u001c\u0010\u0010\u001a\u0018\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\b\u00a2\u0006\u0002\b\u0012\u00a2\u0006\u0002\b\u0013H\u0007\u001a.\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u0018H\u0007\u00a8\u0006\u0019"}, d2 = {"DeviceConnectScreen", "", "navController", "Landroidx/navigation/NavController;", "DeviceListItem", "device", "Lcom/healthai/app/ui/Device;", "onDeviceClick", "Lkotlin/Function1;", "DeviceSelectionScreen", "devices", "", "RadarScanningView", "VitalsCard", "modifier", "Landroidx/compose/ui/Modifier;", "content", "Landroidx/compose/foundation/layout/RowScope;", "Landroidx/compose/runtime/Composable;", "Lkotlin/ExtensionFunctionType;", "VitalsDashboard", "vitals", "Lcom/healthai/app/domain/model/VitalsLog;", "onDisconnect", "Lkotlin/Function0;", "app_debug"})
public final class DeviceConnectScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void DeviceConnectScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RadarScanningView() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DeviceSelectionScreen(@org.jetbrains.annotations.NotNull()
    java.util.List<com.healthai.app.ui.Device> devices, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.healthai.app.ui.Device, kotlin.Unit> onDeviceClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DeviceListItem(@org.jetbrains.annotations.NotNull()
    com.healthai.app.ui.Device device, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.healthai.app.ui.Device, kotlin.Unit> onDeviceClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void VitalsDashboard(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.NotNull()
    com.healthai.app.ui.Device device, @org.jetbrains.annotations.NotNull()
    com.healthai.app.domain.model.VitalsLog vitals, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDisconnect) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void VitalsCard(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super androidx.compose.foundation.layout.RowScope, kotlin.Unit> content) {
    }
}