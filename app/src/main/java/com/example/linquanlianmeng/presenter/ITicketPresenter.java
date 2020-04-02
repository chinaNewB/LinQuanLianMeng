package com.example.linquanlianmeng.presenter;

import com.example.linquanlianmeng.base.IBasePresenter;
import com.example.linquanlianmeng.view.ITicketPagerCallback;

public interface ITicketPresenter extends IBasePresenter<ITicketPagerCallback> {

    /**
     * 生成淘口令
     * @param title
     * @param url
     * @param cover
     */
    void getTicket(String title,String url,String cover);

}
