package com.example.frutossecos.network;

public interface GenericCallback<T> {
    void onSuccess(T data);
    void onError(Error error);
}
