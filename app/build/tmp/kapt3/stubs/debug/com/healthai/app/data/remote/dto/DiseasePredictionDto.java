package com.healthai.app.data.remote.dto;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\u0006\u0010\u0019\u001a\u00020\u001aJ\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u001c"}, d2 = {"Lcom/healthai/app/data/remote/dto/DiseasePredictionDto;", "", "diseaseName", "", "confidence", "", "severity", "advice", "(Ljava/lang/String;FLjava/lang/String;Ljava/lang/String;)V", "getAdvice", "()Ljava/lang/String;", "getConfidence", "()F", "getDiseaseName", "getSeverity", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toDomainModel", "Lcom/healthai/app/domain/model/DiseaseResult;", "toString", "app_debug"})
public final class DiseasePredictionDto {
    @com.google.gson.annotations.SerializedName(value = "disease_name")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String diseaseName = null;
    @com.google.gson.annotations.SerializedName(value = "confidence")
    private final float confidence = 0.0F;
    @com.google.gson.annotations.SerializedName(value = "severity_level")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String severity = null;
    @com.google.gson.annotations.SerializedName(value = "medical_advice")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String advice = null;
    
    public DiseasePredictionDto(@org.jetbrains.annotations.NotNull()
    java.lang.String diseaseName, float confidence, @org.jetbrains.annotations.NotNull()
    java.lang.String severity, @org.jetbrains.annotations.NotNull()
    java.lang.String advice) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDiseaseName() {
        return null;
    }
    
    public final float getConfidence() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSeverity() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAdvice() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.healthai.app.domain.model.DiseaseResult toDomainModel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final float component2() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.healthai.app.data.remote.dto.DiseasePredictionDto copy(@org.jetbrains.annotations.NotNull()
    java.lang.String diseaseName, float confidence, @org.jetbrains.annotations.NotNull()
    java.lang.String severity, @org.jetbrains.annotations.NotNull()
    java.lang.String advice) {
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