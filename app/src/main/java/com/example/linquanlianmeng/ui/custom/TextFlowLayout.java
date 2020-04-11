package com.example.linquanlianmeng.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.linquanlianmeng.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextFlowLayout extends ViewGroup {
    private List<String> mTextList = new ArrayList<>();

    public static final float DEFAULT_SPACE = 20;
    private float mItemHorizontalSpace = DEFAULT_SPACE;
    private float mItemVerticalSpace = DEFAULT_SPACE;
    private int mSelfWidth;
    private int mItemHeight;
    private OnFlowTextItemClickListener mItemClickListener = null;

    public int getContentSize() {
        return mTextList.size();
    }

    public float getItemHorizontalSpace() {
        return mItemHorizontalSpace;
    }

    public void setItemHorizontalSpace(float itemHorizontalSpace) {
        mItemHorizontalSpace = itemHorizontalSpace;
    }

    public float getItemVerticalSpace() {
        return mItemVerticalSpace;
    }

    public void setItemVerticalSpace(float itemVerticalSpace) {
        mItemVerticalSpace = itemVerticalSpace;
    }

    //    ==================================================


    public TextFlowLayout(Context context) {
        this(context, null);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //去拿到相关属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowTextStyle);
        mItemHorizontalSpace = typedArray.getDimension(R.styleable.FlowTextStyle_horizontalSpace, DEFAULT_SPACE);
        mItemVerticalSpace = typedArray.getDimension(R.styleable.FlowTextStyle_verticalSpace, DEFAULT_SPACE);
        typedArray.recycle();
//        LogUtils.d(this, "mItemHorizontalSpace -- " + mItemHorizontalSpace);
//        LogUtils.d(this, "mItemVerticalSpace ===== " + mItemVerticalSpace);
    }


    //    ==========================================================

    public void setTextList(List<String> textList) {
        removeAllViews();
        this.mTextList.clear();
        this.mTextList.addAll(textList);
        Collections.reverse(mTextList);

        //遍历内容
        for (String text : mTextList) {
            //添加子view
            //LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, true);
            //等价于
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            item.setText(text);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onFlowItemClick(text);
                    }
                }
            });
            addView(item);
        }
    }

    //这是描述所有行
    private List<List<View>> lines = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 0) {
            return;
        }
        //这是描述单行
        List<View> line = null;
        lines.clear();

        mSelfWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
//        LogUtils.d(this, "mSelfWidth ---------" + mSelfWidth);

        //测量
//        LogUtils.d(this, "onMeasure ---- > " + getChildCount());
        //测量孩子
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = getChildAt(i);
            if (itemView.getVisibility() != VISIBLE) {
                //不需要进行测量
                continue;
            }
            //测量前
//            LogUtils.d(this, "before height --- " + itemView.getMeasuredHeight());
            measureChild(itemView, widthMeasureSpec, heightMeasureSpec);
            //测量后
//            LogUtils.d(this, "after height --- " + itemView.getMeasuredHeight());
            if (line == null) {
                //说明当前行为空，可以添加
                line = createNewLine(itemView);
            } else {
                //判断是否可以继续添加
                if (canBeAdd(itemView, line)) {
                    //可以添加进去
                    line.add(itemView);

                } else {
                    //新创建一行
                    line = createNewLine(itemView);
                }
            }
        }


        mItemHeight = getChildAt(0).getMeasuredHeight();
        int selfHeight = (int) (lines.size() * mItemHeight + mItemVerticalSpace * (lines.size() + 1) + 0.5f);

        //测量自己
        setMeasuredDimension(mSelfWidth, selfHeight);
    }

    private List<View> createNewLine(View itemView) {
        List<View> line = new ArrayList<>();
        line.add(itemView);
        lines.add(line);
        return line;
    }

    /**
     * 判断当前行是否可以再继续添加数据
     *
     * @param itemView
     * @param line
     */
    private boolean canBeAdd(View itemView, List<View> line) {
        //所有已经添加的子view宽度相加 + (line.size() + 1) * mItemHorizontalSpace + itemView.getMeasuredWidth();
        //条件:如果小于/等于当前控件的宽度，则可以添加，否则不能添加

        int totalWidth = itemView.getMeasuredWidth();
        for (View view : line) {
            //叠加所有已经添加控件的宽度
            totalWidth += view.getMeasuredWidth();
        }
        //水平间距的宽度
        totalWidth += mItemHorizontalSpace * (line.size() + 1);
//        LogUtils.d(this, "totalWidth ------ " + totalWidth);

        //条件:如果小于/等于当前控件的宽度，则可以添加，否则不能添加
        return totalWidth <= mSelfWidth;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //摆放孩子
//        LogUtils.d(this, "onLayout ---- > " + getChildCount());

        int topOffset = (int) mItemVerticalSpace;
        for (List<View> views : lines) {
            //views是每一行
            int leftOffset = (int) mItemVerticalSpace;
            for (View view : views) {
                //每一行里的每个item
                view.layout(leftOffset, topOffset, leftOffset + view.getMeasuredWidth(), topOffset + view.getMeasuredHeight());
                //
                leftOffset += view.getMeasuredWidth() + mItemHorizontalSpace;
            }

            topOffset += mItemHeight + mItemVerticalSpace;
        }

    }


    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnFlowTextItemClickListener {
        void onFlowItemClick(String text);
    }
}
