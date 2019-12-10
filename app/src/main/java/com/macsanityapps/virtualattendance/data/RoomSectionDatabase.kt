package com.macsanityapps.virtualattendance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE = "notes"

@Database(
    entities = [RoomSection::class],
    version = 1,
    exportSchema = false
)
abstract class RoomSectionDatabase : RoomDatabase() {

    abstract fun roomNoteDao(): SectionDao

    //code below courtesy of https://github.com/googlesamples/android-sunflower; it     is open
    //source just like this application.
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: RoomSectionDatabase? = null

        fun getInstance(context: Context): RoomSectionDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): RoomSectionDatabase {
            return Room.databaseBuilder(context, RoomSectionDatabase::class.java, DATABASE)
                .build()
        }
    }
}