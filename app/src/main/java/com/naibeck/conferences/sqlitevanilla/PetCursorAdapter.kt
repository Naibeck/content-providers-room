package com.naibeck.conferences.sqlitevanilla

import android.content.Context
import android.database.Cursor
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.naibeck.conferences.sqlitevanilla.data.PetContract

/**
 * Created by Kevin Gomez on 4/9/2018.
 * Applaudo Studios
 */
class PetCursorAdapter(context: Context, cursor: Cursor?) : CursorAdapter(context, cursor) {
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?) =
            LayoutInflater.from(context).inflate(R.layout.item_list, parent, false)

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val petName = view?.findViewById<TextView>(R.id.name)
        val petBreed = view?.findViewById<TextView>(R.id.summary)

        val nameColumnIndex = cursor?.getColumnIndex(PetContract.COLUMN_PET_NAME)
        val breedColumnIndex = cursor?.getColumnIndex(PetContract.COLUMN_PET_BREED)

        val name = cursor?.getString(nameColumnIndex!!)
        var breed = cursor?.getString(breedColumnIndex!!)

        if (TextUtils.isEmpty(breed)) {
            breed = context?.getString(R.string.unknown_breed)
        }

        petName?.text = name
        petBreed?.text = breed
    }
}