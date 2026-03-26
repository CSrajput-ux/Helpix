package com.healthai.app.data.remote.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ6\u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u000b0\u00032\u0014\b\u0001\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u000bH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u001a\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u001e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u00032\b\b\u0001\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016\u00a8\u0006\u0017"}, d2 = {"Lcom/healthai/app/data/remote/api/HealthApiService;", "", "analyzeScan", "Lretrofit2/Response;", "Lcom/healthai/app/data/remote/dto/ScanResultDto;", "image", "Lokhttp3/MultipartBody$Part;", "type", "", "(Lokhttp3/MultipartBody$Part;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "askAiDoctor", "", "message", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getVitalsHistory", "", "Lcom/healthai/app/domain/model/VitalsLog;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "syncVitals", "", "metrics", "Lcom/healthai/app/domain/model/HealthMetric;", "(Lcom/healthai/app/domain/model/HealthMetric;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface HealthApiService {
    
    @retrofit2.http.POST(value = "api/health/vitals")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object syncVitals(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.healthai.app.domain.model.HealthMetric metrics, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "api/health/history")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getVitalsHistory(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<com.healthai.app.domain.model.VitalsLog>>> $completion);
    
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "api/scans/analyze")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object analyzeScan(@retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part image, @retrofit2.http.Part(value = "type")
    @org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.healthai.app.data.remote.dto.ScanResultDto>> $completion);
    
    @retrofit2.http.POST(value = "api/chat/ask")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object askAiDoctor(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> message, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.Map<java.lang.String, java.lang.String>>> $completion);
}