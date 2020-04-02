package com.example.linquanlianmeng.view;

import com.example.linquanlianmeng.base.IBaseCallBack;
import com.example.linquanlianmeng.model.domain.Categories;

public interface IHomeCallBack extends IBaseCallBack {

    void onCategoriesLoaded(Categories categories);

}
