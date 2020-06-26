package com.example.phonegallery;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhoneGalleryFragment extends Fragment {
    private RecyclerView mPhotoRecylerView;

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();

    // 首先定义 viewholder
    private class PhotoHolder extends RecyclerView.ViewHolder {
       // private TextView mTitleTextView;

        // 其中元素是imageview
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
           // mTitleTextView = (TextView) itemView;
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

        /*
        public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.toString());
        }
        */

        public void bindDrawable(Drawable drawable){
            // 把adapter的数据传进来 bind到holder上
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        // adpter存数据 有一个私有变量就是用来存数据
        private List<GalleryItem> mGalleryItems;

        // adapter构造函数
        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        // 传入的参数 第一个是viewholder 把数据bind上去 通过position找到数据
        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            // holder.bindGalleryItem(galleryItem);

            Drawable placeHolder = getResources().getDrawable(R.drawable.ic_launcher_foreground);
            holder.bindDrawable(placeHolder);
        }

        // 在adapter里创建viewholder 在显示图表里 view是新创建的imangerview
        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            /*TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);*/

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    // 在Fragment中添加内部类 FetchItemsTask 然后覆盖AsyncTask doInBackground方法 在里面从目标网站获取数据并日志记录
    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            /*
            try{
                String result = new FilckerFetchr().getUrlString("https://www.bignerdranch.com");
                Log.i(TAG, "Fetched contents of URL: " + result);
            } catch (IOException ioe){
                Log.e(TAG, "Failed to Fetch URL: " + ioe);
            }*/
            return new FilckerFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
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

        setupAdapter();
        return v;
    }

    // setupAdapter()会自动配置RecyclerView的adaptor 应该在onCreateView()方法中调用这个方法，这样每次因设备旋转重新生成RecyclerView时，
    // 可重新为其分配置对应的adapter
    private void setupAdapter(){
        if(isAdded()){
            mPhotoRecylerView.setAdapter(new PhotoAdapter(mItems));
        }
    }
}

// 网络链接需要时间 web服务器可能需要1-2秒的时间来响应 文件下载则耗时更久 考虑到这个因素 Android禁止任何主线程网络链接行为
// 线程 主线程
// 线程是个单一执行序列 单个线程中的代码会逐步执行
// 所有Android应用的运行都是从主线程开始的 然而 主线程不是线程那样的预定执行序列 相反
// 它处于一个无线循环的运行状态 等着用户和系统的触发
// 一旦有事件触发 主线程便执行代码作出响应
