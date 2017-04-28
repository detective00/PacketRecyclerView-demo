package com.javon.packetrecyclerview.demo;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.javon.packetrecyclerview.BaseRecyclerAdapter;
import com.javon.packetrecyclerview.PacketRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.javon.packetrecyclerview.BaseRecyclerAdapter.SCALEIN;

public class MainActivity extends AppCompatActivity implements BaseRecyclerAdapter.OnEventDriver, SwipeRefreshLayout.OnRefreshListener {

    private PacketRecyclerView prv;
    private RecyclerAdapter adapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new RecyclerAdapter(this);
        adapter.openLoadAnimation(SCALEIN, true); // 开启动画
        adapter.setNoMore(R.layout.view_footer_nomore);
        adapter.setMore(R.layout.view_footer_more, this);
        adapter.setError(R.layout.view_footer_error, this);
        prv = (PacketRecyclerView) findViewById(R.id.prv_home);
        prv.setLayoutManager(new LinearLayoutManager(this));
        prv.setAdapterWithProgress(adapter);
        prv.setRefreshListener(this);
        prv.setEventListener(this);
        getVirtualData(page, true);
    }

    @Override
    public void onRefresh() {
        page = 1;
        getVirtualData(page, false);

    }

    @Override
    public void onLoadMore() {
        page ++;
        getVirtualData(page, true);
    }

    @Override
    public void onRetryMore() {
        getVirtualData(page, false);
    }

    @Override
    public void onRetryError() {
        getVirtualData(page, false);
    }

    private void getVirtualData(final int page, final boolean isError){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> data = new ArrayList<>();
                for (int i = 0; i < 10 ; i ++){
                    data.add("VirtualData == >>" + page + " "+ i);
                }
                if(page == 1)
                    adapter.clear();
                adapter.addAllData(isError ? null : data, isError);
            }
        }, 2500);
    }


}
