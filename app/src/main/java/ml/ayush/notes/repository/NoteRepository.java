package ml.ayush.notes.repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ml.ayush.notes.firebase.FirebaseClient;
import ml.ayush.notes.firebase.NoteFirebaseDatabase;
import ml.ayush.notes.models.Note;
import ml.ayush.notes.notelistevaluation.NoteListEvaluation;
import ml.ayush.notes.persistence.NoteDao;
import ml.ayush.notes.persistence.NoteDatabase;

import static ml.ayush.notes.utils.Constants.BACKUP_SYNC;
import static ml.ayush.notes.utils.Constants.IS_BACKUP;
import static ml.ayush.notes.utils.Constants.IS_SYNC;

public class NoteRepository {

    private static final String TAG = "NoteRepository";
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    private MutableLiveData<Boolean> hidePB = new MutableLiveData<>();
    Context context;

    public NoteRepository(Application application) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();
        allNotes = noteDao.getAllNotes();
        context = application;
    }

    public void InsertNote(Note note) {
        new InsertNoteAsyncTask(noteDao).execute(note);
        if (NoteFirebaseDatabase.getCurrentUser() != null && BACKUP_SYNC == 0)
            new FirebaseClient().InsertNote(note);
    }

    public void UpdateNote(Note note) {
        new UpdateNoteAsyncTask(noteDao).execute(note);
        if (NoteFirebaseDatabase.getCurrentUser() != null && BACKUP_SYNC == 0)
            new FirebaseClient().UpdateNote(note);
    }

    public void DeleteNote(Note note) {
        new DeleteNoteAsyncTask(noteDao).execute(note);
        if (NoteFirebaseDatabase.getCurrentUser() != null) new FirebaseClient().DeleteNote(note);
    }

    public void DeleteAllNotes() {
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {

        NoteDao noteDao;

        public InsertNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insertNote(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {

        NoteDao noteDao;

        public UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.updateNote(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {

        NoteDao noteDao;

        public DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.deleteNote(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {

        NoteDao noteDao;

        public DeleteAllNotesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }

    public void BackupNotes(List<Note> FirebaseNotes, List<Note> LocalNotes) {
        List<Note> notes = new NoteListEvaluation(FirebaseNotes, LocalNotes).getFirebaseUpdatedList();
        for (Note note : notes) {
            hidePB.setValue(false);
            new FirebaseClient().InsertNote(note);
        }
        hidePB.setValue(true);
        IS_BACKUP = false;
        Toast.makeText(context, "Backup Done", Toast.LENGTH_SHORT).show();
    }

    public void SyncNotes(List<Note> FirebaseNotes, List<Note> LocalNotes) {
        List<Note> notes = new NoteListEvaluation(FirebaseNotes, LocalNotes).getLocalUpdatedList();
        for (Note note : notes) {
            hidePB.setValue(false);
            new InsertNoteAsyncTask(noteDao).execute(note);
        }
        hidePB.setValue(true);
        IS_SYNC = false;
        Log.d(TAG, "SyncNotes: " + "done");
        Toast.makeText(context, "Sync Done", Toast.LENGTH_SHORT).show();
    }

    public MutableLiveData<Boolean> getHidePB() {
        return hidePB;
    }

    public void setHidePB(boolean hide) {
        hidePB.setValue(hide);
    }

    public void sendFeedback(String type, String content){
        new FirebaseClient().sendFeedback(type, content);
    }
}
