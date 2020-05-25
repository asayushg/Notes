package ml.ayush.notes.ui.newnote;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;

import ml.ayush.notes.R;

public class NewNoteDialog extends DialogFragment {

    private static final String TAG = "NewNoteDialog";
    private MaterialToolbar toolbar;
    private EditText editTitle;
    private boolean isEditingTitle = true;
    private boolean isEditingNote = true;
    NewNoteDialog newNoteDialog;
    private String title;

    public NewNoteDialog() {
    }

    public NewNoteDialog display(FragmentManager fragmentManager) {
        newNoteDialog = new NewNoteDialog();
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

        toolbar.setOnClickListener(v -> {
            editTitle();
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("New Note");
        toolbar.inflateMenu(R.menu.new_note_menu);
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
            }
            else if (isEditingNote){
                cancelEditNote();
                isEditingNote = false;
            }
            else {
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
        };
    }

    private void editTitle() {
        title = toolbar.getTitle().toString();
        toolbar.setTitle("");
        editTitle.setText(title);
        editTitle.setVisibility(View.VISIBLE);
        editTitle.setSelectAllOnFocus(true);
        editTitle.requestFocus();
        InputMethodManager imm = null;
        try {
            imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTitle, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isEditingTitle = true;
        toolbar.getMenu().getItem(0).setVisible(false);
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

    private void EditNote(){
        editTitle.setText(title);
        editTitle.setVisibility(View.VISIBLE);
        editTitle.setSelectAllOnFocus(true);
        editTitle.requestFocus();
        InputMethodManager imm = null;
        try {
            imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTitle, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isEditingNote= true;
    }

    private void cancelEditNote() {
    }

    private void saveNote() {
        //todo
    }
}
