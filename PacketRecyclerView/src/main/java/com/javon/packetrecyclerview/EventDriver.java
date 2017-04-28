package com.javon.packetrecyclerview;

import android.view.View;

import java.util.List;

/**
 * 项目名称:com.javon.packetrecyclerview
 * Created by Administrator on 2016/10/15.
 */

public interface EventDriver<M> {

    void setData(List<M> obj, boolean mIsError);
    void addData(int mLength, boolean mIsError);
    void clear();

    void stopLoadMore();
    void pauseLoadMore();
    void resumeLoadMore();

    void setMore(View mView, BaseRecyclerAdapter.OnEventDriver mListener);
    void setNoMore(View mView);
    void setErrorMore(View mView, BaseRecyclerAdapter.OnEventDriver mListener);


}
