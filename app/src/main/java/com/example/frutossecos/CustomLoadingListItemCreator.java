package com.example.frutossecos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.paginate.recycler.LoadingListItemCreator;

public class CustomLoadingListItemCreator implements LoadingListItemCreator {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Bind custom loading  VH vh = (VH) holder;
        VH vh = (VH) holder;
        //  vh.tvLoading.setText("loading..");
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvLoading;

        public VH(View itemView) {
            super(itemView);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading_text);
        }
    }
}