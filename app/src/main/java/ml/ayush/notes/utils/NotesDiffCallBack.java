package ml.ayush.notes.utils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import ml.ayush.notes.models.Note;

public class NotesDiffCallBack extends DiffUtil.Callback{

    List<Note> oldNotes;
    List<Note> newNotes;

    public NotesDiffCallBack(List<Note> newNotes, List<Note> oldNotes) {
        this.newNotes = newNotes;
        this.oldNotes = oldNotes;
    }

    @Override
    public int getOldListSize() {
        return oldNotes.size();
    }

    @Override
    public int getNewListSize() {
        return newNotes.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldNotes.get(oldItemPosition).getNoteId() == newNotes.get(newItemPosition).getNoteId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldNotes.get(oldItemPosition).equals(newNotes.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}