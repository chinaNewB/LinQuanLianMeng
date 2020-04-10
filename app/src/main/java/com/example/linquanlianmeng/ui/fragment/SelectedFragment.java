package com.example.linquanlianmeng.ui.fragment;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.linquanlianmeng.R;
import com.example.linquanlianmeng.base.BaseFragment;
import com.example.linquanlianmeng.model.domain.IBaseInfo;
import com.example.linquanlianmeng.model.domain.SelectedContent;
import com.example.linquanlianmeng.model.domain.SelectedPageCategory;
import com.example.linquanlianmeng.presenter.ISelectedPagePresenter;
import com.example.linquanlianmeng.ui.adapter.SelectedPageContentAdapter;
import com.example.linquanlianmeng.ui.adapter.SelectedPageLeftAdapter;
import com.example.linquanlianmeng.utils.PresenterManager;
import com.example.linquanlianmeng.utils.SizeUtils;
import com.example.linquanlianmeng.utils.TicketUtil;
import com.example.linquanlianmeng.view.ISelectedPageCallBack;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class SelectedFragment extends BaseFragment
        implements ISelectedPageCallBack,
        SelectedPageLeftAdapter.OnLeftItemClickListener,
        SelectedPageContentAdapter.OnSelectedPageContentItemClickListener {

    private ISelectedPagePresenter mSelectedPagePresenter;

    @BindView(R.id.left_category_list)
    public RecyclerView leftCategoryList;

    @BindView(R.id.right_content_list)
    public RecyclerView rightContentList;

    @BindView(R.id.fragment_bar_title_tv)
    public TextView barTitleTv;


    private SelectedPageLeftAdapter mLeftAdapter;
    private SelectedPageContentAdapter mRightAdapter;


    @Override
    protected void initPresenter() {
        super.initPresenter();
        mSelectedPagePresenter = PresenterManager.getInstance().getSelectedPagePresenter();
        mSelectedPagePresenter.registerViewCallBack(this);
        mSelectedPagePresenter.getCategories();
    }

    @Override
    protected void release() {
        super.release();
        if (mSelectedPagePresenter != null) {
            mSelectedPagePresenter.unregisterViewCallBack(this);
        }
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_selected;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout,container,false);
    }

    @Override
    protected void initView(View rootView) {

        barTitleTv.setText("精选宝贝");

        setUpState(State.SUCCESS);
        leftCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLeftAdapter = new SelectedPageLeftAdapter();
        leftCategoryList.setAdapter(mLeftAdapter);

        rightContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRightAdapter = new SelectedPageContentAdapter();
        rightContentList.setAdapter(mRightAdapter);
        rightContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int topAndBottom = SizeUtils.dip2px(getContext(), 4);
                int leftAndRight = SizeUtils.dip2px(getContext(), 6);
                outRect.top = topAndBottom;
                outRect.left = leftAndRight;
                outRect.right = leftAndRight;
                outRect.bottom = topAndBottom;
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mLeftAdapter.setOnLeftItemClickListener(this);
        mRightAdapter.setOnSelectedPageContentItemClickListener(this);
    }

    @Override
    public void onCategoriesLoaded(SelectedPageCategory categories) {
        setUpState(State.SUCCESS);
        mLeftAdapter.setData(categories);
        //分类内容
//        LogUtils.d(this, "onCategoriesLoaded ------- " + categories);
        // 更新UI
        //根据当前选中的分类，获收分类详情内谷
        List<SelectedPageCategory.DataBean> data = categories.getData();
        mSelectedPagePresenter.getContentByCategory(data.get(0));

    }

    @Override
    public void onContentLoaded(SelectedContent content) {
//        LogUtils.d(this, "onContentLoaded ------- " + content);
        mRightAdapter.setData(content);
        rightContentList.scrollToPosition(0);
    }

    @Override
    protected void onRetryClick() {
        //重试
        if (mSelectedPagePresenter != null) {
            mSelectedPagePresenter.reloadContent();
        }
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

    }

    @Override
    public void onLeftItemClick(SelectedPageCategory.DataBean item) {
        //左边的分类点击了
//        LogUtils.d(this, "onLeftItemClick ---- " + item.getFavorites_title());
        mSelectedPagePresenter.getContentByCategory(item);
    }

    @Override
    public void onContentItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(),item);
    }
}
