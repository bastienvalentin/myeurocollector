package fr.vbastien.mycoincollector.controller

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import io.fabric.sdk.android.Fabric

/**
 * Created by vbastien on 10/08/2017.
 * The main application
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Stetho.initializeWithDefaults(this)

    }
}