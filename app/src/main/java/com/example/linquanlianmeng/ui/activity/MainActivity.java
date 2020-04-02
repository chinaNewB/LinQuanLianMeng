package com.example.linquanlianmeng.ui.activity;

import android.view.MenuItem;

import com.example.linquanlianmeng.R;
import com.example.linquanlianmeng.base.BaseActivity;
import com.example.linquanlianmeng.base.BaseFragment;
import com.example.linquanlianmeng.ui.fragment.HomeFragment;
import com.example.linquanlianmeng.ui.fragment.OnSellFragment;
import com.example.linquanlianmeng.ui.fragment.SearchFragment;
import com.example.linquanlianmeng.ui.fragment.SelectedFragment;
import com.example.linquanlianmeng.utils.LogUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;

public class MainActivity extends BaseActivity implements IMainActivity {


    @BindView(R.id.main_navgation_bar)
    public BottomNavigationView mNavigationView;

    private HomeFragment mHomeFragment;
    private OnSellFragment mRedPacketFragment;
    private SelectedFragment mSelectedFragment;
    private SearchFragment mSearchFragment;
    private FragmentManager mFm;

    @Override
    protected void initPresenter() {

    }

    /**
     * 跳转到搜索界面
     */
    public void switch2Search() {
        //切换导航栏的选中项
        mNavigationView.setSelectedItemId(R.id.search);

    }

    @Override
    protected void initEvent() {
        initListener();
    }

    @Override
    protected void initView() {
        initFraments();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }


    private void initFraments() {
        mHomeFragment = new HomeFragment();
        mRedPacketFragment = new OnSellFragment();
        mSelectedFragment = new SelectedFragment();
        mSearchFragment = new SearchFragment();
        mFm = getSupportFragmentManager();
        switchFragment(mHomeFragment);

    }

    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    LogUtils.d(this, "切换到首页");
                    // TODO: 2020/3/3
                    switchFragment(mHomeFragment);
                } else if (item.getItemId() == R.id.selected) {
                    LogUtils.d(this, "切换到精选");
                    switchFragment(mSelectedFragment);

                } else if (item.getItemId() == R.id.red_packet) {
                    LogUtils.d(this, "切换到特惠");
                    switchFragment(mRedPacketFragment);

                } else if (item.getItemId() == R.id.search) {
                    LogUtils.d(this, "切换到搜索");
                    switchFragment(mSearchFragment);

                }
                return true;
            }
        });
    }

    /**
     * 上一次显示的fragment
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment) {
        //如果上一个fragment跟当前要切换的fragment是同一个，那么不需要切换
        if (lastOneFragment == targetFragment) {
            return;
        }
        //修改成add和hide的方式來控制Fragment的切换
        FragmentTransaction transaction = mFm.beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.add(R.id.main_page_container, targetFragment);
        } else {
            transaction.show(targetFragment);
        }

        if (lastOneFragment != null) {
            transaction.hide(lastOneFragment);
        }

        lastOneFragment = targetFragment;
        //        transaction.replace(R.id.main_page_container, targetFragment);
        transaction.commit();
    }

}
