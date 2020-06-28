package ml.ayush.notes.ui.profile;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;

import ml.ayush.notes.firebase.FirebaseClient;
import ml.ayush.notes.firebase.NoteFirebaseDatabase;
import ml.ayush.notes.repository.NoteRepository;

import static ml.ayush.notes.utils.Constants.IS_BACKUP;
import static ml.ayush.notes.utils.Constants.IS_SYNC;

public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileViewModel";
    private FirebaseAuth mAuth;
    private Context context;
    private boolean isProgressShowing;
    private boolean isBackup, isSync;
    private NoteRepository noteRepository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        context = application;
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "ProfileViewModel: " + mAuth.getCurrentUser());
        noteRepository = new NoteRepository(application);
    }

    public void BackupNow() {
        isBackup = true;
        FirebaseClient firebaseClient = new FirebaseClient();
        if (NoteFirebaseDatabase.getCurrentUser() != null)
            firebaseClient.getAllNotes().observeForever(noteList -> {
                noteRepository.getAllNotes().observeForever(noteList1 -> {
                    noteRepository.BackupNotes(noteList, noteList1);
                });
            });

    }

    public void SyncNow() {
        isSync = true;
        FirebaseClient firebaseClient = new FirebaseClient();
        if (NoteFirebaseDatabase.getCurrentUser() != null)
            firebaseClient.getAllNotes().observeForever(noteList -> {
                if (noteList == null) {
                    setSync(false);
                    noteRepository.setHidePB(true);
                }
                else {
                    noteRepository.getAllNotes().observeForever(noteList1 -> {
                        noteRepository.SyncNotes(noteList, noteList1);
                    });
                }
            });
    }

    public LiveData<Boolean> HideProgressBar() {
        return noteRepository.getHidePB();
    }

    public boolean isProgressShowing() {
        return isProgressShowing;
    }

    public void setProgressShowing(boolean progressShowing) {
        isProgressShowing = progressShowing;
    }

    public boolean isBackup() {
        return isBackup;
    }

    public void setBackup(boolean backup) {
        isBackup = backup;
        IS_BACKUP = backup;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
        IS_SYNC = sync;
    }
}
