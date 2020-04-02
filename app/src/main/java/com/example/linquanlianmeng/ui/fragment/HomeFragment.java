package com.example.linquanlianmeng.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.linquanlianmeng.R;
import com.example.linquanlianmeng.base.BaseFragment;
import com.example.linquanlianmeng.model.domain.Categories;
import com.example.linquanlianmeng.presenter.IHomePresenter;
import com.example.linquanlianmeng.ui.activity.IMainActivity;
import com.example.linquanlianmeng.ui.activity.MainActivity;
import com.example.linquanlianmeng.ui.adapter.HomePagerAdapter;
import com.example.linquanlianmeng.utils.LogUtils;
import com.example.linquanlianmeng.utils.PresenterManager;
import com.example.linquanlianmeng.view.IHomeCallBack;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class HomeFragment extends BaseFragment implements IHomeCallBack {

    @BindView(R.id.home_indication)
    public TabLayout mTabLayout;

    @BindView(R.id.home_pager)
    public ViewPager homePager;

    @BindView(R.id.home_search_input_box)
    public View mSearchInputBox;

    private IHomePresenter mHomePresenter;
    private HomePagerAdapter mHomePagerAdapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View rootView) {
        //
        mTabLayout.setupWithViewPager(homePager);
        //给ViewPager设置适配器
        mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        homePager.setAdapter(mHomePagerAdapter);
    }

    @Override
    protected void initPresenter() {
        //创建Persenter
        mHomePresenter = PresenterManager.getInstance().getHomePresenter();
        mHomePresenter.registerViewCallBack(this);
        mSearchInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索界面
                FragmentActivity activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((IMainActivity) activity).switch2Search();
                }
            }
        });
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_home_fragment_layout, container, false);
    }

    @Override
    protected void loadData() {
        //加载数据
        mHomePresenter.getCategories();
    }

    @Override
    public void onCategoriesLoaded(Categories categories) {

        setUpState(State.SUCCESS);
        LogUtils.d(this,"onCategoriesLoaded --------");
        //加载的数据就会从这里回来
        if (mHomePagerAdapter != null) {
//            homePager.setOffscreenPageLimit(categories.getData().size());
            mHomePagerAdapter.setCategories(categories);
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
        setUpState(State.EMPTY);
    }

    @Override
    protected void release() {
        //取消回调注册
        if (mHomePresenter != null) {
            mHomePresenter.unregisterViewCallBack(this);
        }
    }

    @Override
    protected void onRetryClick() {
        //网络错误，点击了重试
        //重新加载分类内容
        if (mHomePresenter != null) {
            mHomePresenter.getCategories();
        }
    }
}
