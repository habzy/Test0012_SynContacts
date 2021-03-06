/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.habzy.syncontacts.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.ParseException;

import com.habzy.syncontacts.Constants;
import com.habzy.syncontacts.platform.ContactManager;
import com.habzy.syncontacts.platform.User;
import com.habzy.syncontacts.platform.User.Status;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the
 * platform ContactOperations provider.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter
{
    private static final String TAG = "SyncAdapter";
    
    private final AccountManager mAccountManager;
    
    private final Context mContext;
    
    private Date mLastUpdated;
    
    private int mFakeUserId = 133;
    
    public SyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }
    
    private User getFakeUser()
    {
        User fakeUser = new User("Fake Nmae","Fake","Name","1234567",null,null,"fake@gmial.com",false ,mFakeUserId);
        return fakeUser;
    }
    
    private User getFakeUser2()
    {
        User fakeUser = new User("Fake2 Nmae","Fake2","Name","5555555",null,null,"fake2@gmial.com",false ,mFakeUserId+1);
        return fakeUser;
    }
    
    private Status getFakeStatus()
    {
        return new User.Status(mFakeUserId, "fake online");
    }
    
    private Status getFake2Status()
    {
        return new User.Status(mFakeUserId+1, "xx is xx");
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult)
    {
        
        Log.e(TAG, "=======onPerformSync==account.name:"+account.name);
        
        List<User> users;
        List<Status> statuses;
        String authtoken = null;
        try
        {
            // use the account manager to request the credentials
            authtoken = mAccountManager.blockingGetAuthToken(account,
                    Constants.AUTHTOKEN_TYPE,
                    true /* notifyAuthFailure */);
            ////TODO -  fetch updates from the service
            // users = NetworkUtilities.fetchFriendUpdates(account,
            // authtoken,
            // mLastUpdated);
            users = new ArrayList<User>();
            users.add(getFakeUser());
            users.add(getFakeUser2());
            // update the last synced date.
            mLastUpdated = new Date();
            // update platform contacts.
            Log.d(TAG, "Calling contactManager's sync contacts");
            ContactManager.syncContacts(mContext, account, users);
            //TODO - fetch and update status messages for all the synced users.
            // statuses = NetworkUtilities.fetchFriendStatuses(account,
            // authtoken);
            statuses = new ArrayList<User.Status>();
            statuses.add(getFakeStatus());
            statuses.add(getFake2Status());
            ContactManager.insertStatuses(mContext, account.name, statuses);
        }
        catch (final AuthenticatorException e)
        {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "AuthenticatorException", e);
        }
        catch (final OperationCanceledException e)
        {
            Log.e(TAG, "OperationCanceledExcetpion", e);
        }
        catch (final IOException e)
        {
            Log.e(TAG, "IOException", e);
            syncResult.stats.numIoExceptions++;
        }
//        catch (final AuthenticationException e)
//        {
//            mAccountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE,
//                    authtoken);
//            syncResult.stats.numAuthExceptions++;
//            Log.e(TAG, "AuthenticationException", e);
//        }
        catch (final ParseException e)
        {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "ParseException", e);
        }
//        catch (final JSONException e)
//        {
//            syncResult.stats.numParseExceptions++;
//            Log.e(TAG, "JSONException", e);
//        }
    }
}
