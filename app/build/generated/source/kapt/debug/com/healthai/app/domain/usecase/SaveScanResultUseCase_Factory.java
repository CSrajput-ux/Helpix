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
public final class SaveScanResultUseCase_Factory implements Factory<SaveScanResultUseCase> {
  private final Provider<HealthRepository> repositoryProvider;

  public SaveScanResultUseCase_Factory(Provider<HealthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SaveScanResultUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SaveScanResultUseCase_Factory create(
      Provider<HealthRepository> repositoryProvider) {
    return new SaveScanResultUseCase_Factory(repositoryProvider);
  }

  public static SaveScanResultUseCase newInstance(HealthRepository repository) {
    return new SaveScanResultUseCase(repository);
  }
}
