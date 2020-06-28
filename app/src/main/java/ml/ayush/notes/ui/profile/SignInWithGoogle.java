package ml.ayush.notes.ui.profile;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import ml.ayush.notes.R;

public class SignInWithGoogle {

    Context context;

    // Configure Google Sign In
    GoogleSignInOptions gso;

    GoogleSignInClient mGoogleSignInClient;

    public SignInWithGoogle(Context context) {
        this.context = context;
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context.getApplicationContext(), gso);

    }

    public Intent signInIntent() {
        return mGoogleSignInClient.getSignInIntent();
    }


}
