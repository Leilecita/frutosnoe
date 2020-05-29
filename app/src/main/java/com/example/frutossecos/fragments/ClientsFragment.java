package com.example.frutossecos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.CustomLoadingListItemCreator;
import com.example.frutossecos.Events.EventListUsersState;
import com.example.frutossecos.R;
import com.example.frutossecos.activities.CreateUserActivity;
import com.example.frutossecos.adapters.ClientAdapter;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.Client;
import com.example.frutossecos.network.models.ReportClient;
import com.example.frutossecos.network.models.ReportsOrder.ValuesOrderReport;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.UUID;

public class ClientsFragment extends BaseFragment implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private ClientAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;
    private TextView pendients;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;
    private String mQuery = "";
    private String token = "";

    private LinearLayout lineDebt;
    private TextView totalDebt;


    public void onClickButton() {
        activityAddUser();
    }

    public int getIconButton() {
        return R.mipmap.addperson2;
    }

    public int getVisibility() {
        return 0;
    }

    public void onClickAction() {

    }

    @Override
    public void onRefresh() {

    }

    public ClientsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_clients, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_users);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ClientAdapter(getActivity(), new ArrayList<Client>());

        registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);

        pendients = mRootView.findViewById(R.id.pendients);
        lineDebt = mRootView.findViewById(R.id.line_debt);
        totalDebt = mRootView.findViewById(R.id.total_debt);


        final SearchView searchView = mRootView.findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });

        searchView.setQueryHint("Buscar");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.trim().toLowerCase().equals(mQuery)) {
                    // mCurrentPage = 0;
                    //mAdapter.clear();
                    clearview();
                    listUsers(newText.trim().toLowerCase());
                }
                return false;
            }
        });

        implementsPaginate();
        loadCantOrdersPendient();

        EventBus.getDefault().register(this);

        return mRootView;
    }

    private void loadCantOrdersPendient() {

        ApiClient.get().getTotalOrdersPendients(new GenericCallback<ValuesOrderReport>() {
            @Override
            public void onSuccess(ValuesOrderReport data) {
                pendients.setText(String.valueOf(data.pendients));
            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("OnResume");
        if (!isLoading()) {
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems = true;
            listUsers("");
            loadCantOrdersPendient();
        }

    }

    private void clearview() {
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems = true;
    }

    public void listUsers(final String query) {
        loadingInProgress = true;
        this.mQuery = query;
        final String newToken = UUID.randomUUID().toString();
        this.token = newToken;
        ApiClient.get().getClients(query,mCurrentPage ,new GenericCallback<ReportClient>() {
            @Override
            public void onSuccess(ReportClient data) {

                if (token.equals(newToken)) {
                    Log.e("TOKEN", "Llega token: " + newToken);
                    if (query == mQuery) {

                        if (data.listClients.size() == 0) {
                            hasMoreItems = false;
                        } else {
                            int prevSize = mAdapter.getItemCount();
                            mAdapter.pushList(data.listClients);
                            mCurrentPage++;
                            if (prevSize == 0) {
                                layoutManager.scrollToPosition(0);
                            }
                        }
                        loadingInProgress = false;
                    }
                } else {
                    Log.e("TOKEN", "Descarta token: " + newToken);
                }

                if ((Double.compare(data.totalDebt, 0d) > 0)){
                    lineDebt.setVisibility(View.VISIBLE);
                    totalDebt.setText(String.valueOf(data.totalDebt));
                }else{
                    lineDebt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Error error) {
                loadingInProgress = false;
            }
        });
    }

    private void activityAddUser() {
         startActivity(new Intent(getContext(), CreateUserActivity.class));
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Ingrese nombre");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.requestFocus();
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.trim().toLowerCase().equals(mQuery)) {
                    mCurrentPage = 0;
                    mAdapter.clear();
                    listUsers(newText.trim().toLowerCase());
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void implementsPaginate() {

        loadingInProgress = false;
        mCurrentPage = 0;
        hasMoreItems = true;

        paginate = Paginate.with(mRecyclerView, this)
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
        public void onLoadMore () {
            listUsers(mQuery);
        }

        @Override
        public boolean isLoading () {
            return loadingInProgress;
        }

        @Override
        public boolean hasLoadedAllItems () {
            return !hasMoreItems;
        }

    @Subscribe
    public void onEvent(EventListUsersState event){
        clearview();
        listUsers(mQuery);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {

        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
