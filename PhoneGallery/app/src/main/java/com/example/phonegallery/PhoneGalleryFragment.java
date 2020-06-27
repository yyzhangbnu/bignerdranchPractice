package com.example.phonegallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
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

    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

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
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getmUrl());

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
        private String mQuery;
        public FetchItemsTask(String query){
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            /*
            try{
                String result = new FilckerFetchr().getUrlString("https://www.bignerdranch.com");
                Log.i(TAG, "Fetched contents of URL: " + result);
            } catch (IOException ioe){
                Log.e(TAG, "Failed to Fetch URL: " + ioe);
            }*/
           // return new FilckerFetchr().fetchItems();

          //  String query = "robot";
            if(mQuery == null){
                return new FilckerFetchr().fetchRecentPhotos();
            }else {
                return new FilckerFetchr().searchPhotos(mQuery);
            }
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
        setHasOptionsMenu(true);

        // 调用FetchItemsTask新实例的execute方法就可以启动AsyncTask，进而触发后台线程调用doinBackground方法
        // new FetchItemsTask().execute();
        updateItems();
        Handler reponseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(reponseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                target.bindDrawable(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroy");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        // menu class是Androids.appcomp.widge.SearchView, can't use the below to get searchView object.
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit: " + query);
                updateItems();
                QueryPreferences.setStoredQuery(getActivity(), query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems(){
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
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
