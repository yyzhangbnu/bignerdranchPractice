package com.example.quizproject;

public class Question {
    private int mTexResId;
    private boolean mAnswerTrue;

    public Question(int textResId, boolean answerTrue){
        mTexResId = textResId;
        mAnswerTrue = answerTrue;
    }

    public boolean ismAnswerTrue() {
        return mAnswerTrue;
    }

    public void setmAnswerTrue(boolean mAnswerTrue) {
        this.mAnswerTrue = mAnswerTrue;
    }

    public int getmTexResId() {
        return mTexResId;
    }

    public void setmTexResId(int mTexResId) {
        this.mTexResId = mTexResId;
    }
}
