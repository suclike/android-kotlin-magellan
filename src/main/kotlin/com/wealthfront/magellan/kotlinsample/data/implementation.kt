package com.wealthfront.magellan.kotlinsample.data

import android.os.Handler
import android.support.annotation.VisibleForTesting
import com.wealthfront.magellan.kotlinsample.isRunningTest

/**
 * Implementation of the Notes Service API that adds a latency simulating network.
 */
class NotesServiceApiImpl : NotesServiceApi {

    override fun getAllNotes(onLoaded : (List<Note>) -> Unit) {
        if (isRunningTest) {
            onLoaded(ArrayList<Note>(NOTES_SERVICE_DATA.values))
            return
        }
        // Simulate network by delaying the execution.
        val handler = Handler()
        handler.postDelayed({
            val notes = ArrayList<Note>(NOTES_SERVICE_DATA.values)
            onLoaded(notes)
        }, SERVICE_LATENCY_IN_MILLIS.toLong())
    }

    override fun getNote(noteId: String, onLoaded: (Note?) -> Unit) {
        //TODO: Add network latency here too.
        val note = NOTES_SERVICE_DATA.get(noteId)
        onLoaded(note)
    }

    override fun saveNote(note: Note) {
        NOTES_SERVICE_DATA.put(note.id, note)
    }

    companion object {

        private val NOTES = mutableListOf(
                Note(description = "The simplest navigation library for Android.", title = "Magellan", url = "https://github.com/wealthfront/magellan"),
                Note(description = "Statically typed programming language for modern multiplatform applications. 100% interoperable with Java™ and Android™.", title = "Kotlin", url = "http://kotlinlang.org/")
        )


        private val SERVICE_LATENCY_IN_MILLIS = 700

        @VisibleForTesting
        val NOTES_SERVICE_DATA : MutableMap<String, Note> = NOTES.associateBy { it.id }.toMutableMap()
    }

}



class InMemoryRepository(val api: NotesServiceApi): NotesRepository {
    var notes : List<Note>? = null

    override fun getNotes(onNotesLoaded: (List<Note>) -> Unit) {
        if (notes != null) {
            onNotesLoaded(notes!!)
        } else {
            api.getAllNotes { notes ->
                this.notes = ArrayList(notes)
                onNotesLoaded(notes)
            }
        }
    }

    override fun getNote(noteId: String, onNoteLoaded: (Note?) -> Unit) {
        api.getNote(noteId) { note ->
            onNoteLoaded(note)
        }
    }

    override fun saveNote(note: Note) {
        api.saveNote(note)
        refreshData()
    }

    override fun refreshData() {
        notes = null
    }

}