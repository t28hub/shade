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

        preferences = new ExamplePreferences(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Example example = preferences.load();

        preferences.edit(example)
                .intValue(100)
                .longValue(200)
                .apply();
        Log.d(MainActivity.class.getSimpleName(), example.toString());
    }
}
