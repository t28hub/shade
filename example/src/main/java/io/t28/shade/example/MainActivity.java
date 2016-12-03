/*
 * Copyright (c) 2016 Tatsuya Maki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.t28.shade.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.t28.shade.example.preferences.User;
import io.t28.shade.example.preferences.UserPreferences;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private UserPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = User.getPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final User oldUser = preferences.get();
        Log.d(TAG, oldUser.toString());
        preferences.edit()
                .putName("new name")
                .putType(User.Type.ADMIN)
                .apply();
        final User newUser = preferences.get();
        Log.d(TAG, newUser.toString());
    }
}
