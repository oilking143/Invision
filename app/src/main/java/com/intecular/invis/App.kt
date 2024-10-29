package com.intecular.invis

import android.app.Application
import com.intecular.invis.base.CrashReportingTree
import com.intecular.invis.data.SignInViewModelDelegate
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var signInViewModelDelegate: SignInViewModelDelegate

    override fun onCreate() {
        super.onCreate()

        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }
}