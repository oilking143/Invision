package com.intecular.invis.data

import android.content.Context
import com.intecular.invis.base.di.ApplicationScope
import com.intecular.invis.data.datastore.ProtoDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SignInViewModelDelegate {
    val isUserSignedIn: StateFlow<Boolean>

    val isUserSignedInValue: Boolean

}

internal class SignInViewModelDelegateImpl @Inject constructor(
    val context: Context,
    val protoDataStore: ProtoDataStore,
    @ApplicationScope val applicationScope: CoroutineScope,
    ) : SignInViewModelDelegate {

    override val isUserSignedIn: StateFlow<Boolean> = protoDataStore.getUserEmail().map { it.isNotEmpty() }
            .stateIn(applicationScope, SharingStarted.Eagerly, false)
    override val isUserSignedInValue: Boolean
        get() = isUserSignedIn.value

        init {
            applicationScope.launch {
                isUserSignedIn.collect()
            }
        }

}