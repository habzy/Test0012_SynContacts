package com.habzy.syncontacts;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AccountAuthenticatorActivity implements
        OnClickListener
{
    
    public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
    
    public static final String PARAM_PASSWORD = "password";
    
    public static final String PARAM_USERNAME = "username";
    
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    
    private static final String TAG = "LoginActivity";
    
    private Button mConfirmBt;
    
    private EditText mAccountEt;
    
    private EditText mPasswordEt;
    
    private Button mCancelBt;
    
    private AccountManager mAccountManager;
    
    /** Was the original caller asking for an entirely new account? */
    protected boolean mRequestNewAccount = false;
    
    private String mAuthtokenType;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);
        
        final Intent intent = getIntent();
        mAuthtokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        
        mAccountManager = AccountManager.get(this);
        
        mConfirmBt = (Button) findViewById(R.id.confirm);
        mCancelBt = (Button) findViewById(R.id.cancel);
        
        mAccountEt = (EditText) findViewById(R.id.account);
        
        mPasswordEt = (EditText) findViewById(R.id.password);
        
        mRequestNewAccount = (intent.getStringExtra(PARAM_USERNAME) == null);
        
        mConfirmBt.setOnClickListener(this);
        mCancelBt.setOnClickListener(this);
        
        Log.e(TAG, "**********onCreate");
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirm:
            {
                showProgress();
                new Thread()
                {
                    public void run()
                    {
                        String userName = mAccountEt.getText().toString();
                        String passWord = mPasswordEt.getText().toString();
                        Log.i(TAG, "============confirm()--");
                        final Account account = new Account(userName,
                                Constants.ACCOUNT_TYPE);
                        if (mRequestNewAccount)
                        {
                            mAccountManager.addAccountExplicitly(account,
                                    passWord,
                                    null);
                            // Set contacts sync for this account.
                            ContentResolver.setSyncAutomatically(account,
                                    ContactsContract.AUTHORITY,
                                    true);
                        }
                        else
                        {
                            mAccountManager.setPassword(account, passWord);
                        }
                        
                        final Intent intent = new Intent();
                        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME,
                                userName);
                        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE,
                                Constants.ACCOUNT_TYPE);
                        setAccountAuthenticatorResult(intent.getExtras());
                        if (mAuthtokenType != null
                                && mAuthtokenType.equals(Constants.AUTHTOKEN_TYPE))
                        {
                            intent.putExtra(AccountManager.KEY_AUTHTOKEN,
                                    passWord);
                        }
                        setAccountAuthenticatorResult(intent.getExtras());
                        setResult(RESULT_OK, intent);
                        finish();
                        
                    };
                }.start();
                
                break;
            }
            case R.id.cancel:
            {
                finish();
                break;
            }
            default:
                break;
        }
        
    }
    
    /**
     * Shows the progress UI for a lengthy operation.
     */
    protected void showProgress()
    {
        showDialog(0);
    }
}