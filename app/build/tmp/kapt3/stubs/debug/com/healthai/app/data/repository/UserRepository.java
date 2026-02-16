package com.healthai.app.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0018\u0010\u0010\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0011\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0015H\u0086@\u00a2\u0006\u0002\u0010\u0016J\u000e\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\u0018J\u0018\u0010\u0019\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u001a\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\n0\u0015H\u0086@\u00a2\u0006\u0002\u0010\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/healthai/app/data/repository/UserRepository;", "", "()V", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "firestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "addVitalsLog", "", "vitalsLog", "Lcom/healthai/app/domain/model/VitalsLog;", "(Lcom/healthai/app/domain/model/VitalsLog;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createUser", "user", "Lcom/healthai/app/domain/model/User;", "(Lcom/healthai/app/domain/model/User;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDoctorById", "doctorId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDoctors", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatestVitalsStream", "Lkotlinx/coroutines/flow/Flow;", "getUserById", "userId", "getVitalsHistory", "app_debug"})
public final class UserRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore firestore = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    
    public UserRepository() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createUser(@org.jetbrains.annotations.NotNull()
    com.healthai.app.domain.model.User user, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addVitalsLog(@org.jetbrains.annotations.NotNull()
    com.healthai.app.domain.model.VitalsLog vitalsLog, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getVitalsHistory(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.healthai.app.domain.model.VitalsLog>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getDoctors(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.healthai.app.domain.model.User>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getDoctorById(@org.jetbrains.annotations.NotNull()
    java.lang.String doctorId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.healthai.app.domain.model.User> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getUserById(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.healthai.app.domain.model.User> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.healthai.app.domain.model.VitalsLog> getLatestVitalsStream() {
        return null;
    }
}