package com.example.linquanlianmeng.ui.activity;

import android.os.Bundle;

import com.example.linquanlianmeng.R;
import com.example.linquanlianmeng.ui.custom.TextFlowLayout;
import com.example.linquanlianmeng.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {


    @BindView(R.id.flow_text)
    public TextFlowLayout mTextFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        ButterKnife.bind(this);
        List<String> testList = new ArrayList<>();
        testList.add("电脑");
        testList.add("笔记本");
        testList.add("android");
        testList.add("java web");
        testList.add("手机");
        testList.add("毛衣");
        testList.add("机械键盘");
        testList.add("运动鞋");
        testList.add("肥宅快乐水");
        testList.add("滑板鞋");
        testList.add("化妆品");
        testList.add("Vue.js");
        mTextFlowLayout.setTextList(testList);
        mTextFlowLayout.setOnFlowTextItemClickListener(new TextFlowLayout.OnFlowTextItemClickListener() {
            @Override
            public void onFlowItemClick(String text) {
                ToastUtil.showToast(text);
            }
        });
    }
}
