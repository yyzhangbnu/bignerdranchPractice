package com.example.phonegallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

public class PhoneGalleryFragment extends Fragment {
    private RecyclerView mPhotoRecylerView;

    private static final String TAG = "PhotoGalleryFragment";

    // 在Fragment中添加内部类 FetchItemsTask 然后覆盖AsyncTask doInBackground方法 在里面从目标网站获取数据并日志记录
    private class FetchItemsTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                String result = new FilckerFetchr().getUrlString("https://www.bignerdranch.com");
                Log.i(TAG, "Fetched contents of URL: " + result);
            } catch (IOException ioe){
                Log.e(TAG, "Failed to Fetch URL: " + ioe);
            }

            return null;
        }
    }
    public static PhoneGalleryFragment newInstance() {
        return new PhoneGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // 调用FetchItemsTask新实例的execute方法就可以启动AsyncTask，进而触发后台线程调用doinBackground方法
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_phone_gallery, container, false);
        mPhotoRecylerView = (RecyclerView) v.findViewById(R.id.phone_recycler_view);
        mPhotoRecylerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return v;
    }
}

// 网络链接需要时间 web服务器可能需要1-2秒的时间来响应 文件下载则耗时更久 考虑到这个因素 Android禁止任何主线程网络链接行为
// 线程 主线程
// 线程是个单一执行序列 单个线程中的代码会逐步执行
// 所有Android应用的运行都是从主线程开始的 然而 主线程不是线程那样的预定执行序列 相反
// 它处于一个无线循环的运行状态 等着用户和系统的触发
// 一旦有事件触发 主线程便执行代码作出响应
