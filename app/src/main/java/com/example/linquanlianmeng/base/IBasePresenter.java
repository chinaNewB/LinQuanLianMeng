package com.example.linquanlianmeng.base;

public interface IBasePresenter<T> {


    /**
     * 注册UI通知接口
     * */
    void registerViewCallBack(T callBack);

    /**
     * 取消UI通知接口
     *
     * @param callBack
     */
    void unregisterViewCallBack(T callBack);
}
