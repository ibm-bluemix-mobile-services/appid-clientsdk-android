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

import com.ibm.cloud.appid.android.api.AuthorizationListener;
import com.ibm.cloud.appid.android.internal.OAuthManager;

public class AuthorizationFlowContext {
	private final OAuthManager oAuthManager;
	private final AuthorizationListener authorizationListener;

	public AuthorizationFlowContext(OAuthManager oAuthManager, AuthorizationListener authorizationListener){
		this.oAuthManager = oAuthManager;
		this.authorizationListener = authorizationListener;
	}

	public OAuthManager getOAuthManager () {
		return oAuthManager;
	}

	public AuthorizationListener getAuthorizationListener () {
		return authorizationListener;
	}
}
