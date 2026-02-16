package com.healthai.app.ui.viewmodel;

import com.healthai.app.domain.usecase.AnalyzeCoughUseCase;
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
public final class ScanViewModel_Factory implements Factory<ScanViewModel> {
  private final Provider<AnalyzeCoughUseCase> analyzeCoughUseCaseProvider;

  public ScanViewModel_Factory(Provider<AnalyzeCoughUseCase> analyzeCoughUseCaseProvider) {
    this.analyzeCoughUseCaseProvider = analyzeCoughUseCaseProvider;
  }

  @Override
  public ScanViewModel get() {
    return newInstance(analyzeCoughUseCaseProvider.get());
  }

  public static ScanViewModel_Factory create(
      Provider<AnalyzeCoughUseCase> analyzeCoughUseCaseProvider) {
    return new ScanViewModel_Factory(analyzeCoughUseCaseProvider);
  }

  public static ScanViewModel newInstance(AnalyzeCoughUseCase analyzeCoughUseCase) {
    return new ScanViewModel(analyzeCoughUseCase);
  }
}
