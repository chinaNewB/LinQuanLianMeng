package com.example.linquanlianmeng.view;

import com.example.linquanlianmeng.base.IBaseCallBack;
import com.example.linquanlianmeng.model.domain.OnSellContent;

public interface IOnSellPageCallBack extends IBaseCallBack {

    /**
     * 特惠内容
     *
     * @param result
     */
    void onContentLoadedSuccess(OnSellContent result);

    /**
     * 加载更多结果
     *
     * @param moreResult
     */
    void onMoreLoaded(OnSellContent moreResult);


    /**
     * 加载更多失败
     */
    void onMoreLoadedError();


    /**
     * 没有更多内容
     */
    void onMoreLoadedEmpty();
}
