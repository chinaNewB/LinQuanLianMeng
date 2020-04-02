package com.example.linquanlianmeng.view;

import com.example.linquanlianmeng.base.IBaseCallBack;
import com.example.linquanlianmeng.model.domain.TicketResult;

public interface ITicketPagerCallback extends IBaseCallBack {
    /**
     * 淘口令加载结果
     *
     * @param cover
     * @param result
     */
    void onTicketLoaded(String cover, TicketResult result);
}
