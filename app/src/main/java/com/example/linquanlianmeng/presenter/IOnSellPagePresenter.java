package com.example.linquanlianmeng.presenter;

import com.example.linquanlianmeng.base.IBasePresenter;
import com.example.linquanlianmeng.view.IOnSellPageCallBack;

public interface IOnSellPagePresenter extends IBasePresenter<IOnSellPageCallBack> {

    /**
     * 加载特惠内容
     */
    void getOnSellContent();

    /**
     * 重新加载内容
     *
     * @Call 网络出问题，恢复网络以后
     */
    void reLoad();

    /**
     * 加载更多特惠内容
     */
    void loaderMore();
}
