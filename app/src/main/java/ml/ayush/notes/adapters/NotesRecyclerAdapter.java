package ml.ayush.notes.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ml.ayush.notes.R;
import ml.ayush.notes.adapters.viewholders.NotesViewHolder;
import ml.ayush.notes.adapters.viewholders.OnNoteListener;
import ml.ayush.notes.animations.Animation;
import ml.ayush.notes.models.Note;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Note> notesList;
    private boolean checkedNotes = false;
    private OnNoteListener onNoteListener;
    private Context context;
    private boolean on_attach = true;
    private int lastAnimatedPosition = -1;

    public NotesRecyclerAdapter(OnNoteListener onNoteListener, Context context) {
        this.onNoteListener = onNoteListener;
        this.context = context;
    }

    public void setNotesList(List<Note> notesList) {
        this.notesList = notesList;
        notifyDataSetChanged();
    }

    public void setCheckedNotes(boolean checkedNotes) {
        this.checkedNotes = checkedNotes;
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
        setBgColor(((NotesViewHolder) holder).cardView);
        if (checkedNotes) {
            ((NotesViewHolder) holder).cardView.setChecked(true);
        } else ((NotesViewHolder) holder).cardView.setChecked(false);
        if (position > lastAnimatedPosition) {
            Animation animation = new Animation();
            animation.setAnimation(((NotesViewHolder) holder).cardView, position, on_attach);
            lastAnimatedPosition = position;
        }
    }

    private void setBgColor(MaterialCardView cardView) {
        String[] colorsTxt = context.getResources().getStringArray(R.array.card_bg_colors);
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < colorsTxt.length; i++) {
            int newColor = Color.parseColor(colorsTxt[i]);
            colors.add(newColor);
        }
        cardView.setCardBackgroundColor(colors.get((int) (Math.random() * (colors.size()))));
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
        notifyDataSetChanged();
    }

}
