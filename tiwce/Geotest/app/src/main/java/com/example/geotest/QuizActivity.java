package com.example.geotest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static String TAG = "QuizActivity";
    private static String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question1, false, false),
            new Question(R.string.question2, true, false),
            new Question(R.string.question3, true,false),
            new Question(R.string.question4, false, false),
            new Question(R.string.question5, true, false)
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;

                if(mQuestionBank[mCurrentIndex].isAnswered() == false){
                    mTrueButton.setClickable(false);
                    mFalseButton.setClickable(false);
                }
                mIsCheater = false;
                updateQuestion();
            }
        });

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex > 0) {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                }
                else {
                    mCurrentIndex = mQuestionBank.length - 1;
                }

                if(mQuestionBank[mCurrentIndex].isAnswered() == false){
                    mTrueButton.setClickable(true);
                    mFalseButton.setClickable(true);
                }
                updateQuestion();
            }
        });

        // 一个activity启动另一个activity最简单的方式是使用startActivity方法，activity调用startactivity(intent)方法时。
        // 调用请求实际发给了操作系统的ActivityManager
        // intent是component用来与操作系统通信的的一种媒介工具。目前我们见过的component就是activity
        // 实际上还有其他的component，service，broadcast receiver以及content provider。
        // public Intent(Context packageContext, Class cls) 这里的class告诉ActivityManager应该启动那个activity，Context告诉在哪里可以找到它

        // 启动了cheatActivity，但是要把当前问题的答案传递给子activity
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
//                startActivity(intent);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivity(intent);
                // 是要在startActivity之后就调用startActivityForResult 把request code发出去，设置返回结果要在子activity里
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    // 当在子activity里点击back按钮 返回主activity的时候，activitymanager调用父activity的以下方法，该方法的参数
    // 来自quizActivity的原始请求代码以及传入setResult方法的结果代码和intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    // 需要从子activity获取返回信息时，可调用一下actvity方法 startActivityForResult
    // public void startActivityForResult(Intent intent, int requestCode)
    // 第一个参数跟之前的intent是一样的，第二个参数是请求代码，请求代码是先发送给子activity，然后在返回给父activity的整数值，
    // 在一个actvity启动多个不同类型的子activity的时候，且要判断消息回馈方时，就会用到该请求代码

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if(mIsCheater){
            messageResId = R.string.judgement_toast;
        } else {
            if (userPressedTrue == answerIsTrue){
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        mQuestionBank[mCurrentIndex].setAnswered(true);

        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show();
       /* mTrueButton.setClickable(false);
        mTrueButton.setAlpha(.5f);
        mFalseButton.setClickable(false);
        mFalseButton.setAlpha(.5f);*/
    }
}
/*
* 暂存的activity记录到底可以保留多久？前面说过，用户按下后退键后，系统会彻底销毁当前的actvity，此时，暂存的activity记录同时被清楚
* 此外，系统重启的话，暂存的activity也会被清楚。
* */

/*
* 用户答完所有题时，显示一个toast消息，给出百分比形式的评分。
* 这个问题，有一个用户给出来的答案，不应该作为question的属性，用户是否答过也不应该作为question的属性
* 一个解法就是用一个数组存储已经答过的题目，然后update题目的时候检查这道题是否答过，数组存的index就是question的currentIndex
* 计算用户得分的百分比这个问题应该是增加两个系统参数，回答对的和回答错的，放在checkAnswer方法中，答对了 correct就加一，打错了incorrect就加1
* 然后每次都计算两个数值和是否为bank的长度来判断是否已经答完 还可以计算百分比
* */

/*
* 创建新的activity 就是从一个activity中启动另一个activity，所谓启动，就是请求android系统创建新的activity实例并调用其oncreate()方法。
* 并且在父activity与子activity之间传递数据
* */