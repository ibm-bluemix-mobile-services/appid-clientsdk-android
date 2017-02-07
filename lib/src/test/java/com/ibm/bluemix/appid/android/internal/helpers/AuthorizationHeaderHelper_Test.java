package com.ibm.bluemix.appid.android.internal.helpers;

import com.ibm.bluemix.appid.android.testing.mocks.HttpURLConnection_Mock;
import com.ibm.mobilefirstplatform.appid_clientsdk_android.BuildConfig;
import com.ibm.mobilefirstplatform.clientsdk.android.security.api.AuthorizationManager;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.assertj.core.api.Java6Assertions.*;

@RunWith (RobolectricTestRunner.class)
@FixMethodOrder (MethodSorters.NAME_ASCENDING)
@Config (constants = BuildConfig.class)
public class AuthorizationHeaderHelper_Test {
	@Test
	public void isAuthorizationRequired () throws Exception {
		Map<String, List<String>> headers;

		headers = new HashMap<>();
		headers.put("Dummy", Arrays.asList("Dummy"));

		// Non-401 status
		assertThat(AuthorizationHeaderHelper.isAuthorizationRequired(200, null)).isEqualTo(false);

		// 401 status, but Www-Authenticate header is null
		assertThat(AuthorizationHeaderHelper.isAuthorizationRequired(401, null)).isEqualTo(false);

		// 401 status, Www-Authenticate header exist, but invalid value
		assertThat(AuthorizationHeaderHelper.isAuthorizationRequired(401, "Dummy")).isEqualTo(false);

		// 401 status, Www-Authenticate header exists, Bearer exists, but not appid scope
		assertThat(AuthorizationHeaderHelper.isAuthorizationRequired(401, "Bearer Dummy")).isEqualTo(false);

		// Www-Authenticate required
		headers.remove(AuthorizationManager.WWW_AUTHENTICATE_HEADER_NAME);
		assertThat(AuthorizationHeaderHelper.isAuthorizationRequired(401, "Bearer scope=\"appid_default\"")).isEqualTo(true);

		// Check with httpUrlConnection
		HttpURLConnection_Mock httpURLConnection = new HttpURLConnection_Mock();
		try {
			assertThat(AuthorizationHeaderHelper.isAuthorizationRequired(httpURLConnection)).isEqualTo(true);
		} catch (IOException e){
			assertThat(e).isNull();
		}
	}
}