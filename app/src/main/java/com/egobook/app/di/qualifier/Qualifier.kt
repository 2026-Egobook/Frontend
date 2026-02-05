package com.egobook.app.di.qualifier

import jakarta.inject.Qualifier
// import javax.inject.Qualifier

@Qualifier
annotation class BackendApi

@Qualifier
annotation class AIApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit
