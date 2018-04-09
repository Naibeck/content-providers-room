package com.naibeck.conferences.sqlitevanilla

import android.app.LoaderManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.naibeck.conferences.sqlitevanilla.data.PetContract

class CatalogActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    companion object {
        private const val PETS_CURSOR = 100
    }

    private lateinit var petCursorAdapter: PetCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        loaderManager.initLoader(PETS_CURSOR, null, this)

        val addPetFab = findViewById<FloatingActionButton>(R.id.fab)
        addPetFab.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        val petList = findViewById<ListView>(R.id.petList)
        val emptyView = findViewById<View>(R.id.empty_view)
        petCursorAdapter = PetCursorAdapter(this, null)
        petList.adapter = petCursorAdapter
        petList.emptyView = emptyView

        petList.onItemClickListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_insert_dummy_data -> {
            insertPet()
            true
        }
        R.id.action_delete_all_entries -> {
            deleteAllPets()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(loaderId: Int, bundle: Bundle?): Loader<Cursor> {
        val projection = arrayOf(PetContract.ID, PetContract.COLUMN_PET_NAME, PetContract.COLUMN_PET_BREED)
        return CursorLoader(this, PetContract.CONTENT_URI, projection, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, cursor: Cursor?) {
        petCursorAdapter.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        petCursorAdapter.swapCursor(null)
    }

    override fun onItemClick(adapterVieew: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val intent = Intent(this, EditorActivity::class.java)
        val currentPetUri = ContentUris.withAppendedId(PetContract.CONTENT_URI, id)
        intent.data = currentPetUri
        startActivity(intent)
    }

    private fun insertPet() {
        val values = ContentValues()
        values.put(PetContract.COLUMN_PET_NAME, "Chikuwa")
        values.put(PetContract.COLUMN_PET_BREED, "Chihuahua")
        values.put(PetContract.COLUMN_PET_GENDER, PetContract.GENDER_FEMALE)
        values.put(PetContract.COLUMN_PET_WEIGHT, 7)
        contentResolver.insert(PetContract.CONTENT_URI, values)
    }

    private fun deleteAllPets() {
        contentResolver.delete(PetContract.CONTENT_URI, null, null)
    }
}
