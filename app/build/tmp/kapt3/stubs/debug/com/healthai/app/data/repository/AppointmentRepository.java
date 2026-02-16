package com.healthai.app.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\u0006\u0010\f\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\u0006\u0010\u0010\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u000eR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/healthai/app/data/repository/AppointmentRepository;", "", "()V", "firestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "createAppointment", "", "appointment", "Lcom/healthai/app/domain/model/Appointment;", "(Lcom/healthai/app/domain/model/Appointment;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAppointmentsForDoctor", "", "doctorId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAppointmentsForPatient", "patientId", "app_debug"})
public final class AppointmentRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore firestore = null;
    
    public AppointmentRepository() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createAppointment(@org.jetbrains.annotations.NotNull()
    com.healthai.app.domain.model.Appointment appointment, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAppointmentsForDoctor(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.healthai.app.domain.model.Appointment>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAppointmentsForPatient(@org.jetbrains.annotations.NotNull()
    java.lang.String patientId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.healthai.app.domain.model.Appointment>> $completion) {
        return null;
    }
}