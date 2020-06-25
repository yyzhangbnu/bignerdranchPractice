package com.example.beatbox;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/*
* 使用BaseObserable累需要三个步骤
* 1 在视图模型里继承BaseObservable类
* 2 使用@Bindable注解视图模型里可绑定的属性
* 3 每次可绑定的属性值改变时，就调用notify()方法或者notifyPropertyChanged(int)方法
* */
public class SoundViewModel extends BaseObservable {
    // 两个属性 一个sound对象
    private Sound mSound;
    // 一个是播放声音文件的beatbox对象
    private BeatBox mBeatBox;

    public SoundViewModel(BeatBox beatBox) {
        mBeatBox = beatBox;
    }

    public Sound getmSound() {
        return mSound;
    }

    @Bindable
    public String getTitle() {
        return mSound.getName();
    }

    public void setmSound(Sound sound) {
        mSound = sound;
        notifyChange();
    }

    public void onButtonClicked() {
        mBeatBox.play(mSound);
    }
}
