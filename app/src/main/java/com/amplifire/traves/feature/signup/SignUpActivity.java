package com.amplifire.traves.feature.signup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifire.traves.R;
import com.amplifire.traves.utils.Utils;
import com.amplifire.traves.feature.base.BaseActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends BaseActivity implements SignUpContract.View {

    @BindView(R.id.edittextEmail)
    EditText edittextEmail;
    @BindView(R.id.edittextPassword)
    EditText edittextPassword;
    @BindView(R.id.edittextConfirmPassword)
    EditText edittextConfirmPassword;

    @Inject
    SignUpPresenter mSignUpPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        setTitle(getString(R.string.text_register));


    }


    private void signUp() {
        edittextEmail.setError(null);
        edittextPassword.setError(null);
        edittextConfirmPassword.setError(null);

        boolean isValid = true;
        String requirement = getString(R.string.error_requirement);

        if (TextUtils.isEmpty(edittextEmail.getText())) {
            isValid = false;
            edittextEmail.setError(requirement);
        } else {
            if (!Utils.isValidEmail(edittextEmail.getText().toString())) {
                isValid = false;
                edittextEmail.setError(getString(R.string.error_wrong_format));
            }
        }

        if (TextUtils.isEmpty(edittextPassword.getText())) {
            isValid = false;
            edittextPassword.setError(requirement);
        } else {
            if (edittextPassword.getText().length() < 6) {
                isValid = false;
                edittextPassword.setError(getString(R.string.text_password) + " " + getString(R.string.error_min_char, "6"));
            }
        }

        if (TextUtils.isEmpty(edittextConfirmPassword.getText())) {
            isValid = false;
            edittextConfirmPassword.setError(requirement);
        } else {
            if (isValid) {
                if (!(edittextConfirmPassword.getText() + "").equals(edittextPassword.getText() + "")) {
                    isValid = false;
                    edittextConfirmPassword.setError(getString(R.string.error_confirm) + " " + getString(R.string.text_failed));
                }
            }
        }

        if (isValid) {
            String email = edittextEmail.getText().toString();
            String password = edittextPassword.getText().toString();
            mSignUpPresenter.createUserEmail(email, password);
        }
    }


    public static void startThisActivity(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }

    @OnClick(R.id.signup)
    public void onViewClicked() {
        signUp();
    }

    @Override
    public void showAlert(boolean isShow) {
        super.showAlert(isShow);
    }

    @Override
    public void registerResult(Task<AuthResult> task) {
        if (!task.isSuccessful()) {
            Toast.makeText(SignUpActivity.this, getString(R.string.text_register) + " " + getString(R.string.text_failed) + "," + task.getException().getMessage(),
                    Toast.LENGTH_SHORT).show();
            showAlert(false);
        } else {
            Toast.makeText(SignUpActivity.this, getString(R.string.text_register) + " " + getString(R.string.text_success),
                    Toast.LENGTH_SHORT).show();
            showAlert(false);
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSignUpPresenter.takeView(this);
    }


}
