package com.javon.packetrecyclerview.demo;

import android.content.Context;
import android.view.View;

import com.javon.packetrecyclerview.BaseViewHolder;

/**
 * project:PacketRecyclerView
 * package:com.javon.packetrecyclerview.demo
 * Created by javonLiu on 2017/4/27.
 * e-mail : liujunjie00@yahoo.com
 */

public class RecyclerHolder extends BaseViewHolder<String> {

    public RecyclerHolder(Context mContext, View mItemView) {
        super(mContext, mItemView);
    }

    @Override
    public void setData(String mData) {
        setText(R.id.text, mData);
    }
}
