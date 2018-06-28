/*
	Copyright 2017 IBM Corp.
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.ibm.cloud.appid.android.internal.authorizationmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;

import com.ibm.cloud.appid.android.api.AuthorizationException;
import com.ibm.cloud.appid.android.api.AuthorizationListener;
import com.ibm.cloud.appid.android.internal.OAuthManager;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;

import java.math.BigInteger;


public class ChromeTabActivity extends Activity {

    public static final String EXTRA_REDIRECT_URI = "com.ibm.cloud.appid.android.EXTRA_REDIRECT_URI";
    public static final String INTENT_GOT_HTTP_REDIRECT = "com.ibm.cloud.appid.android.GOT_HTTP_REDIRECT";
    private static final String INTENT_ALREADY_USED = "com.ibm.cloud.appid.android.INTENT_ALREADY_USED";
    private String postAuthorizationIntent = ".POST_AUTHORIZATION_INTENT";

    private static String FORGOT_PASSWORD = "forgot_password";
    private static String SIGN_UP = "sign_up";

    private BroadcastReceiver broadcastReceiver;
    private AuthorizationListener authorizationListener;
    private OAuthManager oAuthManager;
    private String redirectUrl;

    private static final Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + ChromeTabActivity.class.getName());

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        logger.debug("onCreate");
        super.onCreate(savedInstanceBundle);
        postAuthorizationIntent = getApplicationContext().getPackageName() + postAuthorizationIntent;
        Intent intent = getIntent();
        if (!postAuthorizationIntent.equals(intent.getAction())) {

            String serverUrl = getIntent().getStringExtra(AuthorizationUIManager.EXTRA_URL);
            this.redirectUrl = getIntent().getStringExtra(AuthorizationUIManager.EXTRA_REDIRECT_URL);

            String authFlowContextGuid = getIntent().getStringExtra(AuthorizationUIManager.EXTRA_AUTH_FLOW_CONTEXT_GUID);
            AuthorizationFlowContext ctx = AuthorizationFlowContextStore.remove(authFlowContextGuid);
            this.oAuthManager = ctx.getOAuthManager();
            this.authorizationListener = ctx.getAuthorizationListener();

            logger.debug("serverUrl: " + serverUrl);
            logger.debug("redirectUrl: " + redirectUrl);


            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.enableUrlBarHiding();
            CustomTabsIntent customTabsIntent = builder.build();

            customTabsIntent.intent.setPackage(AuthorizationUIManager.getPackageNameToUse(this.getApplicationContext()));
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onBroadcastReceived(intent);
                }
            };

            IntentFilter intentFilter = new IntentFilter(INTENT_GOT_HTTP_REDIRECT);
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

            //This will launch the chrome tab
            Uri uri = Uri.parse(serverUrl);
            logger.debug("launching custom tab with url: " + uri.toString());
            customTabsIntent.launchUrl(this, uri);
        } else {
            //if we launch after authorization completed
            finish();
        }
    }

    private void onBroadcastReceived(Intent intent) {
        Uri uri = intent.getParcelableExtra(ChromeTabActivity.EXTRA_REDIRECT_URI);
        String url = uri.toString();
        String code = uri.getQueryParameter("code");
        String error = uri.getQueryParameter("error");
        String flow = uri.getQueryParameter("flow");
        String state = uri.getQueryParameter("state");
        if (state != null){
            state = new String(Base64.decode(state, Base64.URL_SAFE));
        }
        String savedState = oAuthManager.getAuthorizationManager().getStateParameter();
        logger.info("onBroadcastReceived: " + url);

        Intent clearTopActivityIntent = new Intent(postAuthorizationIntent);
        clearTopActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (url.startsWith(redirectUrl) && code != null && state.equals(savedState)) {
            logger.debug("Grant code received from authorization server.");
            oAuthManager.getTokenManager().obtainTokensAuthCode(code, authorizationListener);
            startActivity(clearTopActivityIntent);
        } else if (url.startsWith(redirectUrl) && error != null) {
            if (error.equals("invalid_client")) {
                oAuthManager.getRegistrationManager().clearRegistrationData();
                oAuthManager.getAuthorizationManager().launchAuthorizationUI(this, authorizationListener);
            } else {
                String errorCode = uri.getQueryParameter("error_code");
                String errorDescription = uri.getQueryParameter("error_description");
                logger.error("Failed to obtain access and identity tokens, error: " + error);
                logger.error("errorCode: " + errorCode);
                logger.error("errorDescription: " + errorDescription);
                authorizationListener.onAuthorizationFailure(new AuthorizationException(error));
                startActivity(clearTopActivityIntent);
            }
        } else if (url.startsWith(redirectUrl) && (FORGOT_PASSWORD.equals(flow) || SIGN_UP.equals(flow))) {
            logger.debug("onBroadcastReceived: end of flow: " + flow);
            authorizationListener.onAuthorizationSuccess(null, null, null);
            startActivity(clearTopActivityIntent);
        } else {
            logger.debug("onBroadcastReceived: no match case");
            authorizationListener.onAuthorizationFailure(new AuthorizationException("Bad callback uri"));
            startActivity(clearTopActivityIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (!intent.hasExtra(INTENT_ALREADY_USED)) {
            // First time onResume called we add INTENT_ALREADY_USED=true
            // to indicate that this intent was already active for the next time,
            // after the chrome tab closes by user.
            intent.putExtra(INTENT_ALREADY_USED, true);
        } else {
            // User cancelled authentication
            finish();
            authorizationListener.onAuthorizationCanceled();
        }
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }
}
