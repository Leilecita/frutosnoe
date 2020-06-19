package com.example.frutossecos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.frutossecos.R;
import com.example.frutossecos.adapters.PageAdapter;
import com.example.frutossecos.fragments.BaseFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends BaseActivity {
    PageAdapter mAdapter;
    TabLayout mTabLayout;

    FloatingActionButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager viewPager =  findViewById(R.id.viewpager);
        mAdapter = new PageAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mTabLayout =  findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.setSelectedTabIndicatorHeight(10);

        button= findViewById(R.id.fab_agregarTod);

        actionFloatingButton();
        setImageButton();
        setVisibilityButton();

        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_text);
                View v= tab.getCustomView();
                TextView t= v.findViewById(R.id.text1);

                setTextByPosition(t,i);

            }
        }


        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                View im=tab.getCustomView();
                TextView t=im.findViewById(R.id.text1);
                t.setTextColor(getResources().getColor(R.color.white));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View im=tab.getCustomView();
                TextView t=im.findViewById(R.id.text1);
                t.setTextColor(getResources().getColor(R.color.word_clear_clear));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setImageButton();
                actionFloatingButton();
                setVisibilityButton();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setTextByPosition(TextView t, Integer i) {
        if (i == 0) {
            t.setText("CLIENTES");
            t.setTextColor(getResources().getColor(R.color.white));
        } else if (i == 1) {
            t.setText("PEDIDOS");
            t.setTextColor(getResources().getColor(R.color.word_clear_clear));
        } else if (i == 2) {
            t.setText("VENTAS");
            t.setTextColor(getResources().getColor(R.color.word_clear_clear));
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_products:
                startProductsActivity();
                return true;
            case R.id.action_all_orders:
                startAllOrdersActivity();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startProductsActivity(){
        startActivity(new Intent(MainActivity.this, ProductActivity.class));
    }
    private void startAllOrdersActivity(){
        startActivity(new Intent(MainActivity.this, ListOrdersActivity.class));
    }

    private void startStatisticActivity(){
       // startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
    }


    public void actionFloatingButton(){
        int position = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(position);

        if(f instanceof BaseFragment){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((BaseFragment)f).onClickButton();
                }
            });
        }
    }


    public void refreshFragment(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);
        if( f instanceof BaseFragment){
            ((BaseFragment)f).onRefresh();
        }
    }


    public void setVisibilityButton(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);

        if( f instanceof BaseFragment){
            button.setVisibility(((BaseFragment)f).getVisibility());
        }


    }
    public void setImageButton(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);

        if( f instanceof BaseFragment){

            button.setImageResource(((BaseFragment)f).getIconButton());
            button.setColorFilter(this.getResources().getColor(R.color.colorPrimaryDark));

        }



    }
}
