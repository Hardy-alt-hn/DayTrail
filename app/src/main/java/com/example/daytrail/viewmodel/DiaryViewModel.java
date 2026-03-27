package com.example.daytrail.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.daytrail.data.Diary;
import com.example.daytrail.data.DiaryRepository;

import java.util.List;

public class DiaryViewModel extends AndroidViewModel {
    private final DiaryRepository repository;
    private final LiveData<List<Diary>> allDiaries;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        repository = new DiaryRepository(application);
        allDiaries = repository.getAllDiaries();
        searchQuery.setValue("");
    }

    public LiveData<List<Diary>> getAllDiaries() {
        return allDiaries;
    }

    public LiveData<List<Diary>> getDisplayedDiaries() {
        return Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.isEmpty()) {
                return allDiaries;
            } else {
                return repository.searchDiaries(query);
            }
        });
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

    public LiveData<Diary> getDiaryById(long id) {
        return repository.getDiaryById(id);
    }

    public void refreshData() {
        repository.refreshAllDiaries();
    }
}