package com.hearthappy

import android.app.Application
import com.hearthappy.model.database.AbsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.properties.Delegates

/**
 * Created Date 2020/12/29.
 * @author ChenRui
 * ClassDescription:
 */
open class DesktopApp:Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    var database: AbsDatabase by Delegates.notNull()
    override fun onCreate() {
        super.onCreate()
        database= AbsDatabase.getDatabase(this,applicationScope)
    }
}