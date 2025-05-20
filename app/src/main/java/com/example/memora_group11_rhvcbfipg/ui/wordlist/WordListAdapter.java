package com.example.memora_group11_rhvcbfipg.ui.wordlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.modal.WordModal;
import com.example.memora_group11_rhvcbfipg.ui.forms.FolderFormActivity;
import com.example.memora_group11_rhvcbfipg.ui.forms.WordFormActivity;

import java.util.ArrayList;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ItemViewHolder> {

    private ArrayList<WordModal> wordModalArrayList;
    private Context context;
    private DBHandler dbHandler;
    private int selectedPosition;

    ImageView wordMenuIcon;

    public WordListAdapter(ArrayList<WordModal> wordModalArrayList, Context context) {
        this.wordModalArrayList = wordModalArrayList;
        this.context = context;
        this.dbHandler = new DBHandler(context);
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

        wordMenuIcon = holder.itemView.findViewById(R.id.wordMenuIcon);

        // Set a click listener on the view to show the context menu
        wordMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = holder.getAdapterPosition();
                showContextMenu(view);
            }
        });


    }

    @Override
    public int getItemCount() {
        return wordModalArrayList.size();
    }

    public void showContextMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.word_card_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menuEditWord) {
                    editWord();
                    return true;
                } else if (itemId == R.id.menuDeleteWord) {
                    deleteWord();
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
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

    private void editWord() {
        if(selectedPosition != RecyclerView.NO_POSITION){
            WordModal selectedWord = wordModalArrayList.get(selectedPosition);
            Intent intent = new Intent(context, WordFormActivity.class);
            intent.putExtra("wordId", selectedWord.getId());
            intent.putExtra("word", selectedWord.getWord());
            intent.putExtra("description", selectedWord.getMeaning());
            intent.putExtra("folderId", selectedWord.getFolderId());
            intent.putExtra("isEdit", true);
            context.startActivity(intent);
        }
    }

    private void deleteWord() {
        if(selectedPosition != RecyclerView.NO_POSITION){
            WordModal selectedWord = wordModalArrayList.get(selectedPosition);
            dbHandler.deleteWord(selectedWord.getId());
            wordModalArrayList.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            notifyItemRangeChanged(selectedPosition, wordModalArrayList.size());
        }
    }
}
