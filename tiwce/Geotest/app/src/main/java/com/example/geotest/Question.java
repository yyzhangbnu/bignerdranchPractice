package com.example.geotest;

public class Question {
    // question content
    private int mTextResId;
    // question answer
    private boolean mAnswerTrue;
    // if the question is answered
    private boolean mIsAnswered;

    // automatically generate Get() and Set()
    // 1. in android studio-> preferences->editor->Java->code generate->input 'm' in the field->Apply.
    // 2. right click the function's constructor -> Generate -> Get and Set
    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public boolean isAnswered() {
        return mIsAnswered;
    }

    public void setAnswered(boolean answered) {
        mIsAnswered = answered;
    }

    public Question(int textResId, boolean answerTrue, boolean isAnswered) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mIsAnswered = isAnswered;
    }
}
