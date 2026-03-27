package com.example.daytrail.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.daytrail.R;
import com.example.daytrail.data.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter {
    private final Context context;
    private final List<Category> categories;
    private Category selectedCategory;
    private OnCategoryClickListener listener;
    private OnCategoryLongClickListener longClickListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    
    public interface OnCategoryLongClickListener {
        void onCategoryLongClick(Category category);
    }

    public CategoryAdapter(Context context, Category selectedCategory) {
        this.context = context;
        this.categories = new ArrayList<>();
        this.selectedCategory = selectedCategory;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }
    
    public void setOnCategoryLongClickListener(OnCategoryLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void addCategory(List<com.example.daytrail.data.Category> newCategories) {
        if (newCategories != null) {
            categories.clear();
            categories.addAll(newCategories);
            
            // 在主线程中更新 UI
            Activity activity = (Activity) context;
            activity.runOnUiThread(this::notifyDataSetChanged);
        }
    }

    public void updateCategories(List<com.example.daytrail.data.Category> newCategories) {
        if (newCategories != null) {
            categories.clear();
            categories.addAll(newCategories);
            
            // 在主线程中更新 UI
            Activity activity = (Activity) context;
            activity.runOnUiThread(this::notifyDataSetChanged);
        }
    }

    private void notifyDataSetChanged() {
        // 重新构建 UI
        Activity activity = (Activity) context;
        ViewGroup categoryContainer = activity.findViewById(R.id.categoryContainer);
        if (categoryContainer == null) return;

        categoryContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(context);
        
        // 先添加所有分类标签
        for (Category category : categories) {
            View categoryView = inflater.inflate(R.layout.item_category, categoryContainer, false);
            TextView categoryNameText = categoryView.findViewById(R.id.categoryNameText);
            ImageButton deleteButton = categoryView.findViewById(R.id.deleteCategoryButton);
            
            categoryNameText.setText(category.getName());

            if (selectedCategory != null && selectedCategory.getId() == category.getId()) {
                categoryNameText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.selected_category_background));
                categoryNameText.setTextColor(ContextCompat.getColor(context, R.color.selected_category_text));
            } else {
                categoryNameText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.default_category_background));
                categoryNameText.setTextColor(ContextCompat.getColor(context, R.color.default_category_text));
            }

            // 点击分类标签进行筛选
            categoryNameText.setOnClickListener(v -> {
                selectedCategory = category;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
            
            // 显示删除按钮（除了"Uncategorized"）
            if (!"Uncategorized".equals(category.getName())) {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(v -> {
                    if (longClickListener != null) {
                        longClickListener.onCategoryLongClick(category);
                    }
                });
            }

            categoryContainer.addView(categoryView);
        }
        
        // 最后添加添加和删除按钮（在所有分类的右边）
        View addDeleteButtons = inflater.inflate(R.layout.item_add_category, categoryContainer, false);
        ImageButton addButton = addDeleteButtons.findViewById(R.id.addCategoryButton);
        ImageButton deleteAllButton = addDeleteButtons.findViewById(R.id.deleteCategoryButton);
        
        addButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(null); // null 表示点击了添加按钮
            }
        });
        
        // 删除按钮暂时隐藏
        deleteAllButton.setVisibility(View.GONE);
        
        categoryContainer.addView(addDeleteButtons);
    }
}
