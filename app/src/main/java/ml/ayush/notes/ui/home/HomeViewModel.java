package ml.ayush.notes.ui.home;

import android.app.Application;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ml.ayush.notes.firebase.FirebaseClient;
import ml.ayush.notes.firebase.NoteFirebaseDatabase;
import ml.ayush.notes.repository.NoteRepository;
import ml.ayush.notes.models.Note;

public class HomeViewModel extends AndroidViewModel {

    private List<Integer> cardsChecked = new ArrayList<>();
    private int totalCardsChecked = 0;
    private boolean isContextualToolEnabled = false;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }


    public List<Integer> getCardsChecked() {
        return cardsChecked;
    }

    public void setCardsChecked(List<Integer> cardsChecked) {
        this.cardsChecked = cardsChecked;
    }

    public int getTotalCardsChecked() {
        return totalCardsChecked;
    }

    public void setTotalCardsChecked(int totalCardsChecked) {
        this.totalCardsChecked = totalCardsChecked;
    }

    public boolean isContextualToolEnabled() {
        return isContextualToolEnabled;
    }

    public void setContextualToolEnabled(boolean contextualToolEnabled) {
        isContextualToolEnabled = contextualToolEnabled;
    }

    public boolean CardsCheckedContains(int cardId) {
        return cardsChecked.contains(cardId);
    }

    public void CardsCheckedAdd(int id) {
        cardsChecked.add(id);
    }

    public void CardsCheckedRemove(int id) {
        cardsChecked.remove(Integer.valueOf(id));
    }

    public void CardsCheckedClear() {
        cardsChecked.clear();
    }

    public LiveData<List<Note>> getAllNotes(){
        return new NoteRepository(getApplication()).getAllNotes();
    }

    public void DeleteNote( Note note){
        new NoteRepository(getApplication()).DeleteNote(note);
    }

    public String getDrawerHeading(){
        String name;
        try {
            name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        } catch (Exception e) {
            e.printStackTrace();
            name = null;
        }

        return name;
    }

}
