package com.example.wtz.learntosing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonBaseAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected List<T> mDataList;
    protected Context mContext;

    protected CommonBaseAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setDataList(List<T> dataList) {
        mDataList = dataList;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public T getItem(int position) {
        int size = mDataList.size();
        if (position >= size) {
            position = size - 1;
        }
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
