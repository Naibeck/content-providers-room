package com.naibeck.conferences.sqlitevanilla

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.naibeck.conferences.sqlitevanilla.data.PetContract

class CatalogActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        displayDatabaseInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val addPetFab = findViewById<FloatingActionButton>(R.id.fab)
        addPetFab.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        displayDatabaseInfo()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_insert_dummy_data -> {
            insertPet()
            displayDatabaseInfo()
            true
        }
        R.id.action_delete_all_entries -> true
        else -> super.onOptionsItemSelected(item)
    }

    private fun displayDatabaseInfo() {
        val projection = arrayOf(PetContract.ID, PetContract.COLUMN_PET_NAME, PetContract.COLUMN_PET_BREED, PetContract.COLUMN_PET_GENDER, PetContract.COLUMN_PET_WEIGHT)

        val cursor = contentResolver.query(PetContract.CONTENT_URI, projection, null, null, null)
        val displayView = findViewById<TextView>(R.id.text_view_pet)
        cursor.use { cursor ->
            displayView.text = "The pets table contains ${cursor.count} pets.\n\n"
            displayView.append("id - name - breed - weight")
            val idColumnIndex = cursor.getColumnIndex(PetContract.ID)
            val nameColumnIndex = cursor.getColumnIndex(PetContract.COLUMN_PET_NAME)
            val breedColumnIndex = cursor.getColumnIndex(PetContract.COLUMN_PET_BREED)
            val weightColumnIndex = cursor.getColumnIndex(PetContract.COLUMN_PET_WEIGHT)
            while (cursor.moveToNext()) {
                val currentId = cursor.getInt(idColumnIndex)
                val currentName = cursor.getString(nameColumnIndex)
                val currentBreed = cursor.getString(breedColumnIndex)
                val currentWeight = cursor.getString(weightColumnIndex)
                displayView.append("\n $currentId - $currentName - $currentBreed - $currentWeight")
            }
        }
    }

    private fun insertPet() {
        val values = ContentValues()
        values.put(PetContract.COLUMN_PET_NAME, "Chikuwa")
        values.put(PetContract.COLUMN_PET_BREED, "Chihuahua")
        values.put(PetContract.COLUMN_PET_GENDER, PetContract.GENDER_FEMALE)
        values.put(PetContract.COLUMN_PET_WEIGHT, 7)
        contentResolver.insert(PetContract.CONTENT_URI, values)
    }
}
