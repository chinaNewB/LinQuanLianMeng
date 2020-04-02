package com.example.linquanlianmeng.presenter.impl;

import com.example.linquanlianmeng.model.Api;
import com.example.linquanlianmeng.model.domain.Categories;
import com.example.linquanlianmeng.presenter.IHomePresenter;
import com.example.linquanlianmeng.utils.LogUtils;
import com.example.linquanlianmeng.utils.RetrofitManagere;
import com.example.linquanlianmeng.view.IHomeCallBack;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePresenterImpl implements IHomePresenter {
    private IHomeCallBack mCallBack;

    @Override
    public void getCategories() {
        if (mCallBack != null) {
            mCallBack.onLoading();
        }
        //加载分类数据
        Retrofit retrofit = RetrofitManagere.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<Categories> task = api.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                //数据结果
                int code = response.code();
                LogUtils.d(HomePresenterImpl.this, "result code is ------------> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    Categories categories = response.body();
                    if (mCallBack != null) {
                        if (categories == null || categories.getData().size() == 0) {
                            mCallBack.onEmpty();
                        } else {
                            mCallBack.onCategoriesLoaded(categories);
                            LogUtils.d(HomePresenterImpl.this, categories.toString());
                        }
                    }
                } else {
                    //请求失败
                    LogUtils.i(HomePresenterImpl.this, "请求失败...");
                    if (mCallBack != null) {
                        mCallBack.onError();
                    }
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                //加载失败的结果
                // TODO: 2020/3/3
                LogUtils.e(this, "请求错误..." + t);
                if (mCallBack != null) {
                    mCallBack.onError();
                }

            }
        });
    }


    @Override
    public void registerViewCallBack(IHomeCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public void unregisterViewCallBack(IHomeCallBack callBack) {
        mCallBack = null;
    }
}
