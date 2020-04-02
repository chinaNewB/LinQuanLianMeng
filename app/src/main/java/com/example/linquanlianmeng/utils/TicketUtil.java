package com.example.linquanlianmeng.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.linquanlianmeng.model.domain.IBaseInfo;
import com.example.linquanlianmeng.presenter.ITicketPresenter;
import com.example.linquanlianmeng.ui.activity.TicketActivity;

public class TicketUtil {
    public static void toTicketPage(Context context,IBaseInfo baseInfo) {
        //内容点击了
        String title = baseInfo.getTitle();
        //详情的地址
        String url = baseInfo.getUrl();
        if (TextUtils.isEmpty(url)) {
            url = baseInfo.getUrl();
        }
        String cover = baseInfo.getCover();
        //拿到TicketPresenter去加载数据
        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
        ticketPresenter.getTicket(title,url,cover);
        context.startActivity(new Intent(context, TicketActivity.class));
    }
}
