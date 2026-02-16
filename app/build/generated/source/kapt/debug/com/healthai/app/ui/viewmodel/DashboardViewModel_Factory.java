package com.healthai.app.ui.viewmodel;

import com.healthai.app.domain.usecase.GetHealthMetricsUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<GetHealthMetricsUseCase> getHealthMetricsUseCaseProvider;

  public DashboardViewModel_Factory(
      Provider<GetHealthMetricsUseCase> getHealthMetricsUseCaseProvider) {
    this.getHealthMetricsUseCaseProvider = getHealthMetricsUseCaseProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(getHealthMetricsUseCaseProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<GetHealthMetricsUseCase> getHealthMetricsUseCaseProvider) {
    return new DashboardViewModel_Factory(getHealthMetricsUseCaseProvider);
  }

  public static DashboardViewModel newInstance(GetHealthMetricsUseCase getHealthMetricsUseCase) {
    return new DashboardViewModel(getHealthMetricsUseCase);
  }
}
