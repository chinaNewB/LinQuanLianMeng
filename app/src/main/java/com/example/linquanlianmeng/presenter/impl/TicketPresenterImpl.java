package com.example.linquanlianmeng.presenter.impl;

import com.example.linquanlianmeng.model.Api;
import com.example.linquanlianmeng.model.domain.TicketParams;
import com.example.linquanlianmeng.model.domain.TicketResult;
import com.example.linquanlianmeng.presenter.ITicketPresenter;
import com.example.linquanlianmeng.utils.LogUtils;
import com.example.linquanlianmeng.utils.RetrofitManagere;
import com.example.linquanlianmeng.utils.UrlUtils;
import com.example.linquanlianmeng.view.ITicketPagerCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {
    private ITicketPagerCallback mViewCallBack = null;
    private String mCover = null;
    private TicketResult mTicketResult;


    enum LoadState {
        LOADING, SUCCESS, ERROR, NONE
    }

    private LoadState mCurrentState = LoadState.NONE;


    @Override
    public void getTicket(String title, String url, String cover) {
        this.mCover = cover;
        this.onTicketLoading();

        LogUtils.d(TicketPresenterImpl.this, "title ---- " + title);
        LogUtils.d(TicketPresenterImpl.this, "url ---- " + url);
        LogUtils.d(TicketPresenterImpl.this, "cover ---- " + cover);

        String targetUrl = UrlUtils.getTicketUrl(url);
        //TODO:去获取淘口令
        Retrofit retrofit = RetrofitManagere.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(targetUrl, title);

        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {

            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                LogUtils.d(TicketPresenterImpl.this, "code ---- " + code);

                if (code == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    mTicketResult = response.body();
                    LogUtils.d(TicketPresenterImpl.this, "ticket body --- " + mTicketResult.toString());
                    //通知UI更新
                    onTicketLoadedSuccess();
                } else {
                    //请求失败
                    onLoadedTicketError();
                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                //网络错误
                LogUtils.d(TicketPresenterImpl.this, t.toString());
                onLoadedTicketError();
            }
        });
    }

    private void onTicketLoadedSuccess() {
        LogUtils.d(this,"onTicketLoadedSuccess ---- " + mCover);
        LogUtils.d(this,"onTicketLoadedSuccess ---- " + mTicketResult);
        if (mViewCallBack != null) {
            mViewCallBack.onTicketLoaded(mCover, mTicketResult);
        } else {
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void onLoadedTicketError() {
        if (mViewCallBack != null) {
            mViewCallBack.onError();
        } else {
            mCurrentState = LoadState.ERROR;
        }
    }

    @Override
    public void registerViewCallBack(ITicketPagerCallback callback) {
        this.mViewCallBack = callback;
        if (mCurrentState != LoadState.NONE) {
            //说明状态已经改变了
            //更新UI
            if (mCurrentState == LoadState.SUCCESS) {
                onTicketLoadedSuccess();
            } else if (mCurrentState == LoadState.ERROR) {
                onLoadedTicketError();
            } else if (mCurrentState == LoadState.LOADING) {
                onTicketLoading();
            }
        }
    }

    private void onTicketLoading() {
        if (mViewCallBack != null) {
            mViewCallBack.onLoading();
        } else {
            mCurrentState = LoadState.LOADING;
        }
    }

    @Override
    public void unregisterViewCallBack(ITicketPagerCallback callback) {
        this.mViewCallBack = null;
    }
}
