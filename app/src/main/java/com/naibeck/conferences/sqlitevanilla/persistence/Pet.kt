package com.naibeck.conferences.sqlitevanilla.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.ContentValues
import android.provider.BaseColumns

/**
 * Created by Kevin Gomez on 4/9/2018.
 * Applaudo Studios
 */
@Entity(tableName = Pet.TABLE_NAME)
data class Pet(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(index = true, name = Pet.ID)
        var id: Int = 0,
        @ColumnInfo(name = Pet.COLUMN_PET_NAME)
        var name: String? = "",
        @ColumnInfo(name = Pet.COLUMN_PET_BREED)
        var breed: String? = "",
        @ColumnInfo(name = Pet.COLUMN_PET_GENDER)
        var gender: Int? = Pet.GENDER_UNKNOWN,
        @ColumnInfo(name = Pet.COLUMN_PET_WEIGHT)
        var weight: Int? = 0
) {
    companion object : BaseColumns {

        /* Pet table schema */
        const val TABLE_NAME = "pets"
        const val ID = BaseColumns._ID
        const val COLUMN_PET_NAME = "name"
        const val COLUMN_PET_BREED = "breed"
        const val COLUMN_PET_GENDER = "gender"
        const val COLUMN_PET_WEIGHT = "weight"

        /* Possible genders for pets */
        const val GENDER_UNKNOWN: Int = 0
        const val GENDER_MALE: Int = 1
        const val GENDER_FEMALE: Int = 2

        /**
         * Will return a Pet instance using ContentValues
         */
        fun petFromContentValues(contentValues: ContentValues?): Pet {
            val name = contentValues?.getAsString(Pet.COLUMN_PET_NAME)
            val breed = contentValues?.getAsString(Pet.COLUMN_PET_BREED)
            val gender = contentValues?.getAsInteger(Pet.COLUMN_PET_GENDER)
            val weight = contentValues?.getAsInteger(Pet.COLUMN_PET_WEIGHT)

            return Pet(name = name, breed = breed, gender = gender, weight = weight)
        }
    }
}