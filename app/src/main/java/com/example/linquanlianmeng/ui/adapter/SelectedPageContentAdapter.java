package com.example.linquanlianmeng.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.linquanlianmeng.R;
import com.example.linquanlianmeng.model.domain.IBaseInfo;
import com.example.linquanlianmeng.model.domain.SelectedContent;
import com.example.linquanlianmeng.utils.Constans;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedPageContentAdapter extends RecyclerView.Adapter<SelectedPageContentAdapter.InnerHolder> {

    private List<SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mData =
            new ArrayList<>();
    private OnSelectedPageContentItemClickListener mContentItemClickListener = null;


    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean itemData = mData.get(position);
        holder.setData(itemData);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentItemClickListener != null) {
                    mContentItemClickListener.onContentItemClick(itemData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(SelectedContent content) {
        if (content.getCode() == Constans.SUCCESS_CODE) {
            List<SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> map_data = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
            this.mData.clear();
            this.mData.addAll(map_data);
            notifyDataSetChanged();
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.selected_cover)
        public ImageView cover;

        @BindView(R.id.selected_off_prise)
        public TextView offPriseTv;

        @BindView(R.id.selected_title)
        public TextView title;

        @BindView(R.id.selected_buy_btn)
        public TextView buyBtn;

        @BindView(R.id.selected_original_prise)
        public TextView originalPriseTv;


        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean itemData) {
            title.setText(itemData.getTitle());
            String pict_url = "";
            if (itemData.getPict_url() == null) {
                if (itemData.getWhite_image() == null) {
                    pict_url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fnimg.ws.126.net%2F%3Furl%3Dhttp%253A%252F%252Fdingyue.ws.126.net%252F2021%252F0513%252F65283c7aj00qt0zhw000cc0007i007ic.jpg%26thumbnail%3D650x2147483647%26quality%3D80%26type%3Djpg&refer=http%3A%2F%2Fnimg.ws.126.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1625148343&t=0acd4c462bd5a44fd67fd341e4fffe84";
                } else {
                    pict_url = itemData.getWhite_image() + "_270x270xzq90.jpg_.webp";
                }
            } else {
                pict_url = "https://img.alicdn.com/bao/uploaded" + itemData.getPict_url() + "_270x270xzq90.jpg_.webp";
            }
//            LogUtils.d(this, "url ------- " + itemData.getPict_url());
            Glide.with(itemView.getContext()).load(pict_url).into(cover);
            if (TextUtils.isEmpty(itemData.getCoupon_click_url())) {
                originalPriseTv.setText("晚啦，没有优惠券了");
                buyBtn.setVisibility(View.GONE);
            } else {
                originalPriseTv.setText("原价: " + itemData.getZk_final_price());
                buyBtn.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(itemData.getCoupon_info())) {
                offPriseTv.setVisibility(View.GONE);
            } else {
                offPriseTv.setVisibility(View.VISIBLE);
                offPriseTv.setText(itemData.getCoupon_info());
            }

        }
    }

    public void setOnSelectedPageContentItemClickListener(OnSelectedPageContentItemClickListener listener) {
        this.mContentItemClickListener = listener;
    }

    public interface OnSelectedPageContentItemClickListener {
        void onContentItemClick(IBaseInfo item);
    }
}
