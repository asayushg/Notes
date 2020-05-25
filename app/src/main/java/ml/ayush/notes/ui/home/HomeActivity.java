package ml.ayush.notes.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ml.ayush.notes.BuildConfig;
import ml.ayush.notes.R;
import ml.ayush.notes.adapters.NotesRecyclerAdapter;
import ml.ayush.notes.adapters.viewholders.OnNoteListener;
import ml.ayush.notes.models.Note;
import ml.ayush.notes.ui.newnote.NewNoteDialog;
import ml.ayush.notes.ui.newuser.SplashScreenActivity;
import ml.ayush.notes.utils.Constants;

public class HomeActivity extends AppCompatActivity implements OnNoteListener {

    @BindView(R.id.topAppBar)
    MaterialToolbar topAppBar;
    private MenuInflater menuInflater;
    @BindView(R.id.NotesRecyclerView)
    RecyclerView notesRecycler;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private NotesRecyclerAdapter notesRecyclerAdapter;
    private static final String TAG = "HomeActivity";
    private List<Integer> cardsChecked = new ArrayList<>();
    private int totalCardsChecked = 0;
    private ActionMode actionMode;
    private boolean isContextualToolEnabled = false;
    private List<Note> notes = new ArrayList<>();
    private SharedPreferences sp;
    @BindView(R.id.add_new_note)
    FloatingActionButton addNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences("OnBoarding", MODE_PRIVATE);
        ButterKnife.bind(this);
        if (sp.getBoolean("firstTime", true)) {
            runSplash();
        }
        ButterKnife.bind(this);
        initViews();
        setClickListeners();
        test();
    }

    private void runSplash() {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    private void initViews() {
        topAppBar.setTitleTextColor(getApplicationContext().getColor(R.color.white));
        menuInflater = getMenuInflater();
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(Constants.NOTES_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        notesRecycler.setLayoutManager(staggeredGridLayoutManager);
        notesRecyclerAdapter = new NotesRecyclerAdapter(this, this);
        notesRecycler.setAdapter(notesRecyclerAdapter);
        notesRecyclerAdapter.setNotesList(notes);
    }

    private void setClickListeners() {
        topAppBar.setNavigationOnClickListener(v -> {

        });

        topAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.about:
                    getAbout();
                    return true;
                case R.id.rate_us:
                    Rate();
                    return true;
                case R.id.share:
                    ShareApp();
                    return true;
                case R.id.feedback:
                    Feedback();
                    return true;
            }
            return false;
        });

        addNewNote.setOnClickListener(v -> {
            AddNewNote();
        });

    }

    private void getAbout() {
        // todo
    }

    private void Rate() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    private void ShareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey, check out Notes App at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menuInflater.inflate(R.menu.contextual_action_bar, menu);
            isContextualToolEnabled = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();

            switch (id) {
                case R.id.shareNote:
                    shareNote();
                    break;
                case R.id.deleteNote:
                    deleteNote();
                    break;
                //  case R.id.select_all_notes:
                //      checkAllNotes();
                //      break;
                default:
                    Snackbar.make(notesRecycler, getString(R.string.error_msg), Snackbar.LENGTH_SHORT).show();
            }


            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            isContextualToolEnabled = false;
            Log.d(TAG, "UncheckAllNotes: " + cardsChecked);
            UncheckAllNotes();
        }
    };

    public void showContextualToolBar() {
        actionMode = startSupportActionMode(callback);
        actionMode.setTitle(getString(R.string.selected));
    }

    private void test() {
        notes.add(new Note(
                "ABCDEF",
                "how are you feeling now i hope yoy are doing okay",
                "12/2/20"
        ));
        notes.add(new Note(
                "ABCDEFghgdjb",
                "how are you feeling now i hope yoy are doing okay  fsGHcbj sgCHBj gnHBCJNhdvmd ghcbjnk ghdbckjm rfghdbj",
                "12/2/20"
        ));
        notes.add(new Note(
                "ABCDEF",
                "how are you feeling now",
                "12/2/20"
        ));
        notes.add(new Note(
                "ABCDEF",
                "how are you feeling now i hope yoy are doing okay gxhrc etgxch xrwf",
                "12/2/20"
        ));
        notes.add(new Note(
                "ABCDEF",
                "how are you feeling now i hope yoy are doing okay reztgrx wreztxrc wrzetxdrhcf reztxgdrhf",
                "12/2/20"
        ));
        for (int i = 0; i < 100; i++) {
            notes.add(new Note(
                    "ABCDEF",
                    "how are you feeling now i hope yoy are doing okay zsegxdh sgdhf erdxcfg exdrhcf Rztdhrwte zsxrdret zsrxyz txdcf",
                    "12/2/20"
            ));
        }
        notesRecyclerAdapter.setNotesList(notes);
    }

    @Override
    public void onNoteClickListener(int position, MaterialCardView cardView) {
        Log.d(TAG, "onNoteClickListener: " + totalCardsChecked);
        if (totalCardsChecked > 0 || isContextualToolEnabled) {
            toggleCardCheck(cardView);
        } else if (totalCardsChecked == 0) {
            NoteEditor(position);
        }
    }

    @Override
    public boolean onNoteLongClickListener(int position, MaterialCardView cardView) {
        Log.d(TAG, "onNoteLongClickListener: " + totalCardsChecked);
        if (totalCardsChecked == 0 && !isContextualToolEnabled) {
            showContextualToolBar();
            toggleCardCheck(cardView);
        } else if (totalCardsChecked > 0 && !cardView.isChecked()) {
            toggleCardCheck(cardView);
        }
        return true;
    }

    @Override
    public void onCheckedChangeListener(MaterialCardView cardView, boolean isChecked) {
        int cardId = (int) cardView.getTag();
        if (isChecked) {
            if (!cardsChecked.contains(cardId)) cardsChecked.add(cardId);
            if (totalCardsChecked < notes.size()) totalCardsChecked++;
        } else {
            if (cardsChecked.contains(cardId)) cardsChecked.remove(Integer.valueOf(cardId));
            if (totalCardsChecked > 0) totalCardsChecked--;
        }
        setTitleForContextualToolBar();
        Log.d(TAG, "onCheckedChangeListener: " + totalCardsChecked);
    }

    private void setTitleForContextualToolBar() {
        if (actionMode != null) {
            if (totalCardsChecked == 0) actionMode.setTitle(getString(R.string.select_items));
            String titleHelp = getString(R.string.selected);
            if (totalCardsChecked > 1) titleHelp = getString(R.string.items_selected);
            actionMode.setTitle(totalCardsChecked + " " + titleHelp);
        }
    }

    public void NoteEditor(int position) {
        // todo
    }

    public void toggleCardCheck(MaterialCardView cardView) {
        cardView.setChecked(!cardView.isChecked());
    }

    public void UncheckAllNotes() {
        cardsChecked.clear();
        totalCardsChecked = 0;
        notesRecyclerAdapter.notifyDataSetChanged();
    }

    public void checkAllNotes() {
        if (totalCardsChecked < notes.size() - 1) {
            cardsChecked.clear();
            notesRecyclerAdapter.setCheckedNotes(true);
            notesRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void shareNote() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                getSelectedNotes());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private String getSelectedNotes() {
        // todo
        return "";
    }

    // *************** DATABASE OPERATIONS METHODS ****************

    private void deleteNote() {
        // todo
        Snackbar.make(notesRecycler, "delete", Snackbar.LENGTH_SHORT).show();
    }

    public void searchNote(View v) {
        // todo
        Snackbar.make(notesRecycler, "search", Snackbar.LENGTH_SHORT).show();
    }

    private void Feedback() {
        // todo
        Snackbar.make(notesRecycler, "" + totalCardsChecked + " " + cardsChecked, Snackbar.LENGTH_SHORT).show();
    }

    private void AddNewNote() {
        NewNoteDialog newNoteDialog = new NewNoteDialog();
        newNoteDialog.display(getSupportFragmentManager());

    }

}
