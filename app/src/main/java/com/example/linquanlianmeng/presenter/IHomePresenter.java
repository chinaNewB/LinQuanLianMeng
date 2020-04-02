package com.example.linquanlianmeng.presenter;

import com.example.linquanlianmeng.base.IBasePresenter;
import com.example.linquanlianmeng.view.IHomeCallBack;

public interface IHomePresenter extends IBasePresenter<IHomeCallBack> {

    /**
     * 获取商品分类
     * **/
    void getCategories();


}
