package com.naibeck.conferences.sqlitevanilla.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log

/**
 * Created by Kevin Gomez on 4/9/2018.
 * Applaudo Studios
 */
class PetProvider : ContentProvider() {

    companion object {
        const val PETS = 100
        const val PETS_ID = 101
        const val TAG = "pet.provider"
    }

    private val petUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        petUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PETS_PATH, PETS)
        petUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PETS_PATH.plus("/#"), PETS_ID)
    }

    private val petDBHelper by lazy {
        PetDBHelper(context)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        val match = petUriMatcher.match(uri)
        return when (match) {
            PETS -> {
                insertPet(uri, values)
            }
            else -> null

        }
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val sqLiteDatabase = petDBHelper.readableDatabase
        val match = petUriMatcher.match(uri)
        var cursor = when (match) {
            PETS -> {
                sqLiteDatabase.query(PetContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            PETS_ID -> {
                var selection = PetContract.ID.plus("=?")
                var selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                sqLiteDatabase.query(PetContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            else -> {
                throw IllegalArgumentException("Cannot query unknown URI: $uri")
                null
            }
        }

        cursor?.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val match = petUriMatcher.match(uri)
        return when (match) {
            PETS -> updatePet(uri, values, selection, selectionArgs)
            PETS_ID -> {
                val selection = PetContract.ID.plus("=?")
                val selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                updatePet(uri, values, selection, selectionArgs)
            }
            else -> {
                throw IllegalArgumentException("Update is not supported for URI: $uri")
                0
            }

        }
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        val match = petUriMatcher.match(uri)
        val database = petDBHelper.writableDatabase
        val rowsDeleted = when (match) {
            PETS -> {
                database.delete(PetContract.TABLE_NAME, selection, selectionArgs)
            }
            PETS_ID -> {
                val selection = PetContract.ID.plus("=?")
                val selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                database.delete(PetContract.TABLE_NAME, selection, selectionArgs)
            }
            else -> {
                throw IllegalArgumentException("Deletion is not supported for: $uri")
                0
            }
        }

        if (rowsDeleted != 0) {
            context.contentResolver.notifyChange(uri, null)
        }

        return rowsDeleted
    }

    override fun getType(uri: Uri?): String? {
        val match = petUriMatcher.match(uri)
        return when (match) {
            PETS -> PetContract.CONTENT_LIST_TYPE
            PETS_ID -> PetContract.CONTENT_ITEM_TYPE
            else -> {
                throw IllegalArgumentException("Unknown URI $uri with match $match")
                null
            }
        }
    }

    private fun insertPet(uri: Uri?, values: ContentValues?): Uri? {
        val database = petDBHelper.writableDatabase
        val id = database.insert(PetContract.TABLE_NAME, null, values)
        val name = values?.getAsString(PetContract.COLUMN_PET_NAME)
        val gender = values?.getAsInteger(PetContract.COLUMN_PET_GENDER)
        val weight = values?.getAsInteger(PetContract.COLUMN_PET_WEIGHT)

        validateValuesEntries(name, gender, weight)

        return when (id) {
            -1L -> {
                //Something went wrong
                Log.e(TAG, "Failed to insert row for $uri")
                null
            }
            else -> {
                context.contentResolver.notifyChange(uri, null)
                ContentUris.withAppendedId(uri, id)
            }
        }
    }

    private fun updatePet(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val name = values?.getAsString(PetContract.COLUMN_PET_NAME)
        val gender = values?.getAsInteger(PetContract.COLUMN_PET_GENDER)
        val weight = values?.getAsInteger(PetContract.COLUMN_PET_WEIGHT)

        validateValuesEntries(name, gender, weight)

        if (values != null && values?.size() == 0) {
            return 0
        }

        val database = petDBHelper.writableDatabase
        val rowsUpdated = database.update(PetContract.TABLE_NAME, values, selection, selectionArgs)

        if (rowsUpdated != 0) {
            context.contentResolver.notifyChange(uri, null)
        }

        return rowsUpdated
    }

    private fun validateValuesEntries(name: String?, gender: Int?, weight: Int?) {
        if (name == null || name?.isEmpty()) {
            throw IllegalArgumentException("Pet name must be provided")
        }

        if (gender == null || !PetContract.isValidGender(gender)) {
            throw IllegalArgumentException("Pet gender must be valid")
        }

        if (weight == null || weight < 0) {
            throw IllegalArgumentException("Pet weight must be valid")
        }
    }
}