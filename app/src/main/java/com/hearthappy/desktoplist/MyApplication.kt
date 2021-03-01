package com.hearthappy.desktoplist

import android.app.Application
import com.facebook.stetho.Stetho
import com.hearthappy.desktoplist.model.database.AbsDatabase
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.properties.Delegates

/**
 * Created Date 2020/12/29.
 * @author ChenRui
 * ClassDescription:
 */
class MyApplication:Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    var database: AbsDatabase by Delegates.notNull()
    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(applicationContext, "2be16d4c17", false)
        database=AbsDatabase.getDatabase(this,applicationScope)
        Stetho.initializeWithDefaults(this)
    }
}