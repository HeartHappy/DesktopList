package com.hearthappy.model.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hearthappy.model.dao.DesktopDataDao
import com.hearthappy.model.table.DesktopDataTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created Date 2021/2/5.
 * @author ChenRui
 * ClassDescription:数据库
 */
@Database(entities = [DesktopDataTable::class], version = 2, exportSchema = false)
abstract class AbsDatabase : RoomDatabase() {
    abstract fun desktopDataDao(): DesktopDataDao


    companion object {

        @Volatile
        private var INSTANCE: AbsDatabase? = null
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AbsDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AbsDatabase::class.java,
                    "PSDatabase"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.desktopDataDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */
        suspend fun populateDatabase(desktopDataDao: DesktopDataDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            Log.d(TAG, "populateDatabase: ")
        }

        private const val TAG = "AbsDatabase"
    }
}