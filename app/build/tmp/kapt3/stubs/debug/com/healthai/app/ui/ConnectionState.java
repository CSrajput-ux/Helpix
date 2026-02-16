package com.healthai.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0005\u0003\u0004\u0005\u0006\u0007B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0005\b\t\n\u000b\f\u00a8\u0006\r"}, d2 = {"Lcom/healthai/app/ui/ConnectionState;", "", "()V", "Connected", "Connecting", "DevicesFound", "Disconnected", "Scanning", "Lcom/healthai/app/ui/ConnectionState$Connected;", "Lcom/healthai/app/ui/ConnectionState$Connecting;", "Lcom/healthai/app/ui/ConnectionState$DevicesFound;", "Lcom/healthai/app/ui/ConnectionState$Disconnected;", "Lcom/healthai/app/ui/ConnectionState$Scanning;", "app_debug"})
public abstract class ConnectionState {
    
    private ConnectionState() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/healthai/app/ui/ConnectionState$Connected;", "Lcom/healthai/app/ui/ConnectionState;", "device", "Lcom/healthai/app/ui/Device;", "(Lcom/healthai/app/ui/Device;)V", "getDevice", "()Lcom/healthai/app/ui/Device;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class Connected extends com.healthai.app.ui.ConnectionState {
        @org.jetbrains.annotations.NotNull()
        private final com.healthai.app.ui.Device device = null;
        
        public Connected(@org.jetbrains.annotations.NotNull()
        com.healthai.app.ui.Device device) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.healthai.app.ui.Device getDevice() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.healthai.app.ui.Device component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.healthai.app.ui.ConnectionState.Connected copy(@org.jetbrains.annotations.NotNull()
        com.healthai.app.ui.Device device) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/healthai/app/ui/ConnectionState$Connecting;", "Lcom/healthai/app/ui/ConnectionState;", "device", "Lcom/healthai/app/ui/Device;", "(Lcom/healthai/app/ui/Device;)V", "getDevice", "()Lcom/healthai/app/ui/Device;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class Connecting extends com.healthai.app.ui.ConnectionState {
        @org.jetbrains.annotations.NotNull()
        private final com.healthai.app.ui.Device device = null;
        
        public Connecting(@org.jetbrains.annotations.NotNull()
        com.healthai.app.ui.Device device) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.healthai.app.ui.Device getDevice() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.healthai.app.ui.Device component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.healthai.app.ui.ConnectionState.Connecting copy(@org.jetbrains.annotations.NotNull()
        com.healthai.app.ui.Device device) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0013\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005J\u000f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u0019\u0010\t\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lcom/healthai/app/ui/ConnectionState$DevicesFound;", "Lcom/healthai/app/ui/ConnectionState;", "devices", "", "Lcom/healthai/app/ui/Device;", "(Ljava/util/List;)V", "getDevices", "()Ljava/util/List;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class DevicesFound extends com.healthai.app.ui.ConnectionState {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.healthai.app.ui.Device> devices = null;
        
        public DevicesFound(@org.jetbrains.annotations.NotNull()
        java.util.List<com.healthai.app.ui.Device> devices) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.healthai.app.ui.Device> getDevices() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.healthai.app.ui.Device> component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.healthai.app.ui.ConnectionState.DevicesFound copy(@org.jetbrains.annotations.NotNull()
        java.util.List<com.healthai.app.ui.Device> devices) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/healthai/app/ui/ConnectionState$Disconnected;", "Lcom/healthai/app/ui/ConnectionState;", "()V", "app_debug"})
    public static final class Disconnected extends com.healthai.app.ui.ConnectionState {
        @org.jetbrains.annotations.NotNull()
        public static final com.healthai.app.ui.ConnectionState.Disconnected INSTANCE = null;
        
        private Disconnected() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/healthai/app/ui/ConnectionState$Scanning;", "Lcom/healthai/app/ui/ConnectionState;", "()V", "app_debug"})
    public static final class Scanning extends com.healthai.app.ui.ConnectionState {
        @org.jetbrains.annotations.NotNull()
        public static final com.healthai.app.ui.ConnectionState.Scanning INSTANCE = null;
        
        private Scanning() {
        }
    }
}