package com.healthai.app.ui.screens.doctor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bJ\b\u0010\u001c\u001a\u00020\u0019H\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0019\u0010\u0013\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/healthai/app/ui/screens/doctor/DoctorProfileViewModel;", "Landroidx/lifecycle/ViewModel;", "doctorId", "", "(Ljava/lang/String;)V", "_bookingConfirmed", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_doctor", "Lcom/healthai/app/domain/model/User;", "_isLoading", "appointmentRepository", "Lcom/healthai/app/data/repository/AppointmentRepository;", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "bookingConfirmed", "Lkotlinx/coroutines/flow/StateFlow;", "getBookingConfirmed", "()Lkotlinx/coroutines/flow/StateFlow;", "doctor", "getDoctor", "isLoading", "userRepository", "Lcom/healthai/app/data/repository/UserRepository;", "bookAppointment", "", "appointmentDate", "Ljava/util/Date;", "fetchDoctorDetails", "app_debug"})
public final class DoctorProfileViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String doctorId = null;
    @org.jetbrains.annotations.NotNull()
    private final com.healthai.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.healthai.app.data.repository.AppointmentRepository appointmentRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.healthai.app.domain.model.User> _doctor = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.healthai.app.domain.model.User> doctor = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _bookingConfirmed = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> bookingConfirmed = null;
    
    public DoctorProfileViewModel(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.healthai.app.domain.model.User> getDoctor() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getBookingConfirmed() {
        return null;
    }
    
    private final void fetchDoctorDetails() {
    }
    
    public final void bookAppointment(@org.jetbrains.annotations.NotNull()
    java.util.Date appointmentDate) {
    }
}