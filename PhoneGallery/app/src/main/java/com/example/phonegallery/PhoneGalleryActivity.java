package com.example.phonegallery;

import android.app.Activity;
import android.util.Log;

import androidx.fragment.app.Fragment;

public class PhoneGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        Log.d("PhoneGalleryActivity", "create");
        return PhoneGalleryFragment.newInstance();
    }
}
