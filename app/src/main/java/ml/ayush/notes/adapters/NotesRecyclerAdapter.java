package ml.ayush.notes.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ml.ayush.notes.R;
import ml.ayush.notes.adapters.viewholders.NotesViewHolder;
import ml.ayush.notes.adapters.viewholders.OnNoteListener;
import ml.ayush.notes.animations.Animation;
import ml.ayush.notes.models.Note;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NotesRecyclerAdapter";
    private List<Note> notesList;
    private OnNoteListener onNoteListener;
    private Context context;
    private boolean on_attach = true;
    private int lastAnimatedPosition = -1;
    private List<Integer> cardsChecked;

    public NotesRecyclerAdapter(OnNoteListener onNoteListener, Context context, List<Integer> cardsChecked) {
        this.onNoteListener = onNoteListener;
        this.context = context;
        this.cardsChecked = cardsChecked;
    }

    public void setNotesList(List<Note> notesList) {
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);

        return new NotesViewHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((NotesViewHolder) holder).title.setText(notesList.get(position).getTitle());
        ((NotesViewHolder) holder).content.setText(notesList.get(position).getContent());
        ((NotesViewHolder) holder).date.setText(notesList.get(position).getDate());
        ((NotesViewHolder) holder).cardView.setTag(position);

        if (cardsChecked.contains(position)) {
            Log.d(TAG, "onBindViewHolder: " + position + " " + cardsChecked);
            ((NotesViewHolder) holder).cardView.setChecked(true);
        } else {
            ((NotesViewHolder) holder).cardView.setChecked(false);
        }
        if (position > lastAnimatedPosition) {
            Animation animation = new Animation();
            animation.setAnimation(((NotesViewHolder) holder).cardView, position, on_attach);
            lastAnimatedPosition = position;
        }

    }
    
    @Override
    public int getItemCount() {
        return notesList == null ? 0 : notesList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        on_attach = false;
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void addItem(Note note) {
        notesList.add(0, note);
    }

}
