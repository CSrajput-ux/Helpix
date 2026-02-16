package com.healthai.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0007J\u0006\u0010\u001c\u001a\u00020\u001aJ\u0006\u0010\u001d\u001a\u00020\u001aJ\b\u0010\u001e\u001a\u00020\u001aH\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\n0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012R\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/healthai/app/ui/DeviceConnectViewModel;", "Landroidx/lifecycle/ViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_simulatedDevices", "", "Lcom/healthai/app/ui/Device;", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/healthai/app/ui/ConnectionState;", "_vitals", "Lcom/healthai/app/domain/model/VitalsLog;", "connectionManager", "Lcom/healthai/app/services/ConnectionManager;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "userRepository", "Lcom/healthai/app/data/repository/UserRepository;", "vitals", "getVitals", "vitalsJob", "Lkotlinx/coroutines/Job;", "connectToDevice", "", "device", "disconnect", "startScan", "startVitalsListener", "app_debug"})
public final class DeviceConnectViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.healthai.app.ui.ConnectionState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.healthai.app.ui.ConnectionState> state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.healthai.app.domain.model.VitalsLog> _vitals = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.healthai.app.domain.model.VitalsLog> vitals = null;
    @org.jetbrains.annotations.NotNull()
    private final com.healthai.app.services.ConnectionManager connectionManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.healthai.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job vitalsJob;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.healthai.app.ui.Device> _simulatedDevices = null;
    
    public DeviceConnectViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.healthai.app.ui.ConnectionState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.healthai.app.domain.model.VitalsLog> getVitals() {
        return null;
    }
    
    public final void startScan() {
    }
    
    public final void connectToDevice(@org.jetbrains.annotations.NotNull()
    com.healthai.app.ui.Device device) {
    }
    
    public final void disconnect() {
    }
    
    private final void startVitalsListener() {
    }
}