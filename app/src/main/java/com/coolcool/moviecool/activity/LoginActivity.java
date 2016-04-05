package com.coolcool.moviecool.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
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
import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.utils.PasswordUtils;
import com.coolcool.moviecool.utils.RegexUtils;
import com.coolcool.moviecool.model.OrdinaryUser;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
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
    private UserLoginTask mLoginTask = null;

    // UI references.
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
        checkUserFile();
    }

    private void initView(){
        // Set up the login form.
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.userName);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        if (mEmailSignInButton!=null)
            mEmailSignInButton.setOnClickListener(this);

        Button mEmailLoginButton = (Button) findViewById(R.id.email_login_button);
        if (mEmailLoginButton!=null)
            mEmailLoginButton.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.sign_in_form);
        mProgressView = findViewById(R.id.sign_in_progress);
    }

//    private void populateAutoComplete() {
//        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
//    }

    LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks
            =new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            return new CursorLoader(LoginActivity.this
                    // Retrieve data rows for the device user's 'profile' contact.
                    ,Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI
                    , ContactsContract.Contacts.Data.CONTENT_DIRECTORY)
                    , ProfileQuery.PROJECTION
                    // Select only email addresses.
                    ,ContactsContract.Contacts.Data.MIMETYPE + " = ?"
                    , new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE}

                    // Show primary email addresses first. Note that there won't be
                    // a primary email address if the user hasn't specified one.
                    ,ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            List<String> emails = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                emails.add(cursor.getString(ProfileQuery.ADDRESS));
                cursor.moveToNext();
            }

            addEmailsToAutoComplete(emails);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {

        }
    };

    //确认输入是否符合要求
    private boolean checkInput(String userName,String password){
        // Check for a valid userName.
        if (RegexUtils.checkUserName(userName, 2, 20)!=null) {
            mUserNameView.setError(RegexUtils.checkUserName(userName, 2, 20));
            mUserNameView.requestFocus();
            return false;
        }

        // Check for a valid password, if the user entered one.
        if (RegexUtils.checkPassword(password, 6, 9)!=null) {
            mPasswordView.setError(RegexUtils.checkPassword(password, 6, 9));
            mPasswordView.requestFocus();
            return false;
        }

        return true;
    }

    private void checkUserFile(){
        SharedPreferences preferences= getSharedPreferences(Constant.USER_ACCOUNT_FILE, MODE_PRIVATE);
        boolean isRemember=preferences.getBoolean(Constant.USER_PASSWORD_SAVED_STATE,false);
        if (isRemember){
            String account=preferences.getString(Constant.USER_ACCOUNT,"");
            String p=preferences.getString(Constant.USER_PASSWORD,"");
            long savedTime=preferences.getLong(Constant.USER_PASSWORD_SAVED_TIME, 0);
            String password=PasswordUtils.decodePassword(p,savedTime);
            long currentTime=System.currentTimeMillis();
            //用户保存的秘密期限为30天
            if (currentTime-(long)30*24*60*60*1000>savedTime){
                //密码已过期，让用户重新输入密码
                password=null;
                if (!account.equals("")){
                    mUserNameView.setText(account);
                    mPasswordView.requestFocus();
                }else {
                    mUserNameView.requestFocus();
                }
                Constant.ONLINE_STATE=false;
                Log.i(TAG,"保存的密码已过期，账号名字=="+account);
            }else {
                if (password!=null&&!password.equals("")){
                    mPasswordView.setText(password);
                    mPasswordView.requestFocus();
                }
                if (!account.equals("")){
                    mUserNameView.setText(account);
                    mUserNameView.requestFocus();
                }
                Log.i(TAG, "账号名字==" + account+"账号密码==" + password);
            }
        }else {
            Constant.ONLINE_STATE=false;
            Log.i(TAG, "没有保存的密码文件");
        }
    }

    //尝试去登陆
    private void attemptLogin() {
//        if (mLoginTask!=null){
//            return;
//        }

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String userName = mUserNameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        if (checkInput(userName,password)){
//            mSignInTask = new UserSignInTask(email, password);
//            mSignInTask.execute((Void) null);

            OrdinaryUser bu=new OrdinaryUser(this);
            bu.setUsername(userName);
            bu.setPassword(password);
            showProgress(true);
            bu.login(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    showProgress(false);

                    //保存用户账号密码
                    saveUserAccount(userName,password);
                    Constant.ONLINE_STATE =true;
                    OrdinaryUser user= BmobUser
                            .getCurrentUser(LoginActivity.this, OrdinaryUser.class);
                    if (user!=null) Constant.ordinaryUser=user;
                    Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
//                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(int i, String s) {
                    showProgress(false);

                    //保存用户账号，但不保存密码
                    saveUserAccount(userName,null);
                    Log.i(TAG, "登陆失败:i==" + i + "  原因" + s);
                    Toast.makeText(LoginActivity.this
                            , i + "登陆失败：\n" +
                            "网络异常", Toast.LENGTH_SHORT).show();
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
        editor.putString(Constant.USER_PASSWORD, PasswordUtils.encodePassword(password, currentTime));
        editor.putLong(Constant.USER_PASSWORD_SAVED_TIME, currentTime);
        editor.putBoolean(Constant.USER_PASSWORD_SAVED_STATE, true);
        editor.commit();
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this
                , android.R.layout.simple_dropdown_item_1line
                , emailAddressCollection);

        mUserNameView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.email_login_button:
                attemptLogin();
                break;
            case R.id.email_sign_in_button:
                Intent intent=new Intent(LoginActivity.this,SignInActivity.class);
                String account=mUserNameView.getText().toString();
                String password=mPasswordView.getText().toString();
                intent.putExtra(Constant.USER_ACCOUNT,account);
                intent.putExtra(Constant.USER_PASSWORD,password);
                startActivity(intent);
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            Log.i(TAG,"原始邮箱为："+email);
            Log.i(TAG,"原始密码为："+password);
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
                Log.i(TAG,"邮箱为："+pieces[0]);
                Log.i(TAG,"密码为："+pieces[1]);
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
            mLoginTask = null;
            showProgress(false);

            if (success) {
                Intent intent=new Intent(LoginActivity.this,BaseActivity.class);
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mLoginTask = null;
            showProgress(false);
        }
    }

}
