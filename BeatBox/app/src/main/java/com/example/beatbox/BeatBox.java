package com.example.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;

public class BeatBox {
    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;
    private AssetManager mAssets;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        // 首先create asset manager 找到asset音频文件存放的路径
        mAssets = context.getAssets();

        // 创建soundpool 准备使用soundpool播放文件
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);

        // 加载音频文件
        loadSounds();
    }

    public void play(Sound sound){
        Integer soundId = sound.getmSoundId();
        if (soundId == null){
            return;
        }

        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release(){
        mSoundPool.release();
    }

    private void loadSounds() {
        String[] soundNames;
        try {
            // list出来所有音频文件
            soundNames = mAssets.list(SOUNDS_FOLDER);
            Log.d(TAG, "FOUND " + soundNames.length + " sounds");
        } catch (IOException e) {
            Log.e(TAG, "Could not list assets ", e);
            return;
        }

        for (String fileName : soundNames) {
            try {
                // 找到音频文件的目录
                String assetPath = SOUNDS_FOLDER + "/" + fileName;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            } catch (IOException ioe) {
                Log.e(TAG, "Could not load sound" + fileName, ioe);
            }
        }
    }

    private void load(Sound sound) throws IOException{
        AssetFileDescriptor afd = mAssets.openFd(sound.getmAssetPath());
        // 把文件加载到soundpool待播 为了方便管理 重播 卸载音频文件 load方法会返回一个int型ID 这实际就是存储在mSoundId中的ID
        // 调用open(String)有可能抛出IOException load方法也是如此
        int soundId = mSoundPool.load(afd, 1);
        sound.setmSoundId(soundId);
    }

    public List<Sound> getmSounds() {
        return mSounds;
    }
}
