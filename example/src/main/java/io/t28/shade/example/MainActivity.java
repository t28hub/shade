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
