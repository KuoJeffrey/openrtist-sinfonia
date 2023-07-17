package edu.cmu.cs.sinfonia;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SinfoniaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SinfoniaFragment())
                    .commit();
        }
    }
}
