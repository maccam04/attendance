package com.macsanityapps.virtualattendance.data

import androidx.room.*


@Dao
interface SectionDao {
    @Query("SELECT * FROM notes")
    suspend fun getSections(): List<RoomSection>

    @Query("SELECT * FROM notes WHERE creation_date = :creationDate")
    suspend fun getSectioneById(creationDate: String): RoomSection

    @Delete
    suspend fun deleteSection(note: RoomSection)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSection(note: RoomSection): Long
}









//@Dao
//interface NoteDao {
//    @Query("SELECT * FROM notes")
//    suspend fun getNotes(): List<RoomNote>
//
//    @Query("SELECT * FROM notes WHERE creation_date = :creationDate")
//    suspend fun getNoteById(creationDate: String): RoomNote
//
//    @Delete
//    suspend fun deleteNote(note: RoomNote)
//
//    //if update successful, will return number of rows effected, which should be 1
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrUpdateNote(note: RoomNote): Long
//}