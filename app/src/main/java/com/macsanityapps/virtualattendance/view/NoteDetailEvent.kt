package com.macsanityapps.virtualattendance.view

sealed class NoteDetailEvent {
    data class OnDoneClick(val contents: String) : NoteDetailEvent()
    object OnDeleteClick : NoteDetailEvent()
    object OnDeleteConfirmed : NoteDetailEvent()
    data class OnStart(val noteId: String) : NoteDetailEvent()
}
