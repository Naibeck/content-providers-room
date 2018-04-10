package com.naibeck.conferences.sqlitevanilla.provider

import android.content.ContentProvider
import android.content.ContentProviderOperation
import android.content.ContentProviderResult
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.naibeck.conferences.sqlitevanilla.persistence.Pet
import com.naibeck.conferences.sqlitevanilla.persistence.PetDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Kevin Gomez on 4/9/2018.
 * Applaudo Studios
 */
class PetContentProvider : ContentProvider() {

    companion object {
        /**
         * Content authority for ContentProvider
         */
        const val CONTENT_AUTHORITY = "com.naibeck.conferences.pets"
        /**
         * Path for database
         */
        const val PETS_PATH = "pets"
        private val BASE_CONTENT_URI = Uri.parse("content://${PetContentProvider.CONTENT_AUTHORITY}")

        const val CONTENT_LIST_TYPE = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/${PetContentProvider.CONTENT_AUTHORITY}/$PETS_PATH"
        const val CONTENT_ITEM_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/${PetContentProvider.CONTENT_AUTHORITY}/$PETS_PATH"

        const val PETS = 100
        const val PETS_ID = 101

        /**
         * Content uri for pet authority
         */
        val CONTENT_URI: Uri? = Uri.withAppendedPath(PetContentProvider.BASE_CONTENT_URI, PetContentProvider.PETS_PATH)
    }

    private val petUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private val petDao by lazy {
        PetDatabase.getInstance(context)?.petDao()
    }

    init {
        petUriMatcher.addURI(PetContentProvider.CONTENT_AUTHORITY, PetContentProvider.PETS_PATH, PetContentProvider.PETS)
        petUriMatcher.addURI(PetContentProvider.CONTENT_AUTHORITY, PetContentProvider.PETS_PATH.plus("/#"), PetContentProvider.PETS_ID)
    }

    override fun onCreate(): Boolean = true

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val match = petUriMatcher.match(uri)
        val cursor = when (match) {
            PETS -> petDao?.selectPets()
            PETS_ID -> {
                val id = ContentUris.parseId(uri)
                petDao?.selectPetById(id)
            }
            else -> {
                throw IllegalArgumentException("Unknown URI: $uri")
            }
        }
        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri?): String? {
        val match = petUriMatcher.match(uri)
        return when (match) {
            PetContentProvider.PETS -> PetContentProvider.CONTENT_LIST_TYPE
            PetContentProvider.PETS_ID -> PetContentProvider.CONTENT_ITEM_TYPE
            else -> {
                throw IllegalArgumentException("Unknown URI $uri with match $match")
            }
        }
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        val match = petUriMatcher.match(uri)
        return when (match) {
            PETS -> {
                var rowInserted: Uri? = null
                doAsync {
                    val insertedUri = insertPet(uri, values)
                    uiThread {
                        rowInserted = insertedUri
                    }
                }
                rowInserted
            }
            else -> {
                throw IllegalArgumentException("Unknown URI $uri with match $match")
            }
        }
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val match = petUriMatcher.match(uri)
        return when (match) {
            PETS -> updatePet(uri, values)
            PETS_ID -> {
                updatePet(uri, values)
            }
            else -> {
                throw IllegalArgumentException("Unknown URI $uri with match $match")
            }
        }
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        val match = petUriMatcher.match(uri)
        val rowsDeleted = when (match) {
            PETS -> {
                var rows: Int? = null
                doAsync {
                    val result = petDao?.deleteAll()
                    uiThread {
                        rows = result
                    }
                }
                rows
            }
            PETS_ID -> {
                var rows: Int? = null
                doAsync {
                    val result = petDao?.deletePetById(ContentUris.parseId(uri))
                    uiThread {
                        rows = result
                    }
                }
                rows
            }
            else -> {
                throw IllegalArgumentException("Deletion is not supported for: $uri")
            }
        }

        if (rowsDeleted != 0) {
            context.contentResolver.notifyChange(uri, null)
        }

        return rowsDeleted ?: 0
    }

    private fun insertPet(uri: Uri?, values: ContentValues?): Uri? {
        val pet = Pet.petFromContentValues(values)
        var insertedId: Long? = null
        doAsync {
            val id = petDao?.insertPet(pet)
            uiThread {
                insertedId = id
            }
        }

        return when (insertedId) {
            0L -> {
                null
            }
            else -> {
                context?.contentResolver?.notifyChange(uri, null)
                ContentUris.withAppendedId(uri, insertedId!!)
            }
        }
    }

    private fun updatePet(uri: Uri?, values: ContentValues?): Int {
        val pet = Pet.petFromContentValues(values)
        pet.id = ContentUris.parseId(uri).toInt()
        if (values != null && values.size() == 0) {
            return 0
        }

        var rowsUpdated: Int? = null
        doAsync {
            val rows = petDao?.updatePet(pet)
            uiThread {
                rowsUpdated = rows
                context?.contentResolver?.notifyChange(uri, null)
            }
        }
        return rowsUpdated ?: 0
    }

    override fun applyBatch(operations: ArrayList<ContentProviderOperation>?): Array<ContentProviderResult?> {
        val context = context ?: return arrayOfNulls(0)
        val database = PetDatabase.getInstance(context)
        database?.beginTransaction()

        try {
            val result = super.applyBatch(operations)
            database?.setTransactionSuccessful()
            return result
        } finally {
            database?.endTransaction()
        }
    }
}