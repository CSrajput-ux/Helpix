package com.healthai.app.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0016J\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\rR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/healthai/app/data/repository/HealthRepositoryImpl;", "Lcom/healthai/app/data/repository/HealthRepository;", "dao", "Lcom/healthai/app/data/local/dao/HealthMetricDao;", "api", "Lcom/healthai/app/data/remote/api/HealthApiService;", "(Lcom/healthai/app/data/local/dao/HealthMetricDao;Lcom/healthai/app/data/remote/api/HealthApiService;)V", "getLatestMetrics", "Lkotlinx/coroutines/flow/Flow;", "Lcom/healthai/app/domain/model/HealthMetric;", "saveMetric", "", "metric", "(Lcom/healthai/app/domain/model/HealthMetric;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class HealthRepositoryImpl implements com.healthai.app.data.repository.HealthRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.healthai.app.data.local.dao.HealthMetricDao dao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.healthai.app.data.remote.api.HealthApiService api = null;
    
    @javax.inject.Inject()
    public HealthRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.healthai.app.data.local.dao.HealthMetricDao dao, @org.jetbrains.annotations.NotNull()
    com.healthai.app.data.remote.api.HealthApiService api) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.healthai.app.domain.model.HealthMetric> getLatestMetrics() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object saveMetric(@org.jetbrains.annotations.NotNull()
    com.healthai.app.domain.model.HealthMetric metric, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}