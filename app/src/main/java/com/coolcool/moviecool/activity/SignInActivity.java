package com.coolcool.moviecool.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.activity.base.BaseActivity;
import com.coolcool.moviecool.common.Constant;
import com.coolcool.moviecool.model.OrdinaryUser;
import com.coolcool.moviecool.utils.PasswordUtils;
import com.coolcool.moviecool.utils.RegexUtils;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.EmailVerifyListener;
import cn.bmob.v3.listener.SaveListener;

public class SignInActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG="LoginActivity";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserSignInTask mSignInTask = null;

    // UI references.
    private AutoCompleteTextView mUserNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private View mProgressView;
    private View mSignInFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();

        Intent intent=getIntent();
        String account=intent.getStringExtra(Constant.USER_ACCOUNT);
        String password=intent.getStringExtra(Constant.USER_PASSWORD);
        if (account!=null&&!account.equals("")){
            mUserNameView.setText(account);
            mEmailView.requestFocus();
        }
        if (password!=null&&!password.equals("")){
            mPasswordView.setText(password);
        }
    }

    private void initView(){
        // Set up the login form.
        mUserNameView= (AutoCompleteTextView) findViewById(R.id.userName);
//        populateAutoComplete();

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordConfirmView= (EditText) findViewById(R.id.passwordConfirm);

        mPasswordConfirmView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.sign_in || id == EditorInfo.IME_NULL) {
                    attemptSignIn();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        if (mEmailSignInButton!=null)
            mEmailSignInButton.setOnClickListener(this);

        mSignInFormView = findViewById(R.id.sign_in_form);
        mProgressView = findViewById(R.id.sign_in_progress);
    }

//    private void populateAutoComplete() {
//        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
//    }

//    LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks
//            =new LoaderManager.LoaderCallbacks<Cursor>() {
//
//        @Override
//        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
//            return new CursorLoader(SignInActivity.this
//                    // Retrieve data rows for the device user's 'profile' contact.
//                    , Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI
//                    , ContactsContract.Contacts.Data.CONTENT_DIRECTORY)
//                    , ProfileQuery.PROJECTION
//                    // Select only email addresses.
//                    ,ContactsContract.Contacts.Data.MIMETYPE + " = ?"
//                    , new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE}
//
//                    // Show primary email addresses first. Note that there won't be
//                    // a primary email address if the user hasn't specified one.
//                    ,ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
//        }
//
//        @Override
//        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//            List<String> emails = new ArrayList<>();
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                emails.add(cursor.getString(ProfileQuery.ADDRESS));
//                cursor.moveToNext();
//            }
//
//            addEmailsToAutoComplete(emails);
//        }
//
//        @Override
//        public void onLoaderReset(Loader<Cursor> cursorLoader) {
//
//        }
//    };

    //确认输入是否符合要求
    private boolean checkInput(String userName,String email,String password,String passwordConfirm){
        // Check for a valid password, if the user entered one.
        if (RegexUtils.checkUserName(userName,2,20)!=null){
            mUserNameView.setError(RegexUtils.checkUserName(userName, 2, 20));
            mUserNameView.requestFocus();
            return false;
        }

        // Check for a valid email address.
        if (RegexUtils.checkEmailText(email)!=null) {
            mEmailView.setError(RegexUtils.checkEmailText(email));
            mEmailView.requestFocus();
            return false;
        }

        if (RegexUtils.checkPassword(password, 6, 9)!=null) {
            mPasswordView.setError(RegexUtils.checkPassword(password, 6, 9));
            mPasswordView.requestFocus();
            return false;
        }

        if (passwordConfirm==null||!passwordConfirm.equals(password)) {
            mPasswordConfirmView.setError("两次输入密码不同，请再次确认");
            mPasswordConfirmView.requestFocus();
            return false;
        }
        return true;
    }

    //尝试去注册
    private void attemptSignIn() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String userName=mUserNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String passwordConfirm=mPasswordConfirmView.getText().toString();

        if (checkInput(userName,email,password,passwordConfirm)){
            OrdinaryUser bu=new OrdinaryUser(this);
            bu.setUsername(userName);
            bu.setEmail(email);
            bu.setPassword(password);
            bu.setEmailVerified(false);
            showProgress(true);
            bu.signUp(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    showProgress(false);

                    //保存账户信息
                    saveUserAccount(userName, password);

                    OrdinaryUser user= BmobUser
                            .getCurrentUser(SignInActivity.this, OrdinaryUser.class);
                    if (user!=null) Constant.ordinaryUser=user;

//                    BmobRole ordinaryUser=new BmobRole("ordinaryUser");
//                    ordinaryUser.getUsers().add(user);
//                    ordinaryUser.save(SignInActivity.this);

                    //请求邮箱确认
                    OrdinaryUser.requestEmailVerify(SignInActivity.this, email, new EmailVerifyListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(SignInActivity.this
                                    , "注册成功,请登录邮箱确认", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(SignInActivity.this
                                    , "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    showProgress(false);
                    Log.i(TAG, "注册失败:i==" + i + "  原因" + s);

                    String errorText;
                    switch (i) {
                        case 202:
                            errorText = "注册失败：\n该用户名已被注册，请重新输入";
                            break;
                        case 203:
                            errorText = "注册失败：\n该邮箱已被注册，请重新输入";
                            break;
                        default:
                            errorText = "注册失败：\n" +
                                    "该用户名或邮箱已被注册，请重新输入";
                    }
                    Toast.makeText(SignInActivity.this
                            , i + errorText, Toast.LENGTH_SHORT).show();
                    Constant.ONLINE_STATE = false;
                }
            });
        }
    }

    //保存账户信息
    private void saveUserAccount(String userName,String password){
        SharedPreferences.Editor editor = getSharedPreferences(Constant.USER_ACCOUNT_FILE, MODE_PRIVATE).edit();
        editor.putString(Constant.USER_ACCOUNT, userName);
        long currentTime=System.currentTimeMillis();
        editor.putString(Constant.USER_PASSWORD, PasswordUtils.encodePassword(password,currentTime));
        editor.putLong(Constant.USER_PASSWORD_SAVED_TIME, currentTime);
        editor.putBoolean(Constant.USER_PASSWORD_SAVED_STATE, true);
        Constant.ONLINE_STATE = editor.commit();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignInFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignInFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignInFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignInFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this
                , android.R.layout.simple_dropdown_item_1line
                , emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.email_sign_in_button:
                attemptSignIn();
                break;
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    //用户注册的异步任务
    public class UserSignInTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserSignInTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.



            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSignInTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mSignInTask = null;
            showProgress(false);
        }
    }

}
