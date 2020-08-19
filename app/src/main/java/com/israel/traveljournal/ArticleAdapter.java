package com.israel.traveljournal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {
    ArrayList<Article> articleList = new ArrayList<>();
    Context mContext;

    public ArticleAdapter(Context context) {
        mContext = context;
    }

    public ArticleAdapter(ArrayList<Article> articleList, Context context) {
        this.articleList = articleList;
        mContext = context;
    }

    public void setArticle(Article article){
        articleList.add(article);
        notifyDataSetChanged();
    }

    public void editArticle(Article article){
        for (Article a : articleList) {
            if (a.getId().equals(article.getId())){
                int index = articleList.indexOf(a);
                articleList.set(index, article);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextViewName;
        TextView mTextViewDescription;
        Article mArticle;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_article);
            mTextViewName = itemView.findViewById(R.id.tv_article_name);
            mTextViewDescription = itemView.findViewById(R.id.tv_article_description);
        }

        public void bind(Article article){
            mArticle = article;
            mTextViewName.setText(mArticle.getName());
            mTextViewDescription.setText(mArticle.getDescription());
            Glide.with(mContext)
                    .load(mArticle.getImageUrl())
                    .into(mImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ArticleActivity.class);
                    intent.putExtra("article", mArticle);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
