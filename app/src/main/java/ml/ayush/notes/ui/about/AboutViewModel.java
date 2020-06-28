package ml.ayush.notes.ui.about;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import ml.ayush.notes.repository.NoteRepository;

public class AboutViewModel extends AndroidViewModel {
    public AboutViewModel(@NonNull Application application) {
        super(application);
    }

    public void sendFeedback(String type, String content){
        new NoteRepository(getApplication()).sendFeedback(type, content);
    }
}
