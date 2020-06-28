package ml.ayush.notes.notelistevaluation;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ml.ayush.notes.models.Note;

public class NoteListEvaluation {
    private static final String TAG = "NoteListEvaluation";

    private List<Note> firebaseList, localList;
    private List<Note> updatedFirebaseList = new ArrayList<>(), updatedLocalList = new ArrayList<>();

    public NoteListEvaluation(List<Note> firebaseList, List<Note> localList) {
        this.firebaseList = firebaseList;
        this.localList = localList;
    }

    public boolean areListsEqual() {
        return firebaseList == localList;
    }

    public List<Note> getFirebaseUpdatedList() {
        for (Note note : localList) {
           if (!containsObject(firebaseList,note)){
               updatedFirebaseList.add(note);
           }
        }
        return updatedFirebaseList;
    }

    public List<Note> getLocalUpdatedList() {
        for (Note note : firebaseList) {
            if (localList.stream().noneMatch(o -> o.getNoteId().equals(note.getNoteId()))) {
                updatedLocalList.add(note);
            }
        }
        return updatedLocalList;
    }

    public boolean containsObject(List<Note> list, Note note){

        for (Note note1 : list){
            if (note1.getNoteId().equals(note.getNoteId())
            && note1.getTitle().equals(note.getTitle())
            && note1.getContent().equals(note.getContent())
            && note1.getDate().equals(note.getDate())){
                return true;
            }
        }
        return false;
    }
}
