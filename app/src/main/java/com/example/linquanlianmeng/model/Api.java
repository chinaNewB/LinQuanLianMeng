package com.example.linquanlianmeng.model;

import com.example.linquanlianmeng.model.domain.Categories;
import com.example.linquanlianmeng.model.domain.HomePagerContent;
import com.example.linquanlianmeng.model.domain.OnSellContent;
import com.example.linquanlianmeng.model.domain.SearchRecommend;
import com.example.linquanlianmeng.model.domain.SearchResult;
import com.example.linquanlianmeng.model.domain.SelectedContent;
import com.example.linquanlianmeng.model.domain.SelectedPageCategory;
import com.example.linquanlianmeng.model.domain.TicketParams;
import com.example.linquanlianmeng.model.domain.TicketResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    @GET("discovery/categories")
    Call<Categories> getCategories();

    @GET
    Call<HomePagerContent> getHomePagerContent(@Url String url);

    @POST("tpwd")
    Call<TicketResult> getTicket(@Body TicketParams ticketParams);

    @GET("recommend/categories")
    Call<SelectedPageCategory> getSelectedPageCategories();

    @GET
    Call<SelectedContent> getSelectedPageContent(@Url String url);

    @GET
    Call<OnSellContent> getOnSellPageContent(@Url String url);

    @GET("search/recommend")
    Call<SearchRecommend> getRecommendWords();

    @GET("search")
    Call<SearchResult> doSearch(@Query("page") int page,@Query("keyword") String keyword);
}
