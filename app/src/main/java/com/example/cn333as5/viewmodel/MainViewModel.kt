package com.example.cn333as5.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cn333as5.database.AppDatabase
import com.example.cn333as5.database.DbMapper
import com.example.cn333as5.database.Repository
import com.example.cn333as5.domain.model.ColorModel
import com.example.cn333as5.domain.model.NoteModel
import com.example.cn333as5.domain.model.TagModel
import com.example.cn333as5.routing.MyNotesRouter
import com.example.cn333as5.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : ViewModel() {
    val notesNotInTrash: LiveData<List<NoteModel>> by lazy {
        repository.getAllNotesNotInTrash()
    }

    private var _noteEntry = MutableLiveData(NoteModel())

    val noteEntry: LiveData<NoteModel> = _noteEntry

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }

    val tags: LiveData<List<TagModel>> by lazy {
        repository.getAllTags()
    }



    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.noteDao(), db.colorDao(), db.tagDao(), DbMapper())
    }

    fun onCreateNewNoteClick() {
        _noteEntry.value = NoteModel()
        MyNotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteClick(note: NoteModel) {
        _noteEntry.value = note
        MyNotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteEntryChange(note: NoteModel) {
        _noteEntry.value = note
    }

    fun saveNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertNote(note)

            withContext(Dispatchers.Main) {
                MyNotesRouter.navigateTo(Screen.Notes)

                _noteEntry.value = NoteModel()
            }
        }
    }

    fun moveNoteToTrash(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.moveNoteToTrash(note.id)

            withContext(Dispatchers.Main) {
                MyNotesRouter.navigateTo(Screen.Notes)
            }
        }
    }
}