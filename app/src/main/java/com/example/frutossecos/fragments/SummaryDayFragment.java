package com.example.frutossecos.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.CurrentValuesHelper;
import com.example.frutossecos.DateHelper;
import com.example.frutossecos.Events.EventOrderState;
import com.example.frutossecos.R;
import com.example.frutossecos.adapters.ItemSummaryAdapter;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.ReportsOrder.SummaryDay;
import com.example.frutossecos.network.models.ReportsOrder.ValuesDay;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SummaryDayFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private ItemSummaryAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;
    private TextView mDeliver_date;
    private TextView mSumTot;
    private TextView mSumDone;
    private TextView mSumPendient;


    public void onClickButton(){  }
    public int getIconButton(){
        return R.mipmap.add;
    }

    public int getVisibility(){
        return 4;
    }
    public SummaryDayFragment() {
    }

    @Override
    public void onRefresh() {

    }
    public String getHint() {
        return "hola";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.summary_day_fragment, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_summary);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ItemSummaryAdapter(getActivity(),new ArrayList<SummaryDay>());

        registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);

        loadSummaryDay();

        mDeliver_date= mRootView.findViewById(R.id.deliver_date);
        mSumDone= mRootView.findViewById(R.id.sumDone);
        mSumPendient= mRootView.findViewById(R.id.sumPendient);
        mSumTot= mRootView.findViewById(R.id.sumTot);

        if(CurrentValuesHelper.get().getSummaryDate()!=null)
        mDeliver_date.setText(DateHelper.get().getOnlyDate(CurrentValuesHelper.get().getSummaryDate()));

        mDeliver_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

        EventBus.getDefault().register(this);

        return mRootView;
    }

    public void loadSummaryDay(){
        listItemsDay();
        getValuesDay();
    }

    @Override
    public void onResume() {

        super.onResume();
        loadSummaryDay();
    }

    @Subscribe
    public void onEvent(EventOrderState event){
        if(event.getDate().equals(CurrentValuesHelper.get().getSummaryDate())){
            loadSummaryDay();
            Toast.makeText(getContext(), event.getState(), Toast.LENGTH_SHORT).show();
        }
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

    private void getValuesDay(){

        String date=CurrentValuesHelper.get().getSummaryDate();
        if(date!=null){
            ApiClient.get().getValuesDay(date, new GenericCallback<ValuesDay>() {
                @Override
                public void onSuccess(ValuesDay data) {
                    mSumDone.setText(String.valueOf(round(data.sumDone,1)));
                    mSumPendient.setText(String.valueOf(round(data.sumPendient,1)));
                    mSumTot.setText(String.valueOf(round(data.sumTot,1)));
                }

                @Override
                public void onError(Error error) {

                }
            });
        }
    }

    private void selectDate(){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        datePickerDialog = new DatePickerDialog(getContext(),R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String sdayOfMonth = String.valueOf(dayOfMonth);
                        if (sdayOfMonth.length() == 1) {
                            sdayOfMonth = "0" + dayOfMonth;
                        }
                        String smonthOfYear = String.valueOf(monthOfYear + 1);
                        if (smonthOfYear.length() == 1) {
                            smonthOfYear = "0" + smonthOfYear;
                        }
                        mDeliver_date.setText(sdayOfMonth+"-"+smonthOfYear+"-"+year);
                        CurrentValuesHelper.get().setSummaryDate(year+"-"+smonthOfYear+"-"+sdayOfMonth+" 05:00:00");
                        listItemsDay();
                        getValuesDay();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void listItemsDay(){
        String date=CurrentValuesHelper.get().getSummaryDate();
        if(date!=null){

            ApiClient.get().getSummaryDay(date, new GenericCallback<List<SummaryDay>>() {
                @Override
                public void onSuccess(List<SummaryDay> data) {
                    mAdapter.setItems(data);
                }

                @Override
                public void onError(Error error) {

                }
            });
        }

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        System.out.println(String.valueOf((double) tmp / factor));
        return (double) tmp / factor;
    }

}
