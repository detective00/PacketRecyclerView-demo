package com.javon.packetrecyclerview.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.javon.packetrecyclerview.BaseRecyclerAdapter;
import com.javon.packetrecyclerview.BaseViewHolder;

/**
 * project:PacketRecyclerView
 * package:com.javon.packetrecyclerview.demo
 * Created by javonLiu on 2017/4/27.
 * e-mail : liujunjie00@yahoo.com
 */

public class RecyclerAdapter extends BaseRecyclerAdapter<String> {
    public RecyclerAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler_item, parent, false);
        return new RecyclerHolder(parent.getContext(), root);
    }
}
