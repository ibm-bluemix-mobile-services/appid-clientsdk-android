package com.ibm.mobilefirstplatform.appid_clientsdk_android;

import android.util.Base64;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.core.internal.ResponseImpl;
import com.ibm.mobilefirstplatform.clientsdk.android.security.mca.internal.AuthorizationRequest;
import com.ibm.mobilefirstplatform.clientsdk.android.security.mca.internal.AuthorizationRequestManager;
import com.ibm.mobilefirstplatform.clientsdk.android.security.mca.internal.preferences.AuthorizationManagerPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashMap;

/**
 * Created by rotembr on 11/12/2016.
 */

public class AppIdTokenManager {

    private static final String tokenPath = "/oauth/v3/";
    private AuthorizationManagerPreferences preferences;

    AppIdTokenManager(AuthorizationManagerPreferences preferences) {
        this.preferences = preferences;
    }

    void sendTokenRequest(String code) {
        String header = null;
        try {
            header = createTokenRequestHeaders();
            HashMap<String, String> params = new HashMap<>();
            params.put("code", code);
            params.put("client_id", preferences.clientId.get());
            params.put("grant_type", "authorization_code");
            params.put("redirect_uri", AppId.redirectUri);
            AuthorizationRequestManager.RequestOptions options = new AuthorizationRequestManager.RequestOptions();
            options.parameters = params;
            AuthorizationRequest request = null;
            request = new AuthorizationRequest(getTokenUrl(), Request.POST);
            request.addHeader("authorization", header);
            request.send(options.parameters, new ResponseListener() {
                @Override
                public void onSuccess(Response response) {
                    try {
                        saveTokenFromResponse(response);
                        AppIdAuthorizationManager.getInstance().handleAuthorizationSuccess(response);
                    } catch (JSONException e) {
                        AppIdAuthorizationManager.getInstance().handleAuthorizationFailure(null, e, null);
                        throw new RuntimeException("Failed to save token from response", e);
                    }
                }

                @Override
                public void onFailure(Response response, Throwable t, JSONObject extendedInfo) {
                    AppIdAuthorizationManager.getInstance().handleAuthorizationFailure(response, t, extendedInfo);
                }
            });
        }catch (MalformedURLException e) {
            AppIdAuthorizationManager.getInstance().handleAuthorizationFailure(null, e, null);
            throw new RuntimeException("AuthorizationRequest - failed to instantiate class", e);
        }
        catch (Exception e) {
            AppIdAuthorizationManager.getInstance().handleAuthorizationFailure(null, e, null);
            throw new RuntimeException("Failed to create token request headers", e);
        }
    }

    private String getTokenUrl() {
        return AppIdAuthorizationManager.getInstance().getServerHost() +  tokenPath + AppId.getInstance().getTenantId() + "/token";
    }

    private String createTokenRequestHeaders() throws Exception {
        String tokenAuthHeader = null;
        String userName = preferences.clientId.get();
        AppIdRegistrationManager appIdRM = AppIdAuthorizationManager.getInstance().getAppIdRegistrationManager();
        PrivateKey privateKey = appIdRM.getAppIdKeyStore().getStoredKeyPair().getPrivate();
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(userName.getBytes());
        String password = Base64.encodeToString(signature.sign(), Base64.NO_WRAP);
        tokenAuthHeader = "Basic " + Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);
        return tokenAuthHeader;
    }

    /**
     * Extract token from response and save it locally
     *
     * @param response response that contain the token
     */
    private void saveTokenFromResponse(Response response) throws JSONException {
            JSONObject responseJSON = ((ResponseImpl) response).getResponseJSON();
            String accessToken = responseJSON.getString("access_token");
            String idToken = responseJSON.getString("id_token");
            //save the tokens
            preferences.accessToken.set(accessToken);
            preferences.idToken.set(idToken);
            //save the user identity separately
            String[] idTokenData = idToken.split("\\.");
            byte[] decodedIdTokenData = Base64.decode(idTokenData[1], Base64.URL_SAFE);
            String decodedIdTokenString = new String(decodedIdTokenData);
            JSONObject idTokenJSON = new JSONObject(decodedIdTokenString);
            if (idTokenJSON.has("imf.user")) {
                preferences.userIdentity.set(idTokenJSON.getJSONObject("imf.user"));
            }
    }
}