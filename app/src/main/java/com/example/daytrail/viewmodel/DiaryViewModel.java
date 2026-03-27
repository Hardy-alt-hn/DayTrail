package com.example.daytrail.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.daytrail.data.Diary;
import com.example.daytrail.data.Category;
import com.example.daytrail.data.DiaryRepository;
import com.example.daytrail.data.CategoryDao;

import java.util.List;

public class DiaryViewModel extends AndroidViewModel {
    private final DiaryRepository repository;
    private final LiveData<List<Diary>> allDiaries;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<Long> currentCategoryId = new MutableLiveData<>(-1L);
    private final MutableLiveData<List<Diary>> searchedDiaries = new MutableLiveData<>();
    
    private final LiveData<List<Category>> allCategories;
    private Category selectedCategory;

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        repository = new DiaryRepository(application);
        allDiaries = repository.getAllDiaries();
        
        CategoryDao categoryDao = new CategoryDao(application);
        allCategories = categoryDao.getAllCategories();
        selectedCategory = categoryDao.getUncategorizedCategory();
        
        // 初始化显示所有日记
        allDiaries.observeForever(diaries -> 
            searchedDiaries.postValue(diaries));

        searchQuery.observeForever(query -> {
            if (query == null || query.isEmpty()) {
                // 如果没有搜索条件，根据分类过滤
                Long categoryId = currentCategoryId.getValue();
                if (categoryId != null && categoryId != -1) {
                    repository.getDiariesByCategory(categoryId).observeForever(diaries -> 
                        searchedDiaries.postValue(diaries));
                } else {
                    allDiaries.observeForever(diaries -> 
                        searchedDiaries.postValue(diaries));
                }
            } else {
                repository.searchDiaries(query).observeForever(diaries -> 
                    searchedDiaries.postValue(diaries));
            }
        });
        
        currentCategoryId.observeForever(categoryId -> {
            String query = searchQuery.getValue();
            if (query == null || query.isEmpty()) {
                if (categoryId != null && categoryId != -1) {
                    repository.getDiariesByCategory(categoryId).observeForever(diaries -> 
                        searchedDiaries.postValue(diaries));
                } else {
                    allDiaries.observeForever(diaries -> 
                        searchedDiaries.postValue(diaries));
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public LiveData<List<Diary>> getAllDiaries() {
        return allDiaries;
    }

    public LiveData<List<Diary>> getSearchedDiaries() {
        return searchedDiaries;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void insert(Diary diary) {
        repository.insert(diary);
    }

    public void update(Diary diary) {
        repository.update(diary);
    }

    public void delete(Diary diary) {
        repository.delete(diary);
    }

    public Diary getDiaryById(long id) {
        return repository.getDiaryById(id);
    }
    
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }
    
    public void addCategory(String categoryName) {
        Category category = new Category(categoryName);
        repository.addCategory(category);
    }
    
    public void deleteCategory(Category category) {
        repository.deleteCategory(category);
    }
    
    public Category getLatestCategory() {
        List<Category> categories = allCategories.getValue();
        if (categories != null && !categories.isEmpty()) {
            return categories.get(categories.size() - 1);
        }
        return selectedCategory;
    }
    
    public void filterByCategory(long categoryId) {
        currentCategoryId.setValue(categoryId);
    }
    
    public void setSelectedCategory(Category category) {
        this.selectedCategory = category;
    }
    
    public Category getSelectedCategory() {
        return selectedCategory;
    }
}
