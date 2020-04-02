package com.example.linquanlianmeng.presenter;

import com.example.linquanlianmeng.base.IBasePresenter;
import com.example.linquanlianmeng.view.ISerchPageCallBack;

public interface ISearchPresenter extends IBasePresenter<ISerchPageCallBack> {

    /**
     * 获取搜索历史内容
     */
    void getHistories();

    /**
     * 删除搜索历史内容
     */
    void delHistories();


    /**
     * 搜索
     *
     * @param keyoword
     */
    void doSearch(String keyoword);

    /**
     * 重新搜索
     */
    void research();

    /**
     * 获取更多的搜索结果
     */
    void loaderMore();

    /**
     * 获取推荐词
     */
    void getRecommendWords();
}
