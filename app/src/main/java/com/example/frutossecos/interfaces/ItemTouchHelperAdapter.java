package com.example.frutossecos.interfaces;

public interface ItemTouchHelperAdapter {
    boolean isDragEnabled();

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
