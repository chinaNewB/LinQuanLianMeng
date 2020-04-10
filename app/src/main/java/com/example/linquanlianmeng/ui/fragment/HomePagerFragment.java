package com.example.linquanlianmeng.ui.fragment;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.linquanlianmeng.R;
import com.example.linquanlianmeng.base.BaseFragment;
import com.example.linquanlianmeng.model.domain.Categories;
import com.example.linquanlianmeng.model.domain.HomePagerContent;
import com.example.linquanlianmeng.model.domain.IBaseInfo;
import com.example.linquanlianmeng.presenter.ICategoryPagerPresenter;
import com.example.linquanlianmeng.ui.adapter.HomePagerContentAdapter;
import com.example.linquanlianmeng.ui.adapter.LooperPagerAdapter;
import com.example.linquanlianmeng.ui.custom.AutoLoopViewPager;
import com.example.linquanlianmeng.utils.Constans;
import com.example.linquanlianmeng.utils.LogUtils;
import com.example.linquanlianmeng.utils.PresenterManager;
import com.example.linquanlianmeng.utils.SizeUtils;
import com.example.linquanlianmeng.utils.TicketUtil;
import com.example.linquanlianmeng.utils.ToastUtil;
import com.example.linquanlianmeng.view.ICategoryPagerCallBack;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.views.TbNestedScrollView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallBack, HomePagerContentAdapter.OnListItemClickListener, LooperPagerAdapter.OnLooperPageItemClickListener {

    private ICategoryPagerPresenter mCategoryPagePresenter;
    private int mMaterialId;
    private HomePagerContentAdapter mContentAdapter;
    private LooperPagerAdapter mLooperPagerAdapter;

    public static HomePagerFragment newInstance(Categories.DataBean category) {
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        //
        Bundle bundle = new Bundle();
        bundle.putString(Constans.KEY_HOME_PAGER_TITLE, category.getTitle());
        bundle.putInt(Constans.KEY_HOME_PAGER_MATETIAL_ID, category.getId());
        homePagerFragment.setArguments(bundle);

        return homePagerFragment;
    }

    @BindView(R.id.home_pager_content_list)
    public RecyclerView mContentList;

    @BindView(R.id.looper_pager)
    public AutoLoopViewPager looperPager;

    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitleTv;

    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;

    @BindView(R.id.home_pager_nested_scroller)
    public TbNestedScrollView homePagerNestedView;

    @BindView(R.id.home_pager_header_container)
    public LinearLayout homePagerContainer;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void onResume() {
        super.onResume();
        //可见的时候调用开始轮播
        looperPager.startLoop();
        //        LogUtils.d(this, "onResume...");
    }

    @Override
    public void onPause() {
        super.onPause();
        looperPager.stopLoop();
        //        LogUtils.d(this, "onPause...");
    }

    @Override
    protected void onRetryClick() {
        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.getContentByCategoryId(mMaterialId);
        }
    }

    @Override
    protected void initListener() {
        mContentAdapter.setOnListItemClickListener(this);
        mLooperPagerAdapter.setOnLooperPageItemClickListener(this);

        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homePagerContainer == null) {
                    return;
                }
                int headerHeight = homePagerContainer.getMeasuredHeight();
                //                LogUtils.d(HomePagerFragment.this,"headerHeight..."+headerHeight);
                homePagerNestedView.setHeaderHeight(headerHeight);


                int measuredHeight = homePagerParent.getMeasuredHeight();
                //                LogUtils.d(HomePagerFragment.this,"measuredHeight..."+measuredHeight);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentList.getLayoutParams();
                layoutParams.height = measuredHeight;
                mContentList.setLayoutParams(layoutParams);
                if (measuredHeight != 0) {
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mLooperPagerAdapter.getDataSize() == 0) {
                    return;
                }
                int targetPosition = position % mLooperPagerAdapter.getDataSize();
                //切换指示器
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {

                // TODO: 2020/3/6 加载更多内容
                if (mCategoryPagePresenter != null) {
                    mCategoryPagePresenter.loadMore(mMaterialId);
                }
            }
        });
    }

    /**
     * 切换指示器
     *
     * @param targetPosition
     */
    private void updateLooperIndicator(int targetPosition) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);

            }
        }
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        //设置布局管理器
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 1.5f);
            }
        });
        //创建适配器
        mContentAdapter = new HomePagerContentAdapter();
        //设置适配器
        mContentList.setAdapter(mContentAdapter);

        //创建轮播图适配器
        mLooperPagerAdapter = new LooperPagerAdapter();
        //设置适配器
        looperPager.setAdapter(mLooperPagerAdapter);

        //设置Refresh相关属性
        mTwinklingRefreshLayout.setEnableRefresh(false);
        mTwinklingRefreshLayout.setEnableLoadmore(true);
    }

    @Override
    protected void initPresenter() {
        mCategoryPagePresenter = PresenterManager.getInstance().getCategoryPagePresenter();
        mCategoryPagePresenter.registerViewCallBack(this);
    }

    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        String title = arguments.getString(Constans.KEY_HOME_PAGER_TITLE);
        mMaterialId = arguments.getInt(Constans.KEY_HOME_PAGER_MATETIAL_ID);
        // TODO: 2020/3/4 加载数据

        // LogUtils.d(this, "title ----- " + title);
        // LogUtils.d(this, "materialId ----- " + mMaterialId);

        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.getContentByCategoryId(mMaterialId);
        }

        if (currentCategoryTitleTv != null) {
            currentCategoryTitleTv.setText(title);
        }
    }

    @Override
    public void onContentLoaded(List<HomePagerContent.DataBean> contents) {
        //数据列表加载到了
        // TODO: 2020/3/4  更新UI
        mContentAdapter.setData(contents);
        setUpState(State.SUCCESS);
    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onError() {
        //网络错误
        setUpState(State.ERROR);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onLoaderMoreError() {
        ToastUtil.showToast("网络异常，请稍后重试");
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtil.showToast("没有更多数据");
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreLoaded(List<HomePagerContent.DataBean> contents) {
        //添加到适配器数据的底部
        mContentAdapter.addData(contents);
        if (mTwinklingRefreshLayout != null) {
            mTwinklingRefreshLayout.finishLoadmore();
        }
        ToastUtil.showToast("加载了" + contents.size() + "条记录");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLooperListLoaded(List<HomePagerContent.DataBean> contents) {
//        LogUtils.d(this, "looper size --- " + contents.size());
        mLooperPagerAdapter.setData(contents);

        //中间点%数据的size不一定为0，所以显示的就不是第一个，
        //处理一下
        int dx = (Integer.MAX_VALUE / 2) % contents.size();
        int targetCenterPosition = (Integer.MAX_VALUE / 2) - dx;

        //设置到中间点
        looperPager.setCurrentItem(targetCenterPosition);
        looperPointContainer.removeAllViews();


        //添加点
        for (int i = 0; i < contents.size(); i++) {
            View point = new View(getContext());
            int size = SizeUtils.dip2px(getContext(), 8);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.leftMargin = SizeUtils.dip2px(getContext(), 5);
            params.rightMargin = SizeUtils.dip2px(getContext(), 5);
            point.setLayoutParams(params);
            if (i == 0) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
            looperPointContainer.addView(point);
        }
    }

    @Override
    protected void release() {
        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.unregisterViewCallBack(this);
        }
    }

    @Override
    public void onItemClick(IBaseInfo item) {
        //列表内容被点击了
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(), item);
    }

    @Override
    public void onLooperItemClick(IBaseInfo item) {
        //轮播图内容被点击了
        handleItemClick(item);
    }
}
