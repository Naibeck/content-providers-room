package com.naibeck.conferences.sqlitevanilla.data

import android.provider.BaseColumns

/**
 * Created by Kevin Gomez on 4/3/2018.
 * Applaudo Studios
 */
class PetContract private constructor() {

    companion object PetEntry : BaseColumns {
        /* Pet table schema */
        const val TABLE_NAME = "pets"
        const val ID = BaseColumns._ID
        const val COLUMN_PET_NAME = "name"
        const val COLUMN_PET_BREED = "breed"
        const val COLUMN_PET_GENDER = "gender"
        const val COLUMN_PET_WEIGHT = "weight"

        /* Possible genders for pets */
        const val GENDER_UNKNOWN = 0
        const val GENDER_MALE = 1
        const val GENDER_FEMALE = 2
    }
}