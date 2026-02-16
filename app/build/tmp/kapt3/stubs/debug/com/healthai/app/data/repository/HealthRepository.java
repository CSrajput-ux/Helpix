package com.healthai.app.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H&J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0004H\u00a6@\u00a2\u0006\u0002\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/healthai/app/data/repository/HealthRepository;", "", "getLatestMetrics", "Lkotlinx/coroutines/flow/Flow;", "Lcom/healthai/app/domain/model/HealthMetric;", "saveMetric", "", "metric", "(Lcom/healthai/app/domain/model/HealthMetric;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface HealthRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.healthai.app.domain.model.HealthMetric> getLatestMetrics();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveMetric(@org.jetbrains.annotations.NotNull()
    com.healthai.app.domain.model.HealthMetric metric, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}