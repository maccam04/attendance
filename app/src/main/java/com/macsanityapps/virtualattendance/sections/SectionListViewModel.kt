package com.macsanityapps.virtualattendance.sections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.macsanityapps.virtualattendance.common.BaseViewModel
import com.macsanityapps.virtualattendance.common.GET_NOTES_ERROR
import com.macsanityapps.virtualattendance.common.ResultWrapper
import com.macsanityapps.virtualattendance.data.Section
import com.macsanityapps.virtualattendance.data.repository.ISectionRepository
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SectionListViewModel(
    val noteRepo: ISectionRepository,
    uiContext: CoroutineContext
) : BaseViewModel<NoteListEvent>(uiContext) {

    private val noteListState = MutableLiveData<List<Section>>()
    val noteList: LiveData<List<Section>> get() = noteListState

    private val editNoteState = MutableLiveData<Section>()
    val editNote: LiveData<Section> get() = editNoteState


    override fun handleEvent(event: NoteListEvent) {
        when (event) {
            is NoteListEvent.OnStart -> getNotes()
            is NoteListEvent.OnStartSection -> getEnrollSection()
            is NoteListEvent.OnNoteItemClick -> editNote(event.position)
        }
    }

    private fun editNote(position: Int) {
        editNoteState.value = noteList.value!![position]
    }

    private fun getNotes() = launch {
        val notesResult = noteRepo.getNotes()

        when (notesResult) {
            is ResultWrapper.Value -> noteListState.value = notesResult.value
            is ResultWrapper.Error -> errorState.value = GET_NOTES_ERROR
        }
    }

    private fun getEnrollSection()= launch {
        val notesResult = noteRepo.getOwnNotes()

        when (notesResult) {
            is ResultWrapper.Value -> noteListState.value = notesResult.value
            is ResultWrapper.Error -> errorState.value = GET_NOTES_ERROR
        }
    }

}