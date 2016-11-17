package com.example.wtz.learntosing.api;

import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class RestCallback<T> implements retrofit.Callback<T> {
    @Override
    public void success(T t, Response response) {
        if (t == null) {
            onFailure();
        }

        onSuccess(t);
    }

    @Override
    public void failure(RetrofitError error) {
        error.printStackTrace();
        onFailure();
    }

    protected abstract void onSuccess(T t);

    protected abstract void onFailure();
}
