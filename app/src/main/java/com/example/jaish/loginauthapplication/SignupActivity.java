package com.example.jaish.loginauthapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jaish on 14-12-2018.
 */

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private Context signup_context;
    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_mobile)
    EditText _mobileText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;
    FirebaseAuth mAuth;
    String codeSent;
    PhoneAuthProvider.ForceResendingToken mResendToken=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        signup_context = this;
        mAuth = FirebaseAuth.getInstance();
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");
        hideKeyboardFrom(signup_context);
        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.acc_create));
        progressDialog.show();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success

                        if (!SqHelper.getDB(signup_context).CheckFieldsExist(_emailText.getText().toString())) {
                            onSignupSuccess();
                        } else {
                            _signupButton.setEnabled(true);
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.login_exists), Toast.LENGTH_LONG).show();
                        }
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSignupSuccess() {

        try {
            sendVerificationCode(_mobileText.getText().toString());
            _signupButton.setEnabled(true);
            checkOTP_Validation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendVerificationCode(String MobileNo) {
        if (MobileNo.isEmpty()) {
            _mobileText.setError(getResources().getString(R.string.mobile_verify_empty));
            _mobileText.requestFocus();
            return;
        }
        if (MobileNo.length() < 10) {
            _mobileText.setError(getResources().getString(R.string.mobile_verify));
            _mobileText.requestFocus();
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+971" + MobileNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted is provider ===>" + phoneAuthCredential.getProvider());
            Log.d(TAG, "onVerificationCompleted is smscode ===>" + phoneAuthCredential.getSmsCode());
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d(TAG, "onVerificationFailed is ===>" + e.getMessage());
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            Log.d(TAG, "onCodeSent code is ===>" + verificationId);
            codeSent = verificationId;
            mResendToken = forceResendingToken;
        }
    };

    private void checkOTP_Validation() {

        try {
            final Dialog dialog1 = new Dialog(signup_context);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.dialog_confirm);
            dialog1.setCanceledOnTouchOutside(false);
            final EditText _OTPnumbertext = (EditText) dialog1.findViewById(R.id.editTextOtp);
            Button verifySigInnCode = (Button) dialog1.findViewById(R.id.buttonConfirm);
            Button resend_SigInnCode = (Button) dialog1.findViewById(R.id.resend_Confirm);

            verifySigInnCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onCodeReceived code is ===>" + codeSent);

                    if (codeSent != null && !codeSent.equalsIgnoreCase("")) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, _OTPnumbertext.getText().toString());
                        signInWithPhoneAuthCredential(credential);
                    }
                    dialog1.dismiss();
                }
            });
            resend_SigInnCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_mobileText.getText().toString() != null && !_mobileText.getText().toString().equalsIgnoreCase("")) {
//                        sendVerificationCode(_mobileText.getText().toString());
                        resendVerificationCode(_mobileText.getText().toString(), mResendToken);

                    }
                }
            });
            dialog1.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken mResendToken) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+971" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,mResendToken);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userRegistration_success();
                           /* FirebaseUser user = task.getResult().getUser();*/
                            // ...
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.verification_failed), Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }

    private void userRegistration_success() {
        try {
            final LoginDetailsBean loginDetailsBean = new LoginDetailsBean();

            loginDetailsBean.setName(_nameText.getText().toString());
            loginDetailsBean.setEmailid(_emailText.getText().toString());
            loginDetailsBean.setPassword(_passwordText.getText().toString());
            loginDetailsBean.setMobileno(_mobileText.getText().toString());

            if (SqHelper.getDB(signup_context).insertorupdateLoginDetails(loginDetailsBean)) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError(getResources().getString(R.string.name_verify));
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getResources().getString(R.string.email_verify));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length() != 10) {
            _mobileText.setError(getResources().getString(R.string.mobile_verify));
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError(getResources().getString(R.string.password_verify));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError(getResources().getString(R.string.confirm_password_verify));
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    public void hideKeyboardFrom(Context context) {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
