package com.naibeck.conferences.sqlitevanilla.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Kevin Gomez on 4/3/2018.
 * Applaudo Studios
 */
class PetDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "shelter.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(database: SQLiteDatabase?) {
        val createPetsTable = "CREATE TABLE ${PetContract.TABLE_NAME} (${PetContract.ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${PetContract.COLUMN_PET_NAME} TEXT NOT NULL, " +
                "${PetContract.COLUMN_PET_BREED} TEXT, " +
                "${PetContract.COLUMN_PET_GENDER} INTEGER NOT NULL, " +
                "${PetContract.COLUMN_PET_WEIGHT} INTEGER NOT NULL DEFAULT 0" +
                ");"

        database?.execSQL(createPetsTable)

    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Nothing to do since this is the only version.
    }

}