package com.example.linquanlianmeng.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.linquanlianmeng.R;
import com.example.linquanlianmeng.base.BaseFragment;
import com.example.linquanlianmeng.model.domain.Histories;
import com.example.linquanlianmeng.model.domain.IBaseInfo;
import com.example.linquanlianmeng.model.domain.SearchRecommend;
import com.example.linquanlianmeng.model.domain.SearchResult;
import com.example.linquanlianmeng.presenter.ISearchPresenter;
import com.example.linquanlianmeng.ui.adapter.HomePagerContentAdapter;
import com.example.linquanlianmeng.ui.custom.TextFlowLayout;
import com.example.linquanlianmeng.utils.KeyboardUtil;
import com.example.linquanlianmeng.utils.LogUtils;
import com.example.linquanlianmeng.utils.PresenterManager;
import com.example.linquanlianmeng.utils.SizeUtils;
import com.example.linquanlianmeng.utils.TicketUtil;
import com.example.linquanlianmeng.utils.ToastUtil;
import com.example.linquanlianmeng.view.ISerchPageCallBack;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISerchPageCallBack, TextFlowLayout.OnFlowTextItemClickListener {

    private ISearchPresenter mSearchPresenter;

    @BindView(R.id.search_history_view)
    public TextFlowLayout mHistoriesView;

    @BindView(R.id.search_recommend_container)
    public View mRecommendContainer;

    @BindView(R.id.search_history_container)
    public View mHistoriesContainer;

    @BindView(R.id.search_history_delete)
    public View mHistoriesDelete;

    @BindView(R.id.search_recommend_view)
    public TextFlowLayout mRecommendView;

    @BindView(R.id.search_result_list)
    public RecyclerView mSearchList;

    @BindView(R.id.search_result_container)
    public TwinklingRefreshLayout mRefreshContainer;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;

    @BindView(R.id.search_clean_btn)
    public ImageView mCleanInputBtn;

    @BindView(R.id.search_input_box)
    public EditText mSearchInputBox;


    private HomePagerContentAdapter mSearchResultAdapter;

    @Override
    protected void initPresenter() {
        mSearchPresenter = PresenterManager.getInstance().getSearchPresenter();
        mSearchPresenter.registerViewCallBack(this);
        //获取搜索推荐词
        mSearchPresenter.getRecommendWords();
        //        mSearchPresenter.doSearch("毛衣");
        mSearchPresenter.getHistories();
    }

    @Override
    protected void initListener() {
        mHistoriesView.setOnFlowTextItemClickListener(this);
        mRecommendView.setOnFlowTextItemClickListener(this);

        //发起搜索
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果有内容搜索
                //如果输入框没有内容取消
                if (hasInput(false)) {
                    //发起搜索
                    if (mSearchPresenter != null) {
//                        mSearchPresenter.doSearch(mSearchInputBox.getText().toString().trim());
                        toSearch(mSearchInputBox.getText().toString().trim());
//                        KeyboardUtil.hide(getContext(), v);
                    }
                } else {
                    //隐藏键盘
                    KeyboardUtil.hide(getContext(), v);
                }
            }
        });
        //清除输入框里的内容
        mCleanInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInputBox.setText("");
                //回到历史记录界面
                switch2HistoryPage();
            }
        });
        //监听输入框的内容变化
        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //变化的时候通知
                //如果长度不为0，那么显示删除按钮
                //否则隐藏删除按钮
                mCleanInputBtn.setVisibility(hasInput(true) ? View.VISIBLE : View.GONE);
                mSearchBtn.setText(hasInput(false) ? "搜索" : "取消");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mSearchPresenter != null) {
                    //判断拿到的内容是否为空
                    String keyword = v.getText().toString().trim();
                    if (TextUtils.isEmpty(keyword)) {
                        return false;
                    }
                    //发起搜索
                    toSearch(keyword);
                    //                    mSearchPresenter.doSearch(keyword);

                }
                return false;
            }
        });


        mHistoriesDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchPresenter.delHistories();
            }
        });

        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多内容
                if (mSearchPresenter != null) {
                    mSearchPresenter.loaderMore();
                }
            }
        });

        mSearchResultAdapter.setOnListItemClickListener(new HomePagerContentAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(IBaseInfo item) {
                //搜索列表内容被点击了
                TicketUtil.toTicketPage(getContext(), item);
            }
        });
    }

    /**
     * 切换到历史和推荐界面
     */
    private void switch2HistoryPage() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }

        if (mRecommendView.getContentSize() != 0) {
            mRecommendContainer.setVisibility(View.VISIBLE);
        } else {
            mRecommendContainer.setVisibility(View.GONE);
        }

        //内容要隐藏
        mRefreshContainer.setVisibility(View.GONE);
    }

    private boolean hasInput(boolean containSpace) {
        if (containSpace) {
            return mSearchInputBox.getText().toString().trim().length() > 0;
        } else {
            return mSearchInputBox.getText().toString().length() > 0;
        }
    }

    @Override
    protected void onRetryClick() {
        //重新加载内容
        if (mSearchPresenter != null) {
            mSearchPresenter.research();
        }
    }

    @Override
    protected void release() {
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallBack(this);
        }
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout, container, false);
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        //设置布局管理器
        mSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置适配器
        mSearchResultAdapter = new HomePagerContentAdapter();
        mSearchList.setAdapter(mSearchResultAdapter);
        //设置刷新控件
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableOverScroll(true);

        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 1.5f);
            }
        });
    }

    @Override
    public void onHistoriesLoaded(Histories histories) {
        LogUtils.d(SearchFragment.this, "histories ------ " + histories);
        if (histories == null || histories.getHistories().size() == 0) {
            mHistoriesContainer.setVisibility(View.GONE);
        } else {
            mHistoriesContainer.setVisibility(View.VISIBLE);
            mHistoriesView.setTextList(histories.getHistories());
        }
    }

    @Override
    public void onHistoriesDeleted() {
        //更新历史记录
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setUpState(State.SUCCESS);
        //        LogUtils.d(SearchFragment.this, "result ------ " + result);
        //隐藏掉历史记录和推荐
        mRecommendContainer.setVisibility(View.GONE);
        mHistoriesContainer.setVisibility(View.GONE);
        //显示搜索界面
        mRefreshContainer.setVisibility(View.VISIBLE);
        //设置数据
        try {
            mSearchResultAdapter.setData(result.getData()
                    .getTbk_dg_material_optional_response()
                    .getResult_list()
                    .getMap_data());
        } catch (Exception e) {
            e.printStackTrace();
            //切换到搜索内容为空
            setUpState(State.EMPTY);
        }
    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        mRefreshContainer.finishLoadmore();
        //加载到更多结果
        //拿到结果，添加到适配器的尾部
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> moreData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchResultAdapter.addData(moreData);
        //提示用户加载到的内容
        ToastUtil.showToast("加载到了" + moreData.size() + "个宝贝");
    }

    @Override
    public void onMoreLoadedError() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("网络异常，请稍后重试...");
    }

    @Override
    public void onMoreLoadedEmpty() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("没有宝贝了");
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataBean> recommendWords) {
        LogUtils.d(SearchFragment.this, "recommendWords ------ " + recommendWords);
        List<String> recommendKeyWords = new ArrayList<>();
        for (SearchRecommend.DataBean item : recommendWords) {
            recommendKeyWords.add(item.getKeyword());
        }

        if (recommendWords == null || recommendWords.size() == 0) {
            mRecommendContainer.setVisibility(View.GONE);
        } else {
            mRecommendView.setTextList(recommendKeyWords);
            mRecommendContainer.setVisibility(View.VISIBLE);
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
    public void onFlowItemClick(String text) {
        //发起搜索
        toSearch(text);
    }

    private void toSearch(String text) {
        if (mSearchPresenter != null) {
            mSearchList.scrollToPosition(0);
            KeyboardUtil.hide(getContext(),mSearchBtn);
            mSearchInputBox.setText(text);
            mSearchInputBox.setFocusable(true);
            mSearchInputBox.requestFocus();
            mSearchInputBox.setSelection(text.length(),text.length());
            mSearchPresenter.doSearch(text);
        }
    }
}
