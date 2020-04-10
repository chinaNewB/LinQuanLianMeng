package com.example.linquanlianmeng.presenter.impl;

import com.example.linquanlianmeng.model.Api;
import com.example.linquanlianmeng.model.domain.SelectedContent;
import com.example.linquanlianmeng.model.domain.SelectedPageCategory;
import com.example.linquanlianmeng.presenter.ISelectedPagePresenter;
import com.example.linquanlianmeng.utils.RetrofitManagere;
import com.example.linquanlianmeng.utils.UrlUtils;
import com.example.linquanlianmeng.view.ISelectedPageCallBack;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectedPagePresenterImpl implements ISelectedPagePresenter {
    private ISelectedPageCallBack mViewCallBack = null;
    private final Api mApi;

    public SelectedPagePresenterImpl() {
        Retrofit retrofit = RetrofitManagere.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void getCategories() {
        if (mViewCallBack != null) {
            mViewCallBack.onLoading();
        }
        //拿retrofit
        Call<SelectedPageCategory> task = mApi.getSelectedPageCategories();
        task.enqueue(new Callback<SelectedPageCategory>() {
            @Override
            public void onResponse(Call<SelectedPageCategory> call, Response<SelectedPageCategory> response) {
                int code = response.code();
//                LogUtils.d(SelectedPagePresenterImpl.this, "result code --------  " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    SelectedPageCategory result = response.body();
                    //通知UI更新
                    if (mViewCallBack != null) {
                        mViewCallBack.onCategoriesLoaded(result);
                    }
                } else {
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedPageCategory> call, Throwable t) {
                onLoadedError();
            }
        });
    }

    private void onLoadedError() {
        if (mViewCallBack != null) {
            mViewCallBack.onError();
        }
    }

    @Override
    public void getContentByCategory(SelectedPageCategory.DataBean item) {

        int categoryId = item.getFavorites_id();
        String targetUrl = UrlUtils.getSelectedPageContentUrl(categoryId);
        Call<SelectedContent> task = mApi.getSelectedPageContent(targetUrl);
        task.enqueue(new Callback<SelectedContent>() {
            @Override
            public void onResponse(Call<SelectedContent> call, Response<SelectedContent> response) {
                int code = response.code();
//                LogUtils.d(SelectedPagePresenterImpl.this, "getContentByCategory result code --------  " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    SelectedContent result = response.body();
                    if (mViewCallBack != null) {
                        mViewCallBack.onContentLoaded(result);
                    }
                } else {
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedContent> call, Throwable t) {
                onLoadedError();
            }
        });
    }

    @Override
    public void reloadContent() {
        this.getCategories();
    }

    @Override
    public void registerViewCallBack(ISelectedPageCallBack callBack) {
        this.mViewCallBack = callBack;
    }

    @Override
    public void unregisterViewCallBack(ISelectedPageCallBack callBack) {
        this.mViewCallBack = null;
    }
}
