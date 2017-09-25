package com.amplifire.traves.feature.signin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifire.traves.R;
import com.amplifire.traves.feature.base.BaseActivity;
import com.amplifire.traves.feature.main.MainActivity;
import com.amplifire.traves.feature.signup.SignUpActivity;
import com.amplifire.traves.widget.AlertLoadingFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener {


    @BindView(R.id.edittextEmail)
    EditText edittextEmail;
    @BindView(R.id.edittextPassword)
    EditText edittextPassword;
    @BindView(R.id.textViewPass)
    IconTextView textViewPass;

    //google
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    //facebook
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        setView();
        setGoogle();
        setFacebook();
    }

    private void setView() {
        textViewPass.setOnTouchListener((v, event) -> {
            textViewPass.setText("{fa-eye}");
            if (event.getAction() == MotionEvent.ACTION_UP) {
                edittextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                textViewPass.setTextColor(ContextCompat.getColor(SignInActivity.this, android.R.color.darker_gray));
            } else {
                edittextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                textViewPass.setTextColor(ContextCompat.getColor(SignInActivity.this, R.color.red));
            }
            return true;
        });

    }

    private void signIn() {
        edittextEmail.setText(null);
        edittextPassword.setText(null);
        boolean isValid = true;

        String requirement = getString(R.string.error_requirement);
        if (TextUtils.isEmpty(edittextEmail.getText())) {
            isValid = false;
            edittextEmail.setError(requirement);
        }
        if (TextUtils.isEmpty(edittextPassword.getText())) {
            isValid = false;
            edittextPassword.setError(requirement);
        }
        if (isValid) {
            String email = edittextEmail.getText().toString();
            String password = edittextPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                signInFailed();
                            } else {
                                signInSuccess();
                            }
                        }
                    });
        }
    }


    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signInSuccess() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            //todo save as a json.
            MainActivity.startThisActivity(this);
        }
    }

    private void signInFailed() {
        //todo failed
        Toast.makeText(this, getString(R.string.text_login) + " " + getString(R.string.text_failed), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //signin google
    private void setGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AlertLoadingFragment.showAlert(this);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        AlertLoadingFragment.setDismiss(SignInActivity.this);
                        if (task.isSuccessful()) {
                            signInSuccess();
                        } else {
                            signInFailed();
                        }

                    }
                });
    }


    //facebook
    private void setFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void signInFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos", "email", "public_profile", "user_posts", "AccessToken"));
        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                signInFailed();
            }
        });


    }

    private void handleFacebookAccessToken(AccessToken token) {
        AlertLoadingFragment.showAlert(this);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        AlertLoadingFragment.setDismiss(SignInActivity.this);
                        if (task.isSuccessful()) {
                            signInSuccess();
                        } else {
                            signInFailed();
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                signInFailed();
            }
        }
    }


    @OnClick({R.id.login, R.id.buttonFacebook, R.id.buttonGoogle, R.id.buttonRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                signIn();
                break;
            case R.id.buttonFacebook:
                signInFacebook();
                break;
            case R.id.buttonGoogle:
                signInGoogle();
                break;
            case R.id.buttonRegister:
                SignUpActivity.startThisActivity(this);
                break;
        }
    }


    public static void startThisActivity(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


}
