package com.example.frutossecos.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.CustomLoadingListItemCreator;
import com.example.frutossecos.R;
import com.example.frutossecos.SimpleItemTouchHelperCallback;
import com.example.frutossecos.adapters.ReportOrderAdapter;
import com.example.frutossecos.interfaces.OnStartDragListener;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.Client;
import com.example.frutossecos.network.models.ReportsOrder.ReportOrder;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class ListOrdersActivity extends BaseActivity implements Paginate.Callbacks, OnStartDragListener {

    private RecyclerView mRecyclerView;
    private ReportOrderAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_user_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        mRecyclerView = findViewById(R.id.list_orders);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ReportOrderAdapter(this, new ArrayList<ReportOrder>(),this);
        mAdapter.setHistoryUser(true);

        registerForContextMenu(mRecyclerView);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);

        implementsPaginate();

    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("OnResume");
        if(!isLoading()) {
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems=true;
            listOrders();

        }

    }

    private void listOrders(){
        loadingInProgress=true;
        ApiClient.get().getAllOrdersByPage(mCurrentPage, new GenericCallback<List<ReportOrder>>() {
            @Override
            public void onSuccess(List<ReportOrder> data) {
                if(data.size() == 0){
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapter.getItemCount();

                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        layoutManager.scrollToPosition(0);
                    }

                }
                loadingInProgress=false;
            }

            @Override
            public void onError(Error error) {
                loadingInProgress = false;
            }



        });
    }


    private void implementsPaginate(){

        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        paginate= Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator())
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return 0;
                    }
                })
                .build();
    }

    @Override
    public void onLoadMore() {
        listOrders();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }

}
