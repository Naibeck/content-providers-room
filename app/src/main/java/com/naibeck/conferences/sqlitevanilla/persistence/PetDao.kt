package com.naibeck.conferences.sqlitevanilla.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import android.database.Cursor

/**
 * Created by Kevin Gomez on 4/9/2018.
 * Applaudo Studios
 */
@Dao
interface PetDao {
    @Query(value = "SELECT ${Pet.ID}, ${Pet.COLUMN_PET_NAME}, ${Pet.COLUMN_PET_BREED} FROM ${Pet.TABLE_NAME}")
    fun selectPets(): Cursor

    @Query(value = "SELECT ${Pet.ID}, ${Pet.COLUMN_PET_NAME}, ${Pet.COLUMN_PET_BREED}, ${Pet.COLUMN_PET_GENDER}, ${Pet.COLUMN_PET_WEIGHT} FROM ${Pet.TABLE_NAME} " +
            "WHERE ${Pet.ID} = :id")
    fun selectPetById(id: Long?): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPet(pet: Pet): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePet(pet: Pet): Int

    @Query(value = "DELETE FROM ${Pet.TABLE_NAME} WHERE ${Pet.ID} = :id")
    fun deletePetById(id: Long?): Int

    @Query(value = "DELETE FROM ${Pet.TABLE_NAME}")
    fun deleteAll(): Int
}