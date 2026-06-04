package com.middlespp.lockey

import android.app.Application
import com.middlespp.lockey.core.di.AppGraph
import com.middlespp.lockey.core.di.createAppGraph

class LockeyApp : Application() {

    lateinit var appGraph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        appGraph = createAppGraph(this)
    }
}
