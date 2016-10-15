package io.t28.shade.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.t28.shade.example.model.Example;
import io.t28.shade.example.model.ExampleService;

public class MainActivity extends AppCompatActivity {
    private ExampleService exampleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exampleService = new ExampleService(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Example example = exampleService.load();

        exampleService.edit(example)
                .intValue(100)
                .longValue(200)
                .apply();
        Log.d(MainActivity.class.getSimpleName(), example.toString());
    }
}
