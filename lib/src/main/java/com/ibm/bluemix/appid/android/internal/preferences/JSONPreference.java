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

package com.ibm.bluemix.appid.android.internal.preferences;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONPreference extends StringPreference {

	JSONPreference(String name, SharedPreferences sharedPreferences) {
		super(name, sharedPreferences);
	}

	public void set(JSONObject json) {
		super.set(json.toString());
	}

	public JSONObject getAsJSON() throws JSONException {
		String stringValue = super.get();
		return (stringValue == null) ? null : new JSONObject(stringValue);
	}
}
