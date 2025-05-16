package com.example.memora_group11_rhvcbfipg.ui.wordlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.modal.WordModal;

import java.util.ArrayList;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ItemViewHolder> {

    private ArrayList<WordModal> wordModalArrayList;
    private Context context;
    private DBHandler dbHandler;

    public WordListAdapter(ArrayList<WordModal> wordModalArrayList, Context context) {
        this.wordModalArrayList = wordModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        WordModal item = wordModalArrayList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return wordModalArrayList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
        }

        public void bind(WordModal item) {
            textTitle.setText(item.getWord());
        }
    }
}
