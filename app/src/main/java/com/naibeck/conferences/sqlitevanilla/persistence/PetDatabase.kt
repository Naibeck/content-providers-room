package com.naibeck.conferences.sqlitevanilla.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by Kevin Gomez on 4/9/2018.
 * Applaudo Studios
 */
@Database(entities = [Pet::class], version = 1)
abstract class PetDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao

    companion object {
        private var INSTANCE: PetDatabase? = null

        fun getInstance(context: Context): PetDatabase? {
            if (INSTANCE == null) {
                synchronized(PetDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            PetDatabase::class.java, "pets.db")
                            .build()
                }
            }

            return INSTANCE
        }

        fun destroyPetDatabaseInstance() {
            INSTANCE = null
        }
    }


}