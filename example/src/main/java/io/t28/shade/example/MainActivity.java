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

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.t28.shade.example.databinding.ActivityMainBinding;
import io.t28.shade.example.model.Setting;
import io.t28.shade.example.preferences.User;
import io.t28.shade.example.preferences.UserPreferences;
import io.t28.shade.example.widget.SettingAdapter;

public class MainActivity extends AppCompatActivity {
    private SettingAdapter adapter;
    private ActivityMainBinding binding;
    private UserPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SettingAdapter();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.mainActivity.setLayoutManager(new LinearLayoutManager(this));
        binding.mainActivity.setAdapter(adapter);
        preferences = User.getPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Set<String> tags = new HashSet<>();
        tags.add("Java");
        tags.add("Android");
        preferences.edit()
                .putId(1024)
                .putName("t28")
                .putType(User.Type.ADMIN)
                .putTags(tags)
                .putLocked(true)
                .putUpdated(new Date())
                .apply();
        final User user = preferences.get();
        final List<Setting> settings = Arrays.asList(
                new Setting(R.string.item_user_id, String.valueOf(user.id())),
                new Setting(R.string.item_user_name, user.name()),
                new Setting(R.string.item_user_type, user.type().name()),
                new Setting(R.string.item_user_tags, TextUtils.join(", ", user.tags())),
                new Setting(R.string.item_user_locked, String.valueOf(user.isLocked())),
                new Setting(R.string.item_user_updated, user.updated().toString())
        );
        adapter.setSettings(settings);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }
}
