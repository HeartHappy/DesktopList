package com.hearthappy.desktoplist.desktopview

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport

/**
 * Created Date 2020/12/29.
 * @author ChenRui
 * ClassDescription:
 */
class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(applicationContext, "2be16d4c17", false);
    }
}