package com.macsanityapps.virtualattendance.sections

sealed class NoteListEvent {
    data class OnNoteItemClick(val position: Int) : NoteListEvent()
    object OnNewNoteClick : NoteListEvent()
    object OnStart : NoteListEvent()
    object OnStartSection : NoteListEvent()
}