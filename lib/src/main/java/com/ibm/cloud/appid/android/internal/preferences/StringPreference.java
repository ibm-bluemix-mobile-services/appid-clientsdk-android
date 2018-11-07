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

package com.ibm.cloud.appid.android.internal.preferences;

import android.content.SharedPreferences;

public class StringPreference {

	private final String name;
	private final SharedPreferences sharedPreferences;
	private final SharedPreferences.Editor editor;

	StringPreference(String name, SharedPreferences sharedPreferences) {
		this.name = name;
		this.sharedPreferences = sharedPreferences;
		this.editor = sharedPreferences.edit();
	}

	public synchronized String get() {
		return sharedPreferences.getString(name, null);
	}

	public synchronized void set(String value) {
		editor.putString(name, value);
		editor.commit();
	}

	public void clear() {
		set(null);
	}
}

