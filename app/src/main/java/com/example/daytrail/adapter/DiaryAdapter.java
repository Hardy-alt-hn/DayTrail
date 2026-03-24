package com.example.daytrail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daytrail.R;
import com.example.daytrail.data.Diary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryAdapter extends ListAdapter<Diary, DiaryAdapter.DiaryViewHolder> {
    private OnDiaryClickListener listener;

    public interface OnDiaryClickListener {
        void onEdit(Diary diary);
        void onDelete(Diary diary);
    }

    public void setOnDiaryClickListener(OnDiaryClickListener listener) {
        this.listener = listener;
    }

    public DiaryAdapter() {
        super(new DiffUtil.ItemCallback<Diary>() {
            @Override
            public boolean areItemsTheSame(@NonNull Diary oldItem, @NonNull Diary newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Diary oldItem, @NonNull Diary newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.getContent().equals(newItem.getContent());
            }
        });
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        Diary diary = getCurrentList().get(position);
        holder.bind(diary);
    }

    class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView dateTextView;
        TextView weatherTextView;
        Button editButton;
        Button deleteButton;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            weatherTextView = itemView.findViewById(R.id.weatherTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Diary diary) {
            titleTextView.setText(diary.getTitle());
            contentTextView.setText(diary.getContent());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            dateTextView.setText(sdf.format(new Date(diary.getDate())));

            String weather = diary.getWeather();
            if ("sunny".equals(weather)) {
                weatherTextView.setText("☀️");
            } else if ("cloudy".equals(weather)) {
                weatherTextView.setText("☁️");
            } else if ("rainy".equals(weather)) {
                weatherTextView.setText("🌧️");
            } else if ("snowy".equals(weather)) {
                weatherTextView.setText("❄️");
            } else {
                weatherTextView.setText("☀️");
            }

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(diary);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(diary);
                }
            });
        }
    }
}
