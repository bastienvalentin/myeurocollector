package fr.vbastien.mycoincollector.controller

import android.app.Application
import com.facebook.stetho.Stetho

/**
 * Created by vbastien on 10/08/2017.
 * The main application
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}