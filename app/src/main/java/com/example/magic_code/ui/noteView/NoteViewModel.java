package com.example.magic_code.ui.noteView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.magic_code.models.Note;

public class NoteViewModel extends ViewModel {

    private MutableLiveData<Note> noteLiveData = new MutableLiveData<>();

    public void setNoteData(Note note) {
        noteLiveData.setValue(note);
    }

    public LiveData<Note> getNoteLiveData() {
        return noteLiveData;
    }
}
