package ml.ayush.notes.adapters.viewholders;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import ml.ayush.notes.R;

public class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, MaterialCardView.OnCheckedChangeListener {
    private static final String TAG = "NotesViewHolder";
    public TextView title, content, date;
    private OnNoteListener onNoteListener;
    public MaterialCardView cardView;

    public NotesViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
        super(itemView);
        this.onNoteListener = onNoteListener;
        title = itemView.findViewById(R.id.noteTitle);
        content = itemView.findViewById(R.id.noteContent);
        date = itemView.findViewById(R.id.noteDate);
        cardView = itemView.findViewById(R.id.noteItem);
        cardView.setOnClickListener(this);
        cardView.setOnLongClickListener(this);
        cardView.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        onNoteListener.onNoteClickListener(getAdapterPosition(), cardView);
        Log.d(TAG, "onClick: " + getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        onNoteListener.onNoteLongClickListener(getAdapterPosition(), cardView);
        Log.d(TAG, "onLongClick: " + getAdapterPosition());
        return true;
    }

    @Override
    public void onCheckedChanged(MaterialCardView card, boolean isChecked) {
        onNoteListener.onCheckedChangeListener(card, isChecked);
    }

}
