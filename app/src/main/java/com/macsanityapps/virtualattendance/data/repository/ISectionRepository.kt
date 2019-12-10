package com.macsanityapps.virtualattendance.data.repository

import com.macsanityapps.virtualattendance.common.ResultWrapper
import com.macsanityapps.virtualattendance.data.Section


interface ISectionRepository {

    suspend fun getNoteById(noteId: String): ResultWrapper<Exception, Section>
    suspend fun getNotes(): ResultWrapper<Exception, List<Section>>
    suspend fun getOwnNotes(): ResultWrapper<Exception, List<Section>>
    suspend fun deleteNote(note: Section): ResultWrapper<Exception, Unit>
    suspend fun updateNote(note: Section): ResultWrapper<Exception, Unit>

}