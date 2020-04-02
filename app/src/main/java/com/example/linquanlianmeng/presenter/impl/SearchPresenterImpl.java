package com.example.linquanlianmeng.presenter.impl;

import com.example.linquanlianmeng.model.Api;
import com.example.linquanlianmeng.model.domain.Histories;
import com.example.linquanlianmeng.model.domain.SearchRecommend;
import com.example.linquanlianmeng.model.domain.SearchResult;
import com.example.linquanlianmeng.presenter.ISearchPresenter;
import com.example.linquanlianmeng.utils.JsonCacheUtil;
import com.example.linquanlianmeng.utils.RetrofitManagere;
import com.example.linquanlianmeng.view.ISerchPageCallBack;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenterImpl implements ISearchPresenter {

    private final Api mApi;
    private ISerchPageCallBack mSerchViewCallBack = null;
    private String mCurrentKeyword = null;
    private final JsonCacheUtil mJsonCacheUtil;

    public SearchPresenterImpl() {
        Retrofit retrofit = RetrofitManagere.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
        mJsonCacheUtil = JsonCacheUtil.getInstance();
    }

    public static final int DEFAULT_PAGE = 0;
    /**
     * 搜索的当前页
     */
    private int mCurrentPage = DEFAULT_PAGE;

    @Override
    public void getHistories() {
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES, Histories.class);
        if (mSerchViewCallBack != null) {
            mSerchViewCallBack.onHistoriesLoaded(histories);
        }

    }

    @Override
    public void delHistories() {
        mJsonCacheUtil.delCache(KEY_HISTORIES);

        if (mSerchViewCallBack != null) {
            mSerchViewCallBack.onHistoriesDeleted();
        }
    }


    public static final String KEY_HISTORIES = "key_histories";
    public static final int DEFAULT_HISTORIES_SIZE = 10;
    private int mHistoriesMaxSize = DEFAULT_HISTORIES_SIZE;
    /**
     * 添加历史记录
     *
     * @param history
     */
    private void saveHistory(String history) {
//        this.delHistories();
        Histories histories = mJsonCacheUtil.getValue(KEY_HISTORIES, Histories.class);

        //如果已经有了，就干掉，再添加
        List<String> historiesList = null;
        if (histories != null && histories.getHistories() != null) {
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }
        //去重完成
        //处理没有数据的情况
        if (historiesList == null) {
            historiesList = new ArrayList<>();
        }
        if (histories == null) {
            histories = new Histories();
            histories.setHistories(historiesList);
        }
        //对个数进行限制
        if (historiesList.size() > mHistoriesMaxSize) {
            historiesList.subList(0, mHistoriesMaxSize);
        }
        //添加记录
        historiesList.add(history);
        //保存记录
        mJsonCacheUtil.saveCache(KEY_HISTORIES,histories);

    }

    @Override
    public void doSearch(String keyoword) {
//        this.delHistories();
        if (mCurrentKeyword == null || !mCurrentKeyword.equals(keyoword)) {
            this.saveHistory(keyoword);
            this.mCurrentKeyword = keyoword;
        }
        //更新UI状态
        if (mSerchViewCallBack != null) {
            mSerchViewCallBack.onLoading();
        }

        Call<SearchResult> task = mApi.doSearch(mCurrentPage, keyoword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    handlerSearchResult(response.body());
                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onError();
            }
        });
    }

    private void onError() {
        if (mSerchViewCallBack != null) {
            mSerchViewCallBack.onError();
        }
    }

    private void handlerSearchResult(SearchResult result) {
        if (mSerchViewCallBack != null) {
            if (isResultEmpty(result)) {
                //数据为空
                mSerchViewCallBack.onEmpty();
            } else {
                mSerchViewCallBack.onSearchSuccess(result);
            }
        }
    }

    private boolean isResultEmpty(SearchResult result) {
        try {
            return result == null || result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void research() {
        if (mCurrentKeyword == null) {
            if (mSerchViewCallBack != null) {
                mSerchViewCallBack.onEmpty();
            }
        } else {
            //可以重新搜索
            this.doSearch(mCurrentKeyword);
        }
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        //进行搜索
        if (mCurrentKeyword == null) {
            if (mSerchViewCallBack != null) {
                mSerchViewCallBack.onEmpty();
            }
        } else {
            //做搜索的事情
            doSearchMore();
        }
    }

    private void doSearchMore() {
        Call<SearchResult> task = mApi.doSearch(mCurrentPage, mCurrentKeyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    handlerMoreSearchResult(response.body());
                } else {
                    onLoaderMoreError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onLoaderMoreError();
            }
        });
    }

    /**
     * 处理加载更多的结果
     *
     * @param body
     */
    private void handlerMoreSearchResult(SearchResult body) {
        if (mSerchViewCallBack != null) {
            if (isResultEmpty(body)) {
                //数据为空
                mSerchViewCallBack.onMoreLoadedEmpty();
            } else {
                mSerchViewCallBack.onMoreLoaded(body);
            }


        }
    }

    /**
     * 加载更多内容失败
     */
    private void onLoaderMoreError() {
        mCurrentPage--;
        if (mSerchViewCallBack != null) {
            mSerchViewCallBack.onMoreLoadedError();
        }
    }

    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = mApi.getRecommendWords();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    //处理结果
                    if (mSerchViewCallBack != null) {
                        mSerchViewCallBack.onRecommendWordsLoaded(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void registerViewCallBack(ISerchPageCallBack callBack) {
        this.mSerchViewCallBack = callBack;
    }

    @Override
    public void unregisterViewCallBack(ISerchPageCallBack callBack) {
        this.mSerchViewCallBack = null;
    }
}
