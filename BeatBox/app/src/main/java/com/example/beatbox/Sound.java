package com.example.beatbox;

public class Sound {
    private String mAssetPath;
    private String mName;

    public Sound(String assetPath){
        mAssetPath = assetPath;
        String[] components = assetPath.split("/");
        String filenName = components[components.length - 1];
        mName = filenName.replace(".wav", "");
    }

    public String getmAssetPath() {
        return mAssetPath;
    }

    public String getName(){
        return mName;
    }
}
