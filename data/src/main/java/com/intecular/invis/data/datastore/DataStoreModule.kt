package com.intecular.invis.data.datastore

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Singleton
    @Provides
    fun provideAead(application: Application): Aead {
        AeadConfig.register()
        return AndroidKeysetManager.Builder()
            .withSharedPref(application, KEYSET_NAME, PREFERENCE_FILE)
            .withKeyTemplate(KeyTemplates.get(KEY_TEMPLATES_NAME))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }

    @ExperimentalSerializationApi
    @Provides
    fun provideDataStore(application: Application, aead: Aead): DataStore<AppSettings> {
        return DataStoreFactory.create(
            produceFile = { File(application.filesDir, "datastore/$DATASTORE_FILE") },
            serializer = AppSettingsSerializer(aead)
        )
    }

    companion object {
        private const val KEY_TEMPLATES_NAME = "AES256_GCM"
        private const val KEYSET_NAME = "master_keyset"
        private const val PREFERENCE_FILE = "master_key_preference"
        private const val MASTER_KEY_URI = "android-keystore://master_key"
        private const val DATASTORE_FILE = "app-settings.json"
    }
}