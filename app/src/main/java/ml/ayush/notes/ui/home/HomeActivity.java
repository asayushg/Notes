package ml.ayush.notes.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import ml.ayush.notes.BuildConfig;
import ml.ayush.notes.R;
import ml.ayush.notes.adapters.NotesRecyclerAdapter;
import ml.ayush.notes.adapters.viewholders.OnNoteListener;
import ml.ayush.notes.adapters.viewholders.SpacesItemDecoration;
import ml.ayush.notes.models.Note;
import ml.ayush.notes.ui.about.AboutActivity;
import ml.ayush.notes.ui.newnote.NewNoteDialog;
import ml.ayush.notes.ui.newuser.SplashScreenActivity;
import ml.ayush.notes.ui.profile.ProfileActivity;
import ml.ayush.notes.utils.Constants;
import ml.ayush.notes.utils.NotesDiffCallBack;

import static ml.ayush.notes.utils.Constants.IS_BACKUP;
import static ml.ayush.notes.utils.Constants.IS_SYNC;

public class HomeActivity extends AppCompatActivity implements OnNoteListener {

    @BindView(R.id.topAppBar)
    MaterialToolbar topAppBar;
    private MenuInflater menuInflater;
    @BindView(R.id.NotesRecyclerView)
    RecyclerView notesRecycler;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private NotesRecyclerAdapter notesRecyclerAdapter;
    private static final String TAG = "HomeActivity";
    private ActionMode actionMode;
    private List<Note> notesList = new ArrayList<>();
    private List<Note> searchNotesList = new ArrayList<>();
    private SharedPreferences sp;
    @BindView(R.id.content)
    CoordinatorLayout content;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.add_new_note)
    FloatingActionButton addNewNote;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.heading_drawer)
    TextView headingDrawer;

    // View Model
    private HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences("OnBoarding", MODE_PRIVATE);

        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication()))
                .get(HomeViewModel.class);

        ButterKnife.bind(this);
        if (sp.getBoolean("firstTime", true)) {
            runSplash();
        }
        ButterKnife.bind(this);
        initViews();
        setClickListeners();
        restorePreviousState();
        subscribeObservers();
    }

    private void restorePreviousState() {
        if (homeViewModel.isContextualToolEnabled()) {
            showContextualToolBar();
            setTitleForContextualToolBar();
        }

        Log.d(TAG, "restorePreviousState: " + homeViewModel.getCardsChecked());
    }

    private void setDrawerAnimation() {

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX);
                float scaleFactor = 4f;
                content.setScaleX(1 - (slideOffset / scaleFactor));
                content.setScaleY(1 - (slideOffset / scaleFactor));
                content.setAlpha(1 - (slideOffset * 1.5f / scaleFactor));
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                if (actionMode != null) actionMode.finish();
            }


        };
        drawerLayout.setDrawerElevation(0f);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

    }


    private void subscribeObservers() {
        homeViewModel.getAllNotes().observe(this, notes -> {
            List<Note> oldNotes = new ArrayList<>(notesList);
            notesList.clear();
            notesList.addAll(notes);
            if (oldNotes.size() > notesList.size()) {
                notesRecyclerAdapter.notifyDataSetChanged();
            } else {
                DiffUtil.DiffResult result = DiffUtil.calculateDiff(new NotesDiffCallBack(oldNotes, notesList));
                notesRecyclerAdapter.setNotesList(notesList);
                result.dispatchUpdatesTo(notesRecyclerAdapter);
                notesRecycler.invalidateItemDecorations();
                notesRecycler.smoothScrollToPosition(0);
            }
        });

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
        notesRecycler.addItemDecoration(new SpacesItemDecoration(20));
        notesRecyclerAdapter = new NotesRecyclerAdapter(this, this, homeViewModel.getCardsChecked());
        notesRecycler.setAdapter(notesRecyclerAdapter);
        notesRecyclerAdapter.setNotesList(notesList);
        notesRecyclerAdapter.notifyDataSetChanged();
        String name = homeViewModel.getDrawerHeading();
        if (name != null) {
            name = getString(R.string.welcome) + "\n" + name;
            headingDrawer.setText(name);
        }
        setDrawerAnimation();

        EditText searchEditText = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getColor(R.color.white));
        searchEditText.setHintTextColor(getColor(R.color.white));
    }

    private void setClickListeners() {
        topAppBar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.LEFT);
        });

        topAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.rate_us:
                    Rate();
                    return true;
                case R.id.share:
                    ShareApp();
                    return true;
            }
            return false;
        });

        addNewNote.setOnClickListener(v -> AddNewNote());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchNote(newText);
                return true;
            }
        });

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
            homeViewModel.setContextualToolEnabled(true);
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
                    showDeleteConfirmation();
                    break;
                //case R.id.select_all_notes:
                //    checkAllNotes();
                //    break;
                default:
                    Snackbar.make(notesRecycler, getString(R.string.error_msg), Snackbar.LENGTH_SHORT).show();
            }


            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            homeViewModel.setContextualToolEnabled(false);
            UncheckAllNotes();
        }
    };

    public void showContextualToolBar() {
        actionMode = startSupportActionMode(callback);
        assert actionMode != null;
        actionMode.setTitle(getString(R.string.selected));
        hideFAB();
    }

    public void showFAB() {
        addNewNote.setVisibility(View.VISIBLE);
    }

    public void hideFAB() {
        addNewNote.setVisibility(View.GONE);
    }


    @Override
    public void onNoteClickListener(int position, MaterialCardView cardView) {
        if (homeViewModel.getTotalCardsChecked() > 0 || homeViewModel.isContextualToolEnabled()) {
            toggleCardCheck(cardView);
        } else if (homeViewModel.getTotalCardsChecked() == 0) {
            NoteEditor(position);
        }
    }

    @Override
    public boolean onNoteLongClickListener(int position, MaterialCardView cardView) {
        Log.d(TAG, "onNoteLongClickListener: " + position + " " + cardView.getTag());
        if (homeViewModel.getTotalCardsChecked() == 0 && !homeViewModel.isContextualToolEnabled()) {
            showContextualToolBar();
            toggleCardCheck(cardView);
        } else if (homeViewModel.getTotalCardsChecked() > 0 && !cardView.isChecked()) {
            toggleCardCheck(cardView);
        }
        return true;
    }

    @Override
    public void onCheckedChangeListener(MaterialCardView cardView, boolean isChecked) {
        // do nothing
        Log.d(TAG, "onCheckedChangeListener: " + cardView.getTag());
    }

    public void onCheckedChange(MaterialCardView cardView, boolean isChecked) {
        int cardId = (int) cardView.getTag();
        int totalCardsChecked = homeViewModel.getTotalCardsChecked();
        Log.d(TAG, "onCheckedChangeListener: " + totalCardsChecked);
        if (isChecked) {
            if (!homeViewModel.CardsCheckedContains(cardId)) {
                homeViewModel.CardsCheckedAdd(cardId);
            }
            if (totalCardsChecked < notesList.size())
                homeViewModel.setTotalCardsChecked(totalCardsChecked + 1);
        } else {
            if (homeViewModel.CardsCheckedContains(cardId)) {
                homeViewModel.CardsCheckedRemove(cardId);
            }
            if (totalCardsChecked > 0)
                homeViewModel.setTotalCardsChecked(totalCardsChecked - 1);
        }
        setTitleForContextualToolBar();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);
        else if(!searchView.isIconified()) searchView.onActionViewCollapsed();
        else super.onBackPressed();
    }


    private void setTitleForContextualToolBar() {
        int totalCardsChecked = homeViewModel.getTotalCardsChecked();
        Log.d(TAG, "setTitleForContextualToolBar: " + totalCardsChecked);
        if (actionMode != null) {
            if (totalCardsChecked == 0) actionMode.setTitle(getString(R.string.select_items));
            String titleHelp = getString(R.string.selected);
            if (totalCardsChecked > 1) titleHelp = getString(R.string.items_selected);
            actionMode.setTitle(totalCardsChecked + " " + titleHelp);
        }
    }

    public void NoteEditor(int position) {
        if (!isBackupOrSyncing()) {
            Note note = notesList.get(position);
            NewNoteDialog newNoteDialog = new NewNoteDialog(note);
            newNoteDialog.display(getSupportFragmentManager(), note);
        }
    }

    public void toggleCardCheck(MaterialCardView cardView) {
        Log.d(TAG, "toggleCardCheck: " + cardView.getTag());
        cardView.setChecked(!cardView.isChecked());
        onCheckedChange(cardView, cardView.isChecked());
    }

    public void UncheckAllNotes() {
        homeViewModel.CardsCheckedClear();
        homeViewModel.setTotalCardsChecked(0);
        notesRecyclerAdapter.notifyDataSetChanged();
        showFAB();
    }

//    public void checkAllNotes() {
//        homeViewModel.CardsCheckedClear();
//        try {
//            for (int i = 0; i < notesList.size(); i++) {
//                toggleCardCheck((MaterialCardView) notesRecycler.getLayoutManager().findViewByPosition(i));
//                Log.d(TAG, "checkAllNotes: " + i);
//                homeViewModel.CardsCheckedAdd(i);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private void shareNote() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                getSelectedNotes());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private String getSelectedNotes() {
        List<Integer> cardsChecked = homeViewModel.getCardsChecked();
        StringBuilder shareNotes = new StringBuilder();
        for (int position : cardsChecked) {
            Note note = notesList.get(position);
            shareNotes.append("Note :-\nTitle: ")
                    .append(note.getTitle())
                    .append("\nContent: ")
                    .append(note.getContent())
                    .append("\nDate: ")
                    .append(note.getDate());
        }
        return shareNotes.toString();
    }

    // *************** DATABASE OPERATIONS METHODS ****************

    private void showDeleteConfirmation() {
        int notes = homeViewModel.getTotalCardsChecked();
        if (notes > 0) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.delete_notes))
                    .setMessage(getString(R.string.delete) + " "
                            + notes +" "
                            + getString(R.string.note_s))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.cancel), ((dialog, which) -> dialog.dismiss()))
                    .setPositiveButton(getString(R.string.delete), ((dialog, which) -> deleteNote()))
                    .show();
        }else {
            Snackbar.make(notesRecycler, getString(R.string.no_note_selected), Snackbar.LENGTH_SHORT).show();
        }
    }


    private void deleteNote() {
        List<Integer> cardsChecked = homeViewModel.getCardsChecked();
        for (int position : cardsChecked) {
            homeViewModel.DeleteNote(notesList.get(position));
        }
        Snackbar.make(notesRecycler, "Deleted " + cardsChecked.size() + " Note(s)", Snackbar.LENGTH_SHORT).show();
        actionMode.finish();
    }

    public void searchNote(String query) {
        searchNotesList.clear();
        for (Note note : notesList) {
            Pattern pattern =  Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(note.getTitle()).find() ||pattern.matcher(note.getContent()).find()
                    ||pattern.matcher(note.getDate()).find()) {
                searchNotesList.add(note);
            }
        }
        notesRecyclerAdapter.setNotesList(searchNotesList);
        notesRecyclerAdapter.notifyDataSetChanged();
    }


    private void AddNewNote() {
        if (!isBackupOrSyncing()) {
            NewNoteDialog newNoteDialog = new NewNoteDialog(null);
            newNoteDialog.display(getSupportFragmentManager(), null);
        }
    }

    public boolean isBackupOrSyncing() {
        if (IS_SYNC || IS_BACKUP) {
            Snackbar.make(notesRecycler, "Sync or Backup is in progress. Please try after some time!", Snackbar.LENGTH_LONG).show();
            return true;
        } else {
            return false;
        }
    }


    // ****************************** Drawer Methods ******************************

    public void OnDrawerItemClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.profile_btn:
                Profile();
                break;
            case R.id.about_btn:
                getAbout(1);
                break;
            case R.id.feedback_btn:
                getAbout(0);
                break;
            case R.id.exit_btn:
                finish();
                break;
            default:
                break;
        }
    }


    private void Profile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void getAbout(int i) {
        Intent intent = new Intent(this, AboutActivity.class);
        if (i == 0) {
            intent.putExtra("Feedback", true);
        }
        startActivity(intent);
    }


}