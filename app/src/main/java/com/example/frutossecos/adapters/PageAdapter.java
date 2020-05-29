package com.example.frutossecos.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.frutossecos.fragments.BaseFragment;
import com.example.frutossecos.fragments.ClientsFragment;
import com.example.frutossecos.fragments.OrdersFragment;
import com.example.frutossecos.fragments.SummaryDayFragment;

import java.util.ArrayList;

public class PageAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private ArrayList<BaseFragment> mFragments;

    public PageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragments = new ArrayList<>();
        //  mFragments.add(new SpendingFragment());
        mFragments.add(new ClientsFragment());
        mFragments.add(new OrdersFragment());
        mFragments.add(new SummaryDayFragment());
        // mFragments.add(new OutcomesFragment());
        // mFragments.add(new IncomesFragment());


        // mFragments.add(new PreimpresoFragment().setChangeListener(this));
        // mFragments.add(new MistakeFragment());
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    /*  @Override
      public void onChange(Fragment fragment) {
          notifyDataSetChanged();
      }
*/
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {


        if (position == 0) {
            return "Clientes";
        } else if (position == 1) {
            return "Pedidos";
        } else {
            return "Resumen";
        }
    }
}