package com.example.phonegallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private Boolean mHasQuit = false;

    //标识下载请求消息
    private static final int MESSAGE_DOWNLAOD = 0;
    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener){
        mThumbnailDownloadListener = listener;
    }

    // 构造函数
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_DOWNLAOD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    // 退出handlerThread线程
    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    // 执行线程需要执行的操作
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
        }

        mRequestHandler.obtainMessage(MESSAGE_DOWNLAOD, target).sendToTarget();
    }

    public void clearQueue(){
        mResponseHandler.removeMessages(MESSAGE_DOWNLAOD);
        mRequestMap.clear();
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }

            byte[] bitmapBytes = new FilckerFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRequestMap.get(target) != url || mHasQuit){
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
}

/*
 * 剖析message
 * 消息是message类的一个实例，它有好几个实例变量 其中三个需要定义
 * what：用户定义的int型消息代码，用来描述消息
 * obj：用户指定，随消息发送的对象
 * target：处理消息的handler
 * Message的目标(target)是一个处理消息的handler类实例
 * Handler可看作message handler的简称
 * 创建message的时候 它会自动个与一个handler相关联 message待处理时，handler对象负责触发消息处理事件
 * 要处理消息以及消息指定的任务，首先需要一个handler实例
 * handler不仅仅是处理message的目标，也是创建和发布message的接口
 *
 * */
