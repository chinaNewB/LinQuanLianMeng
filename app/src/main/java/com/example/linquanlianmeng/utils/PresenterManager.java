package com.example.linquanlianmeng.utils;

import com.example.linquanlianmeng.presenter.ICategoryPagerPresenter;
import com.example.linquanlianmeng.presenter.IHomePresenter;
import com.example.linquanlianmeng.presenter.IOnSellPagePresenter;
import com.example.linquanlianmeng.presenter.ISearchPresenter;
import com.example.linquanlianmeng.presenter.ISelectedPagePresenter;
import com.example.linquanlianmeng.presenter.ITicketPresenter;
import com.example.linquanlianmeng.presenter.impl.CategoryPagePresenterImpl;
import com.example.linquanlianmeng.presenter.impl.HomePresenterImpl;
import com.example.linquanlianmeng.presenter.impl.OnSellPagePresenterImpl;
import com.example.linquanlianmeng.presenter.impl.SearchPresenterImpl;
import com.example.linquanlianmeng.presenter.impl.SelectedPagePresenterImpl;
import com.example.linquanlianmeng.presenter.impl.TicketPresenterImpl;

public class PresenterManager {
    private static final PresenterManager ourInstance = new PresenterManager();
    private final ICategoryPagerPresenter mCategoryPagePresenter;
    private final IHomePresenter mHomePresenter;
    private final ITicketPresenter mTicketPresenter;
    private final ISelectedPagePresenter mSelectedPagePresenter;
    private final IOnSellPagePresenter mOnSellPagePresenter;
    private final ISearchPresenter mSearchPresenter;

    public static PresenterManager getInstance() {
        return ourInstance;
    }

    public ICategoryPagerPresenter getCategoryPagePresenter() {
        return mCategoryPagePresenter;
    }

    public IHomePresenter getHomePresenter() {
        return mHomePresenter;
    }

    public ITicketPresenter getTicketPresenter() {
        return mTicketPresenter;
    }

    public ISelectedPagePresenter getSelectedPagePresenter() {
        return mSelectedPagePresenter;
    }

    public IOnSellPagePresenter getOnSellPagePresenter() {
        return mOnSellPagePresenter;
    }

    public ISearchPresenter getSearchPresenter() {
        return mSearchPresenter;
    }

    private PresenterManager() {
        mCategoryPagePresenter = new CategoryPagePresenterImpl();
        mHomePresenter = new HomePresenterImpl();
        mTicketPresenter = new TicketPresenterImpl();
        mSelectedPagePresenter = new SelectedPagePresenterImpl();
        mOnSellPagePresenter = new OnSellPagePresenterImpl();
        mSearchPresenter = new SearchPresenterImpl();
    }
}
