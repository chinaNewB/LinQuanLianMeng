package com.example.linquanlianmeng.view;

import com.example.linquanlianmeng.base.IBaseCallBack;
import com.example.linquanlianmeng.model.domain.HomePagerContent;

import java.util.List;

public interface ICategoryPagerCallBack extends IBaseCallBack {

    /**
     * 数据加载回来
     *
     * @param contents
     */
    void onContentLoaded(List<HomePagerContent.DataBean> contents);

    int getCategoryId();

    /**
     * 加载更多网络错误
     *
     */
    void onLoaderMoreError();

    /**
     * 没有更多内容
     *
     */
    void onLoaderMoreEmpty();

    /**
     * 加载了更多内容
     *
     */
    void onLoaderMoreLoaded(List<HomePagerContent.DataBean> contents);


    /**
     * 轮播图内容加载到了
     *
     */
    void onLooperListLoaded(List<HomePagerContent.DataBean> contents);
}
