package com.example.linquanlianmeng.presenter;

import com.example.linquanlianmeng.base.IBasePresenter;
import com.example.linquanlianmeng.model.domain.SelectedPageCategory;
import com.example.linquanlianmeng.view.ISelectedPageCallBack;

public interface ISelectedPagePresenter extends IBasePresenter<ISelectedPageCallBack> {
    /**
     * 获取分类
     */
    void getCategories();

    /**
     * 根据分类获取分类内容
     *
     * @param item
     */
    void getContentByCategory(SelectedPageCategory.DataBean item);


    /**
     * 重新加载内容
     */
    void reloadContent();
}
