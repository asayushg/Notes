package ml.ayush.notes.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ml.ayush.notes.R;
import ml.ayush.notes.ui.home.HomeActivity;

import static ml.ayush.notes.utils.Constants.BACKUP_SYNC;
import static ml.ayush.notes.utils.Constants.IS_BACKUP;
import static ml.ayush.notes.utils.Constants.IS_SYNC;
import static ml.ayush.notes.utils.Constants.RC_SIGN_IN;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ProfileViewModel profileViewModel;
    @BindView(R.id.user_info)
    TextView userInfo;
    @BindView(R.id.profile_pic)
    ImageView userImage;
    @BindView(R.id.action_bar_profile)
    MaterialToolbar topAppBar;
    @BindView(R.id.backUp_radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.signIn_with_google_btn)
    SignInButton signInButton;
    @BindView(R.id.progress_bar_profile)
    ProgressBar progressBar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        profileViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication()))
                .get(ProfileViewModel.class);

        subscribeObservers();
        topAppBar.setNavigationOnClickListener(v -> nextActivity());

        setRadioListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInResult(requestCode, data);
    }

    public void BackupNow(View v) {
        if (isNetworkAvailable()) {
            profileViewModel.setBackup(true);
            profileViewModel.BackupNow();
            showProgressBar();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo activeNetworkInfo = null;
        try {
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) return true;
        else {
            Snackbar.make(topAppBar, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    public void SyncNow(View v) {
        if (isNetworkAvailable()) {
            profileViewModel.setSync(true);
            profileViewModel.SyncNow();
            showProgressBar();
        }
    }

    private void subscribeObservers() {

        if (profileViewModel.isProgressShowing()) {
            showProgressBar();
        } else hideProgressBar();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startSignIn();
        } else updateUI(user);

        profileViewModel.HideProgressBar().observe(this, aBoolean -> {
            Log.d(TAG, "subscribeObservers: " + aBoolean);
            if (!aBoolean) showProgressBar();
            else hideProgressBar();
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        profileViewModel.setProgressShowing(true);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        profileViewModel.setProgressShowing(false);
    }

    private void startSignIn() {
        showProgressBar();
        SignInWithGoogle signInWithGoogle = new SignInWithGoogle(this);
        startActivityForResult(signInWithGoogle.signInIntent(), RC_SIGN_IN);
    }

    private void GoogleSignInResult(int requestCode, Intent data) {
        if (requestCode == RC_SIGN_IN && data != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        updateUI(Objects.requireNonNull(mAuth.getCurrentUser()));
                        SyncNow(radioGroup);
                        hideProgressBar();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getApplication(), "Sign-In Failed. Try again!", Toast.LENGTH_SHORT).show();
                    }
                    // ...
                });
    }

    private void updateUI(FirebaseUser user) {
        Glide.with(this)
                .load(user.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(userImage);
        String s = getString(R.string.welcome) + "\n\n" + user.getDisplayName() + "\n" + user.getEmail();
        userInfo.setText(s);
        signInButton.setVisibility(View.GONE);
    }

    private void setRadioListener() {
        signInButton.setOnClickListener(v -> startSignIn());

        switch (BACKUP_SYNC) {
            case 0:
                radioGroup.check(R.id.real_time_radio);
                break;
            case 1:
                radioGroup.check(R.id.daily_at_night_radio);
                break;
            case 2:
                radioGroup.check(R.id.weekly_radio);
                break;
            case 3:
                radioGroup.check(R.id.never_radio);
                break;
            default:
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkUser()) {
                switch (checkedId) {
                    case R.id.real_time_radio:
                        BACKUP_SYNC = 0;
                        Snackbar.make(radioGroup, getString(R.string.done), Snackbar.LENGTH_SHORT).show();
                        break;
                    case R.id.never_radio:
                        BACKUP_SYNC = 3;
                        Snackbar.make(radioGroup, getString(R.string.done), Snackbar.LENGTH_SHORT).show();
                        break;
                    case R.id.daily_at_night_radio:
                    case R.id.weekly_radio:
                        Snackbar.make(radioGroup, getString(R.string.coming_soon), Snackbar.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }
            } else
                Snackbar.make(radioGroup, getString(R.string.sign_in), Snackbar.LENGTH_SHORT).show();
        });

    }

    private boolean checkUser() {
        return mAuth.getCurrentUser() != null;
    }

    private void nextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
