package ml.ayush.notes.firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ml.ayush.notes.models.Note;

public class FirebaseClient {

    private FirebaseDatabase database;
    private MutableLiveData<List<Note>> firebaseNotes = new MutableLiveData<>();


    public FirebaseClient() {
        database = NoteFirebaseDatabase.getInstance();
    }

    public void InsertNote(Note note) {
        Map<String, String> mNote = new HashMap<>();
        mNote.put("Title", note.getTitle() + "");
        mNote.put("Content", note.getContent() + "");
        mNote.put("Date", note.getDate());
        database.getReference()
                .child("Notes/" + NoteFirebaseDatabase.getCurrentUser().getUid()
                        + "/" + note.getNoteId())
                .setValue(mNote);
    }

    public void UpdateNote(Note note) {
        Map<String, Object> mNote = new HashMap<>();
        mNote.put("Title", note.getTitle() + "");
        mNote.put("Content", note.getContent() + "");
        mNote.put("Date", note.getDate());
        database.getReference()
                .child("Notes/" + NoteFirebaseDatabase.getCurrentUser().getUid()
                        + "/" + note.getNoteId())
                .updateChildren(mNote);
    }

    public void DeleteNote(Note note) {
        database.getReference()
                .child("Notes/" + NoteFirebaseDatabase.getCurrentUser().getUid()
                        + "/" + note.getNoteId())
                .removeValue();
    }


    public LiveData<List<Note>> getAllNotes() {
        database.getReference()
                .child("Notes/" + NoteFirebaseDatabase.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            List<Note> list = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                list.add(new Note(
                                        snapshot.child("Title").getValue().toString(),
                                        snapshot.child("Content").getValue().toString(),
                                        snapshot.child("Date").getValue().toString(),
                                        snapshot.getKey()
                                ));
                            }

                            firebaseNotes.setValue(list);
                        }
                        else firebaseNotes.setValue(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return firebaseNotes;
    }

    public void sendFeedback(String type, String content) {
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("Type", type);
        feedback.put("Content", content);
        database.getReference()
                .child("Feedback")
                .push()
                .setValue(feedback);
    }

}
