package com.healthai.app.ui.screens.health;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000L\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\u001a2\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\b\u0010\t\u001a\u0010\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\fH\u0007\u001a\u001a\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0007\u001a \u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u0015H\u0007\u001aB\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u001d\u0010\u001e\u001a:\u0010\u001f\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010 \u001a\u00020!2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u001cH\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\"\u0010#\u001a*\u0010$\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010%\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\u0007H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b&\u0010\'\u001a\u0018\u0010(\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010 \u001a\u00020!H\u0007\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006)"}, d2 = {"AlertCard", "", "title", "", "subtitle", "time", "color", "Landroidx/compose/ui/graphics/Color;", "AlertCard-g2O1Hgs", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V", "CircularScoreIndicator", "score", "", "HealthScreen", "navController", "Landroidx/navigation/NavController;", "viewModel", "Lcom/healthai/app/ui/viewmodel/HealthViewModel;", "MedicineCard", "name", "isTaken", "", "MetricCard", "status", "value", "progress", "", "modifier", "Landroidx/compose/ui/Modifier;", "MetricCard-Bx497Mc", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JFLandroidx/compose/ui/Modifier;)V", "RecommendationCard", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "RecommendationCard-42QJj7c", "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/graphics/vector/ImageVector;JLandroidx/compose/ui/Modifier;)V", "RiskItem", "percent", "RiskItem-mxwnekA", "(Ljava/lang/String;IJ)V", "SectionHeader", "app_debug"})
public final class HealthScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void HealthScreen(@org.jetbrains.annotations.NotNull()
    androidx.navigation.NavController navController, @org.jetbrains.annotations.NotNull()
    com.healthai.app.ui.viewmodel.HealthViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SectionHeader(@org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.graphics.vector.ImageVector icon) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void CircularScoreIndicator(int score) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void MedicineCard(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String time, boolean isTaken) {
    }
}