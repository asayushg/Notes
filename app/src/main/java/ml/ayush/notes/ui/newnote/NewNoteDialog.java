package ml.ayush.notes.ui.newnote;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import ml.ayush.notes.R;
import ml.ayush.notes.repository.NoteRepository;
import ml.ayush.notes.models.Note;

public class NewNoteDialog extends DialogFragment {

    private static final String TAG = "NewNoteDialog";
    private MaterialToolbar toolbar;
    private EditText editTitle, editNote;
    private TextView detailNote;
    private NewNoteDialog newNoteDialog;
    private boolean isEditingTitle = true;
    private boolean isEditingNote = true;
    private String title;
    private Note note;
    private String date;
    FrameLayout frameLayout;

    public NewNoteDialog() {
    }

    public NewNoteDialog(Note note) {
        this.note = note;
    }

    public NewNoteDialog display(FragmentManager fragmentManager, Note note) {
        this.note = note;
        newNoteDialog = new NewNoteDialog(note);
        newNoteDialog.show(fragmentManager, TAG);
        return newNoteDialog;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.new_note_dialog, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getContext().getColor(R.color.white));
        editTitle = view.findViewById(R.id.edit_title);
        editTitle.setVisibility(View.GONE);
        editNote = view.findViewById(R.id.edit_note);
        detailNote = view.findViewById(R.id.note_details);

        detailNote.setText(getDate() + getDetail());
        editNote.setMinHeight(getMinHeight());
        editNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                detailNote.setText(getDate() + getDetail());
            }
        });

        editNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isEditingTitle) {
                    saveEditTitle();
                }
            }
        });
        toolbar.setOnClickListener(v -> {
            editTitle();
        });

        return view;
    }

    private int getMinHeight() {
        return 50;
    }

    private String getDate() {
        if (date != null) return date;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String formattedDate = df.format(c);
        date = formattedDate;
        return date;
    }

    private String getDetail() {
        return " | " + editNote.length() + " characters";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(getString(R.string.new_note));
        editNote.setHint(getString(R.string.add_note));
        toolbar.inflateMenu(R.menu.new_note_menu);
        if (note != null) {
            toolbar.setTitle(note.getTitle());
            editNote.setText(note.getContent());
        }
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.save_note:
                    if (!isEditingTitle) {
                        saveNote();
                    } else {
                        saveEditTitle();
                    }
                    break;
                case R.id.edit_title:
                    editTitle();
                    break;
                default:
                    break;
            }
            return true;
        });
        editTitle();

        toolbar.setNavigationOnClickListener(v -> {
            if (isEditingTitle) {
                cancelEditTitle();
                isEditingTitle = false;
            } else if (isEditingNote) {
                dismiss();
                isEditingNote = false;
            } else {
                dismiss();
            }

        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (isEditingTitle) {
                    saveEditTitle();
                } else {
                    saveNote();
                    dismiss();
                }
                Log.d(TAG, "onBackPressed: ");
            }

            @Override
            public boolean onTouchEvent(@NonNull MotionEvent event) {
                //Toast.makeText(getContext(), ""+ event.getY(), Toast.LENGTH_SHORT).show();
                if (event.getY()> 300) EditNote();
                return super.onTouchEvent(event);
            }
        };


    }

    private void editTitle() {
        title = toolbar.getTitle().toString();
        toolbar.setTitle("");
        editTitle.setText(title);
        editTitle.setVisibility(View.VISIBLE);
        editTitle.setSelectAllOnFocus(true);
        editTitle.requestFocus();
        isEditingTitle = true;
        toolbar.getMenu().getItem(0).setVisible(false);
        showKeyBoard();
    }


    private void saveEditTitle() {
        toolbar.setTitle(editTitle.getText().toString());
        toolbar.getMenu().getItem(0).setVisible(true);
        editTitle.setVisibility(View.GONE);
        isEditingTitle = false;
    }

    private void cancelEditTitle() {
        toolbar.getMenu().getItem(0).setVisible(true);
        editTitle.setVisibility(View.GONE);
        toolbar.setTitle(title);
        isEditingTitle = false;
    }

    private void showKeyBoard(){
        InputMethodManager imm = null;
        try {
            imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTitle, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void EditNote() {
        editNote.requestFocus();
        InputMethodManager imm = null;
        try {
            imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editNote, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isEditingNote = true;
    }

    private void saveNote() {
        if (note != null) {
            Note mNote = new Note(
                    toolbar.getTitle().toString(),
                    editNote.getText().toString(),
                    date,
                    note.getNoteId()
            );
            new NoteRepository(getActivity().getApplication()).UpdateNote(mNote);
        } else {
            Note mNote = new Note(
                    toolbar.getTitle().toString(),
                    editNote.getText().toString(),
                    date,
                    getPrimaryId()
            );
            if(!mNote.getTitle().equals(getString(R.string.new_note)) || !mNote.getContent().isEmpty()) {
                new NoteRepository(getActivity().getApplication()).InsertNote(mNote);
            }
        }
        dismiss();
    }

    private String getPrimaryId() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = df.format(c);
        formattedDate += new Random().nextInt(90) + 10;;
        return formattedDate;
    }
}
