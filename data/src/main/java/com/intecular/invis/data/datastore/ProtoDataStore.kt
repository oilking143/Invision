package com.intecular.invis.data.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProtoDataStore @Inject constructor(private val protoDataStore: DataStore<AppSettings>) {

    suspend fun saveApiHeader(headerString: String) {
        protoDataStore.updateData { settings ->
            settings.copy(apiCustomHeader = headerString)
        }
    }

    fun getApiHeader(): Flow<String> {
        return protoDataStore.data.map { settings ->
            settings.apiCustomHeader
        }
    }

    suspend fun saveUserEmail(token: String) {
        protoDataStore.updateData { settings ->
            settings.copy(refreshToken = token)
        }
    }

    fun getUserEmail(): Flow<String> {
        return protoDataStore.data.map { settings ->
            settings.refreshToken
        }
    }
}