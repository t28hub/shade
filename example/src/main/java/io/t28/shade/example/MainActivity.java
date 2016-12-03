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

import io.t28.shade.example.model.Example;
import io.t28.shade.example.model.ExamplePreferences;

public class MainActivity extends AppCompatActivity {
    private ExamplePreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = new ExamplePreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Example oldValue = preferences.get();
        Log.d(MainActivity.class.getSimpleName(), oldValue.toString());
        preferences.edit()
                .putIntValue(100)
                .putLongValue(200)
                .apply();
        final Example newValue = preferences.get();
        Log.d(MainActivity.class.getSimpleName(), newValue.toString());
    }
}
