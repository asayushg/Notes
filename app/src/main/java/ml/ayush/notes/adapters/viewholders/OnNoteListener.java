package ml.ayush.notes.adapters.viewholders;

import com.google.android.material.card.MaterialCardView;

public interface OnNoteListener {

    void onNoteClickListener(int position, MaterialCardView cardView);

    boolean onNoteLongClickListener(int position, MaterialCardView cardView);

    void onCheckedChangeListener(MaterialCardView cardView, boolean isChecked);

}
