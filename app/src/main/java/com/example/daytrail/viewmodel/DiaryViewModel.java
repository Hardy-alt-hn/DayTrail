package com.example.daytrail.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.daytrail.data.Diary;
import com.example.daytrail.data.DiaryRepository;

import java.util.List;

public class DiaryViewModel extends AndroidViewModel {
    private final DiaryRepository repository;
    private final LiveData<List<Diary>> allDiaries;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private LiveData<List<Diary>> searchedDiaries;

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        repository = new DiaryRepository(application);
        allDiaries = repository.getAllDiaries();
        searchedDiaries = allDiaries;

        searchQuery.observeForever(query -> {
            if (query == null || query.isEmpty()) {
                searchedDiaries = allDiaries;
            } else {
                searchedDiaries = repository.searchDiaries(query);
            }
        });
    }

    public LiveData<List<Diary>> getAllDiaries() {
        return allDiaries;
    }

    public LiveData<List<Diary>> getSearchedDiaries() {
        return searchedDiaries != null ? searchedDiaries : allDiaries;
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
}
