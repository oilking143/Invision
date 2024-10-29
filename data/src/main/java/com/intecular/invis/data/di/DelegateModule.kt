package com.intecular.invis.data.di

import android.content.Context
import com.intecular.invis.base.di.ApplicationScope
import com.intecular.invis.data.ErrorHandlerDelegate
import com.intecular.invis.data.ErrorHandlerDelegateImpl
import com.intecular.invis.data.SignInViewModelDelegate
import com.intecular.invis.data.SignInViewModelDelegateImpl
import com.intecular.invis.data.datastore.ProtoDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DelegateModule {

    @Singleton
    @Provides
    fun provideErrorHandlerDelegate(@ApplicationContext context: Context): ErrorHandlerDelegate {
        return ErrorHandlerDelegateImpl(context)
    }


    @Singleton
    @Provides
    fun provideSignInViewModelDelegate(
        @ApplicationContext context: Context,
        protoDataStore: ProtoDataStore,
        @ApplicationScope applicationScope: CoroutineScope
    ): SignInViewModelDelegate {
        return SignInViewModelDelegateImpl(context, protoDataStore, applicationScope)
    }
}