package com.naibeck.conferences.sqlitevanilla

import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.naibeck.conferences.sqlitevanilla.data.PetContract
import com.naibeck.conferences.sqlitevanilla.data.PetDBHelper

/**
 * Created by Kevin Gomez on 4/3/2018.
 * Applaudo Studios
 */
class EditorActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var breedEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var genderSpinner: Spinner

    private var gender = PetContract.GENDER_UNKNOWN

    private val dbHelper by lazy {
        PetDBHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        nameEditText = findViewById(R.id.edit_pet_name)
        breedEditText = findViewById(R.id.edit_pet_breed)
        weightEditText = findViewById(R.id.edit_pet_weight)
        genderSpinner = findViewById(R.id.spinner_gender)

        setupSpinner()
    }

    private fun setupSpinner() {
        val genderSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender_options, android.R.layout.simple_spinner_item)
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        genderSpinner.adapter = genderSpinnerAdapter
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                gender = PetContract.GENDER_UNKNOWN
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selection = parent?.getItemAtPosition(position) as String
                if (selection.isNotEmpty()) {
                    gender = when (selection) {
                        getString(R.string.gender_male) -> PetContract.GENDER_MALE
                        getString(R.string.gender_female) -> PetContract.GENDER_FEMALE
                        else -> PetContract.GENDER_UNKNOWN
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_save -> {
            insertPet()
            finish()
            true
        }
        R.id.action_delete -> true
        android.R.id.home -> {
            NavUtils.navigateUpFromSameTask(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun insertPet() {
        val name = nameEditText.text.toString().trim()
        val breed = breedEditText.text.toString().trim()
        val weight = weightEditText.text.toString().trim().toInt()

        val database = dbHelper.writableDatabase
        val values = ContentValues()

        values.put(PetContract.COLUMN_PET_NAME, name)
        values.put(PetContract.COLUMN_PET_BREED, breed)
        values.put(PetContract.COLUMN_PET_GENDER, gender)
        values.put(PetContract.COLUMN_PET_WEIGHT, weight)

        val id = database.insert(PetContract.TABLE_NAME, null, values)
        val message = when (id) {
            -1L -> "Error with saving pet"
            else -> "Pet saved with row id: $id"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}