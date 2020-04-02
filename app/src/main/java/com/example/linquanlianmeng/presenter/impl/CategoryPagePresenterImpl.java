package com.example.linquanlianmeng.presenter.impl;

import com.example.linquanlianmeng.model.Api;
import com.example.linquanlianmeng.model.domain.HomePagerContent;
import com.example.linquanlianmeng.presenter.ICategoryPagerPresenter;
import com.example.linquanlianmeng.utils.LogUtils;
import com.example.linquanlianmeng.utils.RetrofitManagere;
import com.example.linquanlianmeng.utils.UrlUtils;
import com.example.linquanlianmeng.view.ICategoryPagerCallBack;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryPagePresenterImpl implements ICategoryPagerPresenter {

    private static final Integer DEFAULT_PAGE = 1;
    private Map<Integer, Integer> pagesInfo = new HashMap<>();
    private Integer mCurrentPage;


    @Override
    public void getContentByCategoryId(int categoryId) {
        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoading();
            }
        }

        //根据分类id去加载内容
        // TODO: 2020/3/4

        Integer targetPage = pagesInfo.get(categoryId);
        if (targetPage == null) {
            targetPage = DEFAULT_PAGE;
            pagesInfo.put(categoryId, DEFAULT_PAGE);
        }

        Call<HomePagerContent> task = createTask(categoryId, targetPage);
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                int code = response.code();
                LogUtils.d(CategoryPagePresenterImpl.this, "code ---- " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomePagerContent pageContent = response.body();
                    LogUtils.d(CategoryPagePresenterImpl.this, "pageContent ------ " + pageContent);
                    //把数据给到UI更新
                    handlerHomePageContentResult(pageContent, categoryId);
                } else {
                    // TODO: 2020/3/4
                    handlerNetworkError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                LogUtils.d(CategoryPagePresenterImpl.this, "onFailure ---- " + t.toString());
                handlerNetworkError(categoryId);

            }
        });

    }

    private Call<HomePagerContent> createTask(int categoryId, Integer targetPage) {
        String homePagerUrl = UrlUtils.createHomePagerUrl(categoryId, targetPage);
        LogUtils.d(this, "homePagerUrl ------- " + homePagerUrl);
        Retrofit retrofit = RetrofitManagere.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        return api.getHomePagerContent(homePagerUrl);
    }

    private void handlerNetworkError(int categoryId) {
        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onError();
            }
        }
    }

    private void handlerHomePageContentResult(HomePagerContent pageContent, int categoryId) {

        List<HomePagerContent.DataBean> data = pageContent.getData();
        //通知UI层更新数据
        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (pageContent == null || pageContent.getData().size() == 0) {
                    callback.onEmpty();
                } else {
                    List<HomePagerContent.DataBean> loopData = data.subList(data.size() - 5, data.size());
                    callback.onLooperListLoaded(loopData);
                    callback.onContentLoaded(data);
                }
            }
        }
    }

    @Override
    public void loadMore(int categoryId) {
        //加载更多数据
        //1.拿到当前页码
        mCurrentPage = pagesInfo.get(categoryId);
        if (mCurrentPage == null) {
            mCurrentPage = 1;
        }
        //2.页码++
        mCurrentPage++;
        //3.加载数据
        Call<HomePagerContent> task = createTask(categoryId, mCurrentPage);
        //4.处理数据结果
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                //结果
                int code = response.code();
                LogUtils.d(CategoryPagePresenterImpl.this, "result code --- " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    HomePagerContent result = response.body();
                    LogUtils.d(CategoryPagePresenterImpl.this,"result ---- " + result.toString());
                    handlerLoadeResult(result, categoryId);
                } else {
                    handlerLoaderMoreError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable t) {
                //请求失败
                LogUtils.d(CategoryPagePresenterImpl.this, t.toString());
                handlerLoaderMoreError(categoryId);
            }
        });
    }

    private void handlerLoadeResult(HomePagerContent result, int categoryId) {
        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (result == null || result.getData().size() == 0) {
                    callback.onLoaderMoreEmpty();
                } else {
                    callback.onLoaderMoreLoaded(result.getData());
                }
            }
        }
    }

    private void handlerLoaderMoreError(int categoryId) {
        mCurrentPage--;
        pagesInfo.put(categoryId, mCurrentPage);
        for (ICategoryPagerCallBack callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoaderMoreError();
            }
        }
    }



    private List<ICategoryPagerCallBack> callbacks = new ArrayList<>();

    @Override
    public void reload(int categoryId) {
        getContentByCategoryId(categoryId);
    }


    @Override
    public void registerViewCallBack(ICategoryPagerCallBack callBack) {
        if (!callbacks.contains(callbacks)) {
            callbacks.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallBack(ICategoryPagerCallBack callBack) {
        callbacks.remove(callBack);
    }

}
