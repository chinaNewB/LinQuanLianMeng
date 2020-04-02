package com.example.linquanlianmeng.presenter;

import com.example.linquanlianmeng.base.IBasePresenter;
import com.example.linquanlianmeng.view.ICategoryPagerCallBack;

public interface ICategoryPagerPresenter extends IBasePresenter<ICategoryPagerCallBack> {

    /**
     * 根据分类id去获取内容
     *
     * @param categoryId
     */
    void getContentByCategoryId(int categoryId);

    void loadMore(int categoryId);

    void reload(int categoryId);



}
