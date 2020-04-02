package com.example.linquanlianmeng.ui.fragment;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.linquanlianmeng.R;
import com.example.linquanlianmeng.base.BaseFragment;
import com.example.linquanlianmeng.model.domain.IBaseInfo;
import com.example.linquanlianmeng.model.domain.OnSellContent;
import com.example.linquanlianmeng.presenter.IOnSellPagePresenter;
import com.example.linquanlianmeng.ui.adapter.OnSellContentAdapter;
import com.example.linquanlianmeng.utils.PresenterManager;
import com.example.linquanlianmeng.utils.SizeUtils;
import com.example.linquanlianmeng.utils.TicketUtil;
import com.example.linquanlianmeng.utils.ToastUtil;
import com.example.linquanlianmeng.view.IOnSellPageCallBack;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class OnSellFragment extends BaseFragment implements IOnSellPageCallBack, OnSellContentAdapter.OnSellPageItemClickListener {

    private IOnSellPagePresenter mOnSellPagePresenter;
    private static final int DEFAULT_SPAN_COUNT = 2;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView mContentRv;

    @BindView(R.id.on_sell_refresh_layout)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;


    @BindView(R.id.fragment_bar_title_tv)
    public TextView barTitleTv;

    private OnSellContentAdapter mOnSellContentAdapter;

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mOnSellPagePresenter = PresenterManager.getInstance().getOnSellPagePresenter();
        mOnSellPagePresenter.registerViewCallBack(this);
        mOnSellPagePresenter.getOnSellContent();
    }

    @Override
    protected void release() {
        super.release();
        if (mOnSellPagePresenter != null) {
            mOnSellPagePresenter.unregisterViewCallBack(this);
        }
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_on_sell;
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多的内容
                if (mOnSellPagePresenter != null) {
                    mOnSellPagePresenter.loaderMore();
                }
            }
        });

        mOnSellContentAdapter.setOnSellPageItemClickListener(this);
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout,container,false);
    }

    @Override
    protected void initView(View rootView) {
        barTitleTv.setText("特惠宝贝");


        mOnSellContentAdapter = new OnSellContentAdapter();
        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT);
        mContentRv.setLayoutManager(gridLayoutManager);
        mContentRv.setAdapter(mOnSellContentAdapter);
        mContentRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.left = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.right = SizeUtils.dip2px(getContext(), 2.5f);

            }
        });

        mTwinklingRefreshLayout.setEnableLoadmore(true);
        mTwinklingRefreshLayout.setEnableRefresh(false);
        mTwinklingRefreshLayout.setEnableOverScroll(true);
    }

    @Override
    protected void onRetryClick() {
        super.onRetryClick();
        mOnSellPagePresenter.getOnSellContent();
    }

    @Override
    public void onContentLoadedSuccess(OnSellContent result) {
        //数据回来了
        setUpState(State.SUCCESS);
        // TODO: 2020/3/23 更新UI
        mOnSellContentAdapter.setData(result);
    }

    @Override
    public void onMoreLoaded(OnSellContent moreResult) {
        mTwinklingRefreshLayout.finishLoadmore();
        //添加内容到适配器里面
        mOnSellContentAdapter.onMoreLoaded(moreResult);
        int size = moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        ToastUtil.showToast("加载了" + size + "个宝贝");
    }

    @Override
    public void onMoreLoadedError() {
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtil.showToast("网络异常，请稍后重试.");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtil.showToast("没有更多内容...");
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onSellItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(),item);
    }
}
