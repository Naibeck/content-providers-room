package com.naibeck.conferences

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * Created by Kevin Gomez on 4/3/2018.
 * Applaudo Studios
 */
class VanillaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)){
            return
        }
        LeakCanary.install(this)
    }

}