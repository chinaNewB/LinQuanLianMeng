package com.example.linquanlianmeng.presenter.impl;

import com.example.linquanlianmeng.model.Api;
import com.example.linquanlianmeng.model.domain.OnSellContent;
import com.example.linquanlianmeng.presenter.IOnSellPagePresenter;
import com.example.linquanlianmeng.utils.LogUtils;
import com.example.linquanlianmeng.utils.RetrofitManagere;
import com.example.linquanlianmeng.utils.UrlUtils;
import com.example.linquanlianmeng.view.IOnSellPageCallBack;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnSellPagePresenterImpl implements IOnSellPagePresenter {

    public static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private final Api mApi;
    private IOnSellPageCallBack mOnSellPageCallBack = null;

    public OnSellPagePresenterImpl() {
        Retrofit retrofit = RetrofitManagere.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void getOnSellContent() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        //通知UI状态为加载中...
        if (mOnSellPageCallBack != null) {
            mOnSellPageCallBack.onLoading();
        }
        //获取特惠内容

        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        LogUtils.d(OnSellPagePresenterImpl.this, "targetUrl " + targetUrl);
        Call<OnSellContent> task = mApi.getOnSellPageContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                mIsLoading = false;
                int code = response.code();
                LogUtils.d(OnSellPagePresenterImpl.this, "result " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    onSuccess(result);
                    LogUtils.d(OnSellPagePresenterImpl.this, "result " + result.toString());

                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                onError();
                LogUtils.d(OnSellPagePresenterImpl.this, " onFailure " + t.toString());

            }
        });
    }

    private void onSuccess(OnSellContent result) {
        if (mOnSellPageCallBack != null) {
            try {
                if (isEmpty(result)) {
                    onEmpty();
                } else {
                    mOnSellPageCallBack.onContentLoadedSuccess(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onEmpty();
            }
        }
    }

    private boolean isEmpty(OnSellContent content) {
        int size = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        return size == 0;
    }

    private void onEmpty() {
        if (mOnSellPageCallBack != null) {
            mOnSellPageCallBack.onEmpty();
        }
    }


    private void onError() {
        mIsLoading = false;
        if (mOnSellPageCallBack != null) {
            mOnSellPageCallBack.onError();
        }
    }

    @Override
    public void reLoad() {
        //重新加载
        this.getOnSellContent();
    }

    /**
     * 当前加载状态
     */
    private boolean mIsLoading = false;

    @Override
    public void loaderMore() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        //加载更多
        mCurrentPage++;
        //去加载更多内容
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<OnSellContent> task = mApi.getOnSellPageContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                mIsLoading = false;
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    onMoreLoaded(result);
                } else {
                    onMoreLoadedError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                onMoreLoadedError();
            }
        });
    }

    private void onMoreLoadedError() {
        mIsLoading = false;
        mCurrentPage--;
        mOnSellPageCallBack.onMoreLoadedError();
    }

    /**
     * 加载更多的结果，通知UI更新
     *
     * @param result
     */
    private void onMoreLoaded(OnSellContent result) {
        if (mOnSellPageCallBack != null) {
            if (isEmpty(result)) {
                mCurrentPage--;
                mOnSellPageCallBack.onMoreLoadedEmpty();
            } else {
                mOnSellPageCallBack.onMoreLoaded(result);
            }
        }
    }

    @Override
    public void registerViewCallBack(IOnSellPageCallBack callBack) {
        this.mOnSellPageCallBack = callBack;
    }

    @Override
    public void unregisterViewCallBack(IOnSellPageCallBack callBack) {
        this.mOnSellPageCallBack = null;
    }
}
