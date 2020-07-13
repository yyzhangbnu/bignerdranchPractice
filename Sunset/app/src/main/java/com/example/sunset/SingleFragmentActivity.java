package com.example.sunset;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    // abstract class must have definition at children class
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        // get fragment manager
        FragmentManager fm = getSupportFragmentManager();

        // use the fragment manager to find the fragment
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        // if fragment is null, try to set one and transaction and commit
        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}