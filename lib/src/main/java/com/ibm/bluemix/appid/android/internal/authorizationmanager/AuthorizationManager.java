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

package com.ibm.bluemix.appid.android.internal.authorizationmanager;

import android.app.Activity;
import android.net.Uri;

import com.ibm.bluemix.appid.android.api.AppID;
import com.ibm.bluemix.appid.android.api.AuthorizationException;
import com.ibm.bluemix.appid.android.api.AuthorizationListener;
import com.ibm.bluemix.appid.android.api.tokens.AccessToken;
import com.ibm.bluemix.appid.android.internal.OAuthManager;
import com.ibm.bluemix.appid.android.internal.config.Config;
import com.ibm.bluemix.appid.android.internal.network.AppIDRequest;
import com.ibm.bluemix.appid.android.internal.registrationmanager.RegistrationListener;
import com.ibm.bluemix.appid.android.internal.registrationmanager.RegistrationManager;
import com.ibm.bluemix.appid.android.internal.tokens.AccessTokenImpl;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.Logger;

import org.json.JSONObject;

public class AuthorizationManager {
	private static final String OAUTH_AUTHORIZATION_PATH = "/authorization";

	private final AppID appId;
	private final OAuthManager oAuthManager;
	private final RegistrationManager registrationManager;

	private final static String CLIENT_ID = "client_id";
	private final static String RESPONSE_TYPE = "response_type";
	private final static String RESPONSE_TYPE_CODE = "code";

	private final static String SCOPE = "scope";
	private final static String SCOPE_OPENID = "openid";

	private final static String REDIRECT_URI = "redirect_uri";
	private final static String USE_LOGIN_WIDGET = "use_login_widget";
	private final static String IDP = "idp";
	private final static String ACCESS_TOKEN = "appid_access_token";



	private final static Logger logger = Logger.getLogger(Logger.INTERNAL_PREFIX + AuthorizationManager.class.getName());

	// TODO: document
	public AuthorizationManager (final OAuthManager oAuthManager) {
		this.oAuthManager = oAuthManager;
		this.appId = oAuthManager.getAppId();
		this.registrationManager = oAuthManager.getRegistrationManager();
	}

	// TODO: document
	public String getAuthorizationUrl(boolean useLoginWidget, String idpName, AccessToken accessToken) {
		String clientId = registrationManager.getRegistrationDataString(RegistrationManager.CLIENT_ID);
		String redirectUri = registrationManager.getRegistrationDataString(RegistrationManager.REDIRECT_URIS, 0);

		String serverUrl = Config.getServerUrl(this.appId) + OAUTH_AUTHORIZATION_PATH;
		Uri.Builder builder = Uri.parse(serverUrl).buildUpon()
				.appendQueryParameter(RESPONSE_TYPE, RESPONSE_TYPE_CODE)
				.appendQueryParameter(CLIENT_ID, clientId)
				.appendQueryParameter(REDIRECT_URI, redirectUri)
				.appendQueryParameter(SCOPE, SCOPE_OPENID)
				.appendQueryParameter(USE_LOGIN_WIDGET, String.valueOf(useLoginWidget));

		if (idpName != null){
			builder.appendQueryParameter(IDP, idpName);
		}
		if(accessToken != null){
			builder.appendQueryParameter(ACCESS_TOKEN, accessToken.getRaw());
		}
		return builder.build().toString();
	}
	public void launchAuthorizationUI (final Activity activity, final AuthorizationListener authorizationListener){
		launchAuthorizationUI(activity, authorizationListener, null);
	}

	// TODO: document
	public void launchAuthorizationUI (final Activity activity, final AuthorizationListener authorizationListener, final AccessToken accessToken){
		registrationManager.ensureRegistered(activity, new RegistrationListener() {
			@Override
			public void onRegistrationFailure (String message) {
				logger.error(message);
				authorizationListener.onAuthorizationFailure(new AuthorizationException(message));
			}

			@Override
			public void onRegistrationSuccess () {
				String authorizationUrl = getAuthorizationUrl(true, null, accessToken);
				String redirectUri = registrationManager.getRegistrationDataString(RegistrationManager.REDIRECT_URIS, 0);
				AuthorizationUIManager auim = new AuthorizationUIManager(oAuthManager, authorizationListener, authorizationUrl, redirectUri);
				auim.launch(activity);
			}
		});
	}

	private void registerAndLoginAnon(final String accessTokenString, final AuthorizationListener authorizationListener){

		registrationManager.ensureRegistered(AppID.getInstance().getContext(), new RegistrationListener() {
			@Override
			public void onRegistrationFailure (String message) {
				logger.error(message);
				authorizationListener.onAuthorizationFailure(new AuthorizationException(message));
			}

			@Override
			public void onRegistrationSuccess () {
				continueAnonLogin( accessTokenString, authorizationListener);
			}
		});
	}

	public void continueAnonLogin(String accessTokenString, final AuthorizationListener listener){
		AccessToken accessToken;
		if (accessTokenString == null){
			accessToken = oAuthManager.getTokenManager().getLatestAccessToken();
		} else {
			accessToken = new AccessTokenImpl(accessTokenString);
		}
		String authorizationUrl = getAuthorizationUrl(false, AccessTokenImpl.IDP_ANONYMOUS, accessToken);


		AppIDRequest request = new AppIDRequest(authorizationUrl, AppIDRequest.GET);
		request.send(new ResponseListener(){
						 @Override
						 public void onSuccess(Response response) {
							 if (logger.isSDKDebugLoggingEnabled()){
								 logger.debug("loginAnonymously.Response in onSuccess:" + response.getResponseText());
							 }
							 String location = response.getHeaders().get("Location").toString();
							 String locationUrl = location.substring(1,location.length()-1); // removing []
							 String code= Uri.parse(locationUrl).getQueryParameter("code");

							 oAuthManager.getTokenManager().obtainTokens(code, listener);
						 }

						 @Override
						 public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
							 if (logger.isSDKDebugLoggingEnabled()) {
								 String message = response == null ? "" : response.getResponseText();
								 logger.debug("loginAnonymously.Response in onFailure:" + message, t);
							 }
							 String message = t != null ? t.getLocalizedMessage() : "Authorization request failed.";
							 message = extendedInfo !=null ? message + extendedInfo.toString() : message;
							 listener.onAuthorizationFailure( new AuthorizationException(message));
						 }
					 }

		);
	}

	public void loginAnonymously(String accessTokenString, final AuthorizationListener listener){
		registerAndLoginAnon(accessTokenString, listener);
	}
}
