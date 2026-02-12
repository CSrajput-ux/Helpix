package com.healthai.app.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00040\u0003H\'J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0004H\u00a7@\u00a2\u0006\u0002\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/healthai/app/data/local/dao/HealthMetricDao;", "", "getLatestMetric", "Lkotlinx/coroutines/flow/Flow;", "Lcom/healthai/app/data/local/entity/HealthMetricEntity;", "insertMetric", "", "metric", "(Lcom/healthai/app/data/local/entity/HealthMetricEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface HealthMetricDao {
    
    @androidx.room.Query(value = "SELECT * FROM health_metrics ORDER BY timestamp DESC LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.healthai.app.data.local.entity.HealthMetricEntity> getLatestMetric();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertMetric(@org.jetbrains.annotations.NotNull()
    com.healthai.app.data.local.entity.HealthMetricEntity metric, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}