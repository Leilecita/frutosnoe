package com.example.frutossecos.fragments;

import androidx.fragment.app.Fragment;

import com.example.frutossecos.R;
import com.example.frutossecos.interfaces.OnFloatingButton;

public class BaseFragment extends Fragment implements OnFloatingButton {

    public void onRefresh(){
    }

    public void onClickButton(){}


    public int getIconButton(){
        return R.drawable.change;
    }

    public int getVisibility(){return 0;}

}
