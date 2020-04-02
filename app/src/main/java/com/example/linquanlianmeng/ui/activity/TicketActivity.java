package com.example.linquanlianmeng.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linquanlianmeng.R;
import com.example.linquanlianmeng.base.BaseActivity;
import com.example.linquanlianmeng.model.domain.TicketResult;
import com.example.linquanlianmeng.presenter.ITicketPresenter;
import com.example.linquanlianmeng.utils.LogUtils;
import com.example.linquanlianmeng.utils.PresenterManager;
import com.example.linquanlianmeng.utils.ToastUtil;
import com.example.linquanlianmeng.utils.UrlUtils;
import com.example.linquanlianmeng.view.ITicketPagerCallback;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketPagerCallback {

    private ITicketPresenter mTicketPresenter;
    private boolean mHasTaobaoApp = false;

    @BindView(R.id.ticket_cover)
    public ImageView mCover;

    @BindView(R.id.ticket_code)
    public EditText mTicketCode;

    @BindView(R.id.ticket_back_press)
    public View mBackPress;

    @BindView(R.id.ticket_copy_or_open_btn)
    public TextView mCopyOrOpenBtn;

    @BindView(R.id.ticket_cover_loading)
    public View loadingView;

    @BindView(R.id.ticket_load_retry)
    public View retryLoadText;

    @Override
    protected void initPresenter() {
        mTicketPresenter = PresenterManager.getInstance().getTicketPresenter();
        if (mTicketPresenter != null) {
            mTicketPresenter.registerViewCallBack(this);
        }
        //判断是否安装淘宝
        //adb命令：
        /**
         * 1.adb shell
         * 2.logcat | grep START
         * 3.点击手机相应应用，adb界面会显示结果
         * 4.复制
         */
        /**
         *  act=android.intent.action.MAIN
         *  cat=[android.intent.category.LAUNCHER]
         *  flg=0x10200000
         *  cmp=com.taobao.taobao/com.taobao.tao.welcome.Welcome
         *  bnds=[454,733][622,901] (has extras)} from uid 10021
         *  com.taobao.taobao/com.taobao.tao.TBMainActivity
         */
        //包名是这个：com.taobao.taobao
        //检查是否安装淘宝应用
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTaobaoApp = packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mHasTaobaoApp = false;
        }
        LogUtils.d(this, "mHasTaobaoApp ----- " + mHasTaobaoApp);
        //根据这个值去修改UI
        mCopyOrOpenBtn.setText(mHasTaobaoApp ? "打开淘宝领券" : "复制淘口令");
    }


    @Override
    protected void release() {
        if (mTicketPresenter != null) {
            mTicketPresenter.unregisterViewCallBack(this);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
        mBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCopyOrOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制淘口令
                //拿到内容
                String ticketCode = mTicketCode.getText().toString().trim();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //复制到粘贴板
                ClipData clipData = ClipData.newPlainText("tao_bao_ticket_code", ticketCode);
                cm.setPrimaryClip(clipData);
                //判断有没有淘宝
                if (mHasTaobaoApp) {
                    //有就打开淘宝

                    Intent taobaoIntent = new Intent();
                    /*taobaoIntent.setAction("android.intent.action.MAIN");
                    taobaoIntent.addCategory("android.intent.category.LAUNCHER");
                    com.taobao.taobao/com.taobao.tao.TBMainActivity*/
                    ComponentName componentName = new ComponentName("com.taobao.taobao", "com.taobao.tao.TBMainActivity");
                    taobaoIntent.setComponent(componentName);
                    startActivity(taobaoIntent);
                } else {
                    //没有就提示复制成功
                    ToastUtil.showToast("已经复制，粘贴分享，或打开淘宝");
                }

            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {

        if (mCover != null && !TextUtils.isEmpty(cover)) {
            ViewGroup.LayoutParams layoutParams = mCover.getLayoutParams();
            int targetWidth = layoutParams.width / 2;
            LogUtils.d(this, "cover width ----- " + targetWidth);
            String coverPath = UrlUtils.getCoverPath(cover);
            LogUtils.d(this, "coverPath ----- " + coverPath);
            Glide.with(this).load(coverPath).into(mCover);
        }

        if (result != null && result.getData().getTbk_tpwd_create_response() != null) {
            mTicketCode.setText(result.getData().getTbk_tpwd_create_response().getData().getModel());
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEmpty() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }
}
