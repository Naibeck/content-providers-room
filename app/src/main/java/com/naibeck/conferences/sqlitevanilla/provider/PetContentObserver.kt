package com.naibeck.conferences.sqlitevanilla.provider

import android.database.ContentObserver
import android.os.Handler

/**
 * Created by Kevin Gomez on 4/10/2018.
 * Applaudo Studios
 */
class PetContentObserver(handler: Handler, private val contract: PetContentContract) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        contract.onSomethingChanged(selfChange)

    }

    interface PetContentContract {
        fun onSomethingChanged(changed: Boolean)
    }
}