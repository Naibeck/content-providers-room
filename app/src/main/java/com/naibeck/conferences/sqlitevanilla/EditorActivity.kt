package com.naibeck.conferences.sqlitevanilla

import android.app.AlertDialog
import android.app.LoaderManager
import android.content.ContentValues
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.naibeck.conferences.sqlitevanilla.data.PetContract

/**
 * Created by Kevin Gomez on 4/3/2018.
 * Applaudo Studios
 */
class EditorActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, View.OnTouchListener {
    companion object {
        private const val PET_CURSOR = 101
    }

    private lateinit var nameEditText: EditText
    private lateinit var breedEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var genderSpinner: Spinner

    private var currentPetUri: Uri? = null
    private var petHasChanged = false

    private var gender = PetContract.GENDER_UNKNOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        currentPetUri = intent.data

        if (currentPetUri == null) {
            title = getString(R.string.editor_activity_title_new_pet)
            invalidateOptionsMenu()
        } else {
            title = getString(R.string.editor_activity_title_edit_pet)
            loaderManager.initLoader(PET_CURSOR, null, this)
        }

        currentPetUri = intent.data

        nameEditText = findViewById(R.id.edit_pet_name)
        breedEditText = findViewById(R.id.edit_pet_breed)
        weightEditText = findViewById(R.id.edit_pet_weight)
        genderSpinner = findViewById(R.id.spinner_gender)

        nameEditText.setOnTouchListener(this)
        breedEditText.setOnTouchListener(this)
        weightEditText.setOnTouchListener(this)
        genderSpinner.setOnTouchListener(this)

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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        if (currentPetUri == null) {
            val menuItem = menu?.findItem(R.id.action_delete)
            menuItem?.isVisible = false
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_save -> {
            savePet()
            finish()
            true
        }
        R.id.action_delete -> {
            showDeleteConfirmationDialog()
            true
        }
        android.R.id.home -> {
            if (!petHasChanged) {
                NavUtils.navigateUpFromSameTask(this)
                true
            } else {
                val discardButtonClickListener = DialogInterface.OnClickListener { _, _ ->
                    NavUtils.navigateUpFromSameTask(this)
                }

                showUnsavedChangedDialog(discardButtonClickListener)
                true
            }
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(loaderId: Int, bundle: Bundle?): Loader<Cursor> {
        val projection = arrayOf(PetContract.ID, PetContract.COLUMN_PET_NAME, PetContract.COLUMN_PET_BREED, PetContract.COLUMN_PET_GENDER, PetContract.COLUMN_PET_WEIGHT)
        return CursorLoader(this, currentPetUri, projection, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        if (data != null && data?.moveToFirst()) {
            val nameColumnIndex = data?.getColumnIndex(PetContract.COLUMN_PET_NAME)
            val breedColumnIndex = data?.getColumnIndex(PetContract.COLUMN_PET_BREED)
            val genderColumnIndex = data?.getColumnIndex(PetContract.COLUMN_PET_GENDER)
            val weightColumnIndex = data?.getColumnIndex(PetContract.COLUMN_PET_WEIGHT)

            val name = data?.getString(nameColumnIndex)
            val breed = data?.getString(breedColumnIndex)
            val gender = data?.getInt(genderColumnIndex)
            val weight = data?.getInt(weightColumnIndex)

            nameEditText.setText(name)
            breedEditText.setText(breed)
            weightEditText.setText(weight.toString())

            when (gender) {
                PetContract.GENDER_MALE -> genderSpinner.setSelection(1)
                PetContract.GENDER_FEMALE -> genderSpinner.setSelection(2)
                else -> genderSpinner.setSelection(0)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        nameEditText.setText("")
        breedEditText.setText("")
        weightEditText.setText("")
        genderSpinner.setSelection(0)
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        petHasChanged = true
        return false
    }

    override fun onBackPressed() {
        if (!petHasChanged) {
            super.onBackPressed()
            return
        }

        val discardButtonClickListener = DialogInterface.OnClickListener { _, _ ->
            finish()
        }

        showUnsavedChangedDialog(discardButtonClickListener)
    }

    private fun savePet() {
        if (nameEditText.text.isEmpty() || weightEditText.text.isEmpty()) {
            return
        }

        val name = nameEditText.text.toString().trim()
        val breed = breedEditText.text.toString().trim()
        var weight = 0
        if (weightEditText.text.isNotEmpty()) {
            weight = weightEditText.text.toString().trim().toInt()
        }

        val values = ContentValues()

        values.put(PetContract.COLUMN_PET_NAME, name)
        values.put(PetContract.COLUMN_PET_BREED, breed)
        values.put(PetContract.COLUMN_PET_GENDER, gender)
        values.put(PetContract.COLUMN_PET_WEIGHT, weight)

        if (currentPetUri != null) {
            updatePet(values)
        } else {
            insertPet(values)
        }
    }

    private fun insertPet(values: ContentValues) {
        val uri = contentResolver.insert(PetContract.CONTENT_URI, values)
        val message = when (uri) {
            null -> getString(R.string.editor_insert_pet_failed)
            else -> getString(R.string.editor_insert_pet_successful)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updatePet(values: ContentValues) {
        val updatedRows = contentResolver.update(currentPetUri, values, null, null)

        val message = when (updatedRows) {
            0 -> getString(R.string.editor_update_pet_failed)
            else -> getString(R.string.editor_update_pet_successful)
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showUnsavedChangedDialog(discardButtonClickListener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.unsaved_changes_dialog_msg)
        builder.setNegativeButton(R.string.discard, discardButtonClickListener)
        builder.setPositiveButton(R.string.keep_editing, { dialog, _ ->
            dialog?.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.delete_dialog_msg)
        builder.setPositiveButton(R.string.delete, { _, _ -> deletePet() })
        builder.setNegativeButton(R.string.cancel, { dialog, _ ->
            dialog?.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }

    private fun deletePet() {
        if (currentPetUri != null) {
            val rowsDeleted = contentResolver.delete(currentPetUri, null, null)
            val message = when (rowsDeleted) {
                0 -> getString(R.string.editor_delete_pet_failed)
                else -> getString(R.string.editor_delete_pet_successful)
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}