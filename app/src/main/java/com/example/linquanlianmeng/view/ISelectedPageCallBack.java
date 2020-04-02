package com.example.linquanlianmeng.view;

import com.example.linquanlianmeng.base.IBaseCallBack;
import com.example.linquanlianmeng.model.domain.SelectedContent;
import com.example.linquanlianmeng.model.domain.SelectedPageCategory;

public interface ISelectedPageCallBack extends IBaseCallBack {

    /**
     * 分类内容结果
     *
     * @param categories
     */
    void onCategoriesLoaded(SelectedPageCategory categories);

    /**
     * 内容
     *
     * @param content
     */
    void onContentLoaded(SelectedContent content);

}
