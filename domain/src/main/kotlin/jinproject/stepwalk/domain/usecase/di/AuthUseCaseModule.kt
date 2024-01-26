package jinproject.stepwalk.domain.usecase.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.domain.usecase.auth.CheckIdUseCase
import jinproject.stepwalk.domain.usecase.auth.CheckIdUseCaseImpl
import jinproject.stepwalk.domain.usecase.auth.FindIdUseCase
import jinproject.stepwalk.domain.usecase.auth.FindIdUseCaseImpl
import jinproject.stepwalk.domain.usecase.auth.RequestEmailCodeUseCase
import jinproject.stepwalk.domain.usecase.auth.RequestEmailCodeUseCaseImpl
import jinproject.stepwalk.domain.usecase.auth.ResetPasswordUseCase
import jinproject.stepwalk.domain.usecase.auth.ResetPasswordUseCaseImpl
import jinproject.stepwalk.domain.usecase.auth.SignInUseCase
import jinproject.stepwalk.domain.usecase.auth.SignInUseCaseImpl
import jinproject.stepwalk.domain.usecase.auth.SignUpUseCase
import jinproject.stepwalk.domain.usecase.auth.SignUpUseCaseImpl
import jinproject.stepwalk.domain.usecase.auth.VerificationEmailCodeUseCase
import jinproject.stepwalk.domain.usecase.auth.VerificationEmailCodeUseCaseImpl
import jinproject.stepwalk.domain.usecase.auth.VerificationUserEmailUseCase
import jinproject.stepwalk.domain.usecase.auth.VerificationUserEmailUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindsCheckIdUseCase(checkIdUseCaseImpl: CheckIdUseCaseImpl) : CheckIdUseCase

    @Singleton
    @Binds
    abstract fun bindsFindIdUseCase(findIdUseCaseImpl: FindIdUseCaseImpl) : FindIdUseCase

    @Singleton
    @Binds
    abstract fun bindsRequestEmailCodeUseCase(requestEmailCodeUseCaseImpl: RequestEmailCodeUseCaseImpl) : RequestEmailCodeUseCase

    @Singleton
    @Binds
    abstract fun bindsResetPasswordUseCase(resetPasswordUseCaseImpl: ResetPasswordUseCaseImpl) : ResetPasswordUseCase

    @Singleton
    @Binds
    abstract fun bindsSignInUseCase(signInUseCaseImpl: SignInUseCaseImpl) : SignInUseCase

    @Singleton
    @Binds
    abstract fun bindsSignUpUseCase(signUpUseCaseImpl: SignUpUseCaseImpl) : SignUpUseCase

    @Singleton
    @Binds
    abstract fun bindsVerificationEmailCodeUseCase(verificationEmailCodeUseCaseImpl: VerificationEmailCodeUseCaseImpl) : VerificationEmailCodeUseCase

    @Singleton
    @Binds
    abstract fun bindsVerificationUserEmailUseCase(verificationUserEmailUseCaseImpl: VerificationUserEmailUseCaseImpl) : VerificationUserEmailUseCase

}