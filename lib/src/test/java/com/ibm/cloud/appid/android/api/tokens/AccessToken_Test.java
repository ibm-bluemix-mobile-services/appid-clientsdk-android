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

package com.ibm.cloud.appid.android.api.tokens;
import com.ibm.cloud.appid.android.internal.tokens.AccessTokenImpl;
import com.ibm.cloud.appid.android.testing.helpers.Consts;
import com.ibm.mobilefirstplatform.appid_clientsdk_android.BuildConfig;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.assertj.core.api.Java6Assertions.*;

@RunWith (RobolectricTestRunner.class)
@FixMethodOrder (MethodSorters.NAME_ASCENDING)
@Config (constants = BuildConfig.class)
public class AccessToken_Test {

	@Test ()
	public void testWithValidAccessToken ()  {
		AccessToken accessToken = new AccessTokenImpl(Consts.ACCESS_TOKEN);
		assertThat(accessToken).isNotNull();
		assertThat(accessToken.getScope()).isEqualTo("appid_default appid_readprofile appid_readuserattr appid_writeuserattr");
	}
}