package com.javon.packetrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 项目名称:com.javon.packetrecyclerview
 * Created by Administrator on 2016/10/17.
 */

public class PacketRecyclerView extends FrameLayout {

    public static final String TAG = PacketRecyclerView.class.getSimpleName();
    public static final boolean DEBUG = false;
    private RecyclerView mRecycler;
    private ViewGroup mProgressView;
    private ViewGroup mEmptyView;
    private ViewGroup mErrorView;
    private int mProgressId;
    private int mEmptyId;
    private int mErrorId;

    private boolean mClipToPadding;
    private int mPadding;
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mScrollbarStyle;

    private RecyclerView.OnScrollListener mInternalOnScrollEvent;
    private RecyclerView.OnScrollListener mExternalOnScrollEvent;

//    private SwipeRefreshLayout mPtrLayout;
    private SuperSwipeRefreshLayout mPtrLayout;
    private TextView tvRefresh;
    private ImageView ivRefresh;
    private ProgressBar pbRefresh;
    private android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener mRefreshEvent;
    private BaseRecyclerAdapter.OnEventDriver mEventDriver;

    public SuperSwipeRefreshLayout getSwipeToRefresh() {
        return mPtrLayout;
    }
    /*public SwipeRefreshLayout getSwipeToRefresh() {
        return mPtrLayout;
    }*/

    public RecyclerView getRecyclerView() {
        return mRecycler;
    }

    public PacketRecyclerView(Context context) {
        super(context);
        initView();
    }

    public PacketRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public PacketRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.packetrecyclerview);
        try {
            mClipToPadding = a.getBoolean(R.styleable.packetrecyclerview_recyclerClipToPadding, false);
            mPadding = (int) a.getDimension(R.styleable.packetrecyclerview_recyclerPadding, -1.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.packetrecyclerview_recyclerPaddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.packetrecyclerview_recyclerPaddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.packetrecyclerview_recyclerPaddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.packetrecyclerview_recyclerPaddingRight, 0.0f);
            mScrollbarStyle = a.getInteger(R.styleable.packetrecyclerview_scrollbarStyle, -1);
            mEmptyId = a.getResourceId(R.styleable.packetrecyclerview_layout_empty, 0);
            mProgressId = a.getResourceId(R.styleable.packetrecyclerview_layout_progress, 0);
            mErrorId = a.getResourceId(R.styleable.packetrecyclerview_layout_error, 0);
        } finally {
            a.recycle();
        }
    }

    private void initView(){
        if (isInEditMode()) {
            return;
        }
        //生成主View
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_recyclerview, this);
        mPtrLayout = (SuperSwipeRefreshLayout) mView.findViewById(R.id.ptr_layout);
        mPtrLayout.setHeaderViewBackgroundColor(0xfff8f8f8);
        mPtrLayout.setHeaderView(createRefreshView());
        mPtrLayout.setEnabled(false);
        mProgressView = (ViewGroup) mView.findViewById(R.id.progress);
        if (mProgressId != 0) LayoutInflater.from(getContext()).inflate(mProgressId, mProgressView);
        mEmptyView = (ViewGroup) mView.findViewById(R.id.empty);
        if (mEmptyId != 0) LayoutInflater.from(getContext()).inflate(mEmptyId, mEmptyView);
        mErrorView = (ViewGroup) mView.findViewById(R.id.error);
        if (mErrorId != 0) {
            View mChildView = LayoutInflater.from(getContext()).inflate(mErrorId, mErrorView);
            mChildView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventDriverRetryError();
                }
            });
        }
        initRecyclerView(mView);
    }

    private void eventDriverRetryError(){
        if(mProgressId != 0 && !mProgressView.isShown()){
            if(mErrorView != null) mErrorView.setVisibility(GONE);
            mProgressView.setVisibility(VISIBLE);
        }
        mEventDriver.onRetryError();
    }


    /**
     * 自定义刷新头部
     * @return
     */
    private View createRefreshView() {
        View headerView = LayoutInflater.from(getContext())
                .inflate(R.layout.view_refresh, null);
        pbRefresh = (ProgressBar) headerView.findViewById(R.id.pb_view);
        tvRefresh = (TextView) headerView.findViewById(R.id.text_view);
        tvRefresh.setText("下拉刷新");
        ivRefresh = (ImageView) headerView.findViewById(R.id.image_view);
        ivRefresh.setVisibility(View.VISIBLE);
        ivRefresh.setImageResource(R.mipmap.down_arrow);
        pbRefresh.setVisibility(View.GONE);
        return headerView;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mPtrLayout.dispatchTouchEvent(ev);
    }

    public void setRecyclerPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
        mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
    }

    public void setEmptyView(View mEmptyView) {
        this.mEmptyView.removeAllViews();
        this.mEmptyView.addView(mEmptyView);
    }

    public void setProgressView(View mProgressView) {
        this.mProgressView.removeAllViews();
        this.mProgressView.addView(mProgressView);
    }

    public void setErrorView(View mErrorView) {
        this.mErrorView.removeAllViews();
        this.mErrorView.addView(mErrorView);
        mErrorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                eventDriverRetryError();
            }
        });
    }

    public void setEmptyView(int mEmptyView) {
        this.mEmptyView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(mEmptyView, this.mEmptyView);
    }

    public void setProgressView(int mProgressView) {
        this.mProgressView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(mProgressView, this.mProgressView);
    }

    public void setErrorView(int mErrorView) {
        this.mErrorView.removeAllViews();
        View mEView= LayoutInflater.from(getContext()).inflate(mErrorView, this.mErrorView);
        mEView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                eventDriverRetryError();
            }
        });
    }

    public void scrollToPosition(int position) {
        getRecyclerView().scrollToPosition(position);
    }


    protected void initRecyclerView(View view) {
        mRecycler = (RecyclerView) view.findViewById(android.R.id.list);
        if (mRecycler != null) {
            mRecycler.setHasFixedSize(true);
            mRecycler.setClipToPadding(mClipToPadding);
            mInternalOnScrollEvent = new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (mExternalOnScrollEvent != null)
                        mExternalOnScrollEvent.onScrolled(recyclerView, dx, dy);

                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (mExternalOnScrollEvent != null)
                        mExternalOnScrollEvent.onScrollStateChanged(recyclerView, newState);

                }
            };
            mRecycler.addOnScrollListener(mInternalOnScrollEvent);
            if (mPadding != -1.0f) {
                mRecycler.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
            }
            if (mScrollbarStyle != -1) {
                mRecycler.setScrollBarStyle(mScrollbarStyle);
            }
        }
        showRecycler();
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecycler.setLayoutManager(manager);
    }


    private static class EasyDataObserver extends RecyclerView.AdapterDataObserver {
        private PacketRecyclerView mRecyclerView;
        private boolean mIsInitialized = false;
        private boolean mHasProgress = false;

        public EasyDataObserver(PacketRecyclerView recyclerView, boolean hasProgress) {
            this.mRecyclerView = recyclerView;
            this.mHasProgress = hasProgress;
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            update();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            update();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            update();
        }

        //自动更改Container的样式
        private void update() {
            log("update");
            if (mRecyclerView.getAdapter() instanceof BaseRecyclerAdapter) {
                    if (((BaseRecyclerAdapter) mRecyclerView.getAdapter()).getCount() == 0) {
                        log("no data:" + ((mHasProgress && !mIsInitialized) ? "show progress" : "show empty"));
                        if (mHasProgress && !mIsInitialized) {
                            mRecyclerView.showProgress();
                        } else {
                            if(((BaseRecyclerAdapter) mRecyclerView.getAdapter()).ismIsError()){
                                mRecyclerView.showError();
                            }else {
                                mRecyclerView.showEmpty();
                            }
                        }
                    } else {
                        log("has data");
                        mRecyclerView.showRecycler();
                    }
            } else {
                if (mRecyclerView.getAdapter().getItemCount() == 0) {
                    log("no data:" + ((mHasProgress && !mIsInitialized) ? "show progress" : "show empty"));
                    if (mHasProgress && !mIsInitialized){
                        mRecyclerView.showProgress();
                    } else{
                        mRecyclerView.showEmpty();
                    }
                } else {
                    log("has data");
                    mRecyclerView.showRecycler();
                }
            }
            mIsInitialized = true;
        }
    }



    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EasyDataObserver(this, false));
        adapter.notifyDataSetChanged();
    }


    public void setAdapterWithProgress(RecyclerView.Adapter adapter) {
        mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EasyDataObserver(this, true));
        adapter.notifyDataSetChanged();
    }


    public void clear() {
        mRecycler.setAdapter(null);
    }


    private void hideAll() {
        mEmptyView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        mErrorView.setVisibility(GONE);
        mPtrLayout.setRefreshing(false);
        mRecycler.setVisibility(GONE);
    }


    public void showError() {
        log("showError");
        if (mErrorView.getChildCount() > 0) {
            hideAll();
            mErrorView.setVisibility(View.VISIBLE);
        } else {
            showRecycler();
        }
    }

    public void showEmpty() {
        log("showEmpty");
        if (mEmptyView.getChildCount() > 0) {
            hideAll();
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            showRecycler();
        }
    }


    public void showProgress() {
        log("showProgress");
        if (mProgressView.getChildCount() > 0) {
            hideAll();
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            showRecycler();
        }
    }


    public void showRecycler() {
        log("showRecycler");
        hideAll();
        mRecycler.setVisibility(View.VISIBLE);
    }



    public void setRefreshListener(final android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener listener) {
        mPtrLayout.setEnabled(true);
//        mPtrLayout.setOnRefreshListener(listener);
        mPtrLayout.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                tvRefresh.setText("正在刷新...");
                ivRefresh.setVisibility(View.GONE);
                pbRefresh.setVisibility(View.VISIBLE);
                listener.onRefresh();
            }

            @Override
            public void onPullDistance(int distance) {
                if (distance == 0)
                    pbRefresh.setVisibility(View.GONE);
            }

            @Override
            public void onPullEnable(boolean enable) {
                tvRefresh.setText(enable ? "松开刷新" : "下拉刷新");
                ivRefresh.setVisibility(View.VISIBLE);
                ivRefresh.setRotation(enable ? 180 : 0);
            }
        });
        this.mRefreshEvent = listener;
    }

    public void setRefreshing(final boolean isRefreshing) {
        mPtrLayout.post(new Runnable() {
            @Override
            public void run() {
                mPtrLayout.setRefreshing(isRefreshing);
            }
        });
    }

    public void setRefreshing(final boolean isRefreshing, final boolean isCallbackListener) {
        mPtrLayout.post(new Runnable() {
            @Override
            public void run() {
                mPtrLayout.setRefreshing(isRefreshing);
                if (isRefreshing && isCallbackListener && mRefreshEvent != null) {
                    mRefreshEvent.onRefresh();
                }
            }
        });
    }

    public void setEventListener(BaseRecyclerAdapter.OnEventDriver mEventDriver) {
        this.mEventDriver = mEventDriver;
    }


    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mExternalOnScrollEvent = listener;
    }


    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.addOnItemTouchListener(listener);
    }

    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.removeOnItemTouchListener(listener);
    }


    public RecyclerView.Adapter getAdapter() {
        return mRecycler.getAdapter();
    }


    public void setOnTouchListener(View.OnTouchListener listener) {
        mRecycler.setOnTouchListener(listener);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecycler.setItemAnimator(animator);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecycler.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.removeItemDecoration(itemDecoration);
    }


    public View getErrorView() {
        if (mErrorView.getChildCount() > 0) return mErrorView.getChildAt(0);
        return null;
    }


    public View getProgressView() {
        if (mProgressView.getChildCount() > 0) return mProgressView.getChildAt(0);
        return null;
    }


    public View getEmptyView() {
        if (mEmptyView.getChildCount() > 0) return mEmptyView.getChildAt(0);
        return null;
    }

    private static void log(String content) {
//        Log.i(TAG, content);
        /*if (DEBUG) {
            Log.i(TAG, content);
        }*/
    }
}
