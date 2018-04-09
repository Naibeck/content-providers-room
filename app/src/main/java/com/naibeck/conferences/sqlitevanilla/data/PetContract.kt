package com.naibeck.conferences.sqlitevanilla.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by Kevin Gomez on 4/3/2018.
 * Applaudo Studios
 */
class PetContract private constructor() {

    companion object PetEntry : BaseColumns {
        const val CONTENT_AUTHORITY = "com.naibeck.conferences.pets"

        const val PETS_PATH = "pets"
        private val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

        const val CONTENT_LIST_TYPE = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/$CONTENT_AUTHORITY/$PETS_PATH"
        const val CONTENT_ITEM_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/$CONTENT_AUTHORITY/$PETS_PATH"

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
         * Content uri for pet authority
         */
        val CONTENT_URI: Uri? = Uri.withAppendedPath(BASE_CONTENT_URI, PETS_PATH)

        /**
         * Validate if current gender is a valid one
         */
        fun isValidGender(gender: Int): Boolean {
            return gender == PetEntry.GENDER_UNKNOWN || gender == PetEntry.GENDER_MALE || gender == PetEntry.GENDER_FEMALE
        }
    }
}