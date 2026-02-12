package com.healthai.app.domain.usecase;

import com.healthai.app.data.repository.HealthRepository;
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
public final class GetHealthMetricsUseCase_Factory implements Factory<GetHealthMetricsUseCase> {
  private final Provider<HealthRepository> repositoryProvider;

  public GetHealthMetricsUseCase_Factory(Provider<HealthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetHealthMetricsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetHealthMetricsUseCase_Factory create(
      Provider<HealthRepository> repositoryProvider) {
    return new GetHealthMetricsUseCase_Factory(repositoryProvider);
  }

  public static GetHealthMetricsUseCase newInstance(HealthRepository repository) {
    return new GetHealthMetricsUseCase(repository);
  }
}
