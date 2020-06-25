package com.example.beatbox;

import android.content.Intent;

public class Sound {
    private String mAssetPath;
    private String mName;
    // 这里设置的是Integr 而不是int 这样在Sound的mSoundId没有值的时候 可以设置为null
    private Integer mSoundId;

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

    public Integer getmSoundId(){ return mSoundId;}

    public void setmSoundId(Integer soundId){
        mSoundId = soundId;
    }
}
