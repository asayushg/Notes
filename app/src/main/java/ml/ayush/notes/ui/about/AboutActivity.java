package ml.ayush.notes.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import ml.ayush.notes.R;

import static ml.ayush.notes.utils.Constants.EMAIL_ID;
import static ml.ayush.notes.utils.Constants.EMAIL_SUBJECT;
import static ml.ayush.notes.utils.Constants.GITHUB_PROFILE;
import static ml.ayush.notes.utils.Constants.INSTAGRAM_PROFILE;
import static ml.ayush.notes.utils.Constants.LINKEDIN_PROFILE;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.action_bar_about)
    MaterialToolbar topAppBar;
    private AboutViewModel aboutViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (getIntent().getBooleanExtra("Feedback", false)) {
            dialogFeedBack(findViewById(R.id.ic_github));
        }
        ButterKnife.bind(this);
        aboutViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication()))
                .get(AboutViewModel.class);
        topAppBar.setNavigationOnClickListener(v -> {
            super.onBackPressed();
        });

    }

    public void OnSocialMediaClick(View v) {
        switch (v.getId()) {
            case R.id.ic_gmail:
                sendEmail();
                break;
            case R.id.ic_github:
                openGithub();
                break;
            case R.id.ic_linkedin:
                openLinkedIn();
                break;
            case R.id.ic_instagram:
                openInstagram();
                break;
        }
    }

    private void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_ID});
        email.putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT);

        //need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    private void openGithub() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(GITHUB_PROFILE));
        startActivity(i);
    }

    private void openLinkedIn() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(LINKEDIN_PROFILE));
        startActivity(i);
    }

    private void openInstagram() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(INSTAGRAM_PROFILE));
        startActivity(i);
    }

    public void dialogFeedBack(View v) {
        String[] type = {"Suggestion", "Issue"};
        AtomicInteger checkedItem = new AtomicInteger();

        final EditText input = new EditText(AboutActivity.this);
        input.setBackgroundColor(getColor(R.color.white));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(5, 5, 5, 5);
        input.setPadding(50, 15, 50, 15);
        input.setHint(getString(R.string.type_here));
        input.setLayoutParams(lp);
        new MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setTitle(getString(R.string.feedback_title))
                .setNeutralButton(getString(R.string.cancel), (dialog, which) -> {

                })
                .setPositiveButton(getString(R.string.send), (dialog, which) -> {
                    String s = input.getText().toString();
                    if (!s.isEmpty()) {
                        sendFeedback(type[checkedItem.get()], s);
                        Toast.makeText(this, getString(R.string.thanks_note), Toast.LENGTH_SHORT).show();
                    }
                })
                .setSingleChoiceItems(type, checkedItem.get(), (dialog, which) -> {
                    checkedItem.set(which);

                })
                .setView(input)
                .show();
    }

    private void sendFeedback(String type, String input) {
        if (!type.isEmpty() && !input.isEmpty()){
            aboutViewModel.sendFeedback(type, input);
        }
    }
}
