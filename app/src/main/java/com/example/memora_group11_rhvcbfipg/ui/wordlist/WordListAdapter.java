package com.example.memora_group11_rhvcbfipg.ui.wordlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.modal.WordModal;
import com.example.memora_group11_rhvcbfipg.ui.forms.FolderFormActivity;
import com.example.memora_group11_rhvcbfipg.ui.forms.WordFormActivity;
import com.example.memora_group11_rhvcbfipg.utils.SoundButtonListener;

import java.util.ArrayList;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ItemViewHolder> {

    private ArrayList<WordModal> wordModalArrayList;
    private Context context;
    private DBHandler dbHandler;
    private int selectedPosition;

    ImageView wordMenuIcon;

    AlertDialog.Builder builder;
    AlertDialog dialog;
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

        // Set a click listener on the view to show the context menu with sound
        wordMenuIcon.setOnClickListener(new SoundButtonListener(context,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedPosition = holder.getAdapterPosition();
                        showContextMenu(view);
                    }
                }, R.raw.button_click));
    }

    @Override
    public int getItemCount() {
        return wordModalArrayList.size();
    }

    public void showContextMenu(View view) {
        // Inflate custom menu layout
        View popupView = LayoutInflater.from(context).inflate(R.layout.custom_word_menu, null);

        // Create popup window
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Set elevation for shadow effect
        popupWindow.setElevation(10);

        // Set up click listeners for menu items with sound
        popupView.findViewById(R.id.menuEditWord).setOnClickListener(new SoundButtonListener(context,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editWord();
                        popupWindow.dismiss();
                    }
                }, R.raw.button_click));

        popupView.findViewById(R.id.menuDeleteWord).setOnClickListener(new SoundButtonListener(context,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteWord();
                        popupWindow.dismiss();
                    }
                }, R.raw.button_click));

        // Position the popup just below and to the right of the anchor view
        popupWindow.showAsDropDown(view, -150, 10);
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
        if(selectedPosition != RecyclerView.NO_POSITION) {
            WordModal selectedWord = wordModalArrayList.get(selectedPosition);

            // Inflate the custom dialog layout
            View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_word_dialog, null);

            // Create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            // Set transparent background
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Find views
            TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
            com.google.android.material.button.MaterialButton buttonNegative = dialogView.findViewById(R.id.buttonNegative);
            com.google.android.material.button.MaterialButton buttonPositive = dialogView.findViewById(R.id.buttonPositive);

            // Set message with word name
            dialogMessage.setText("Are you sure you want to delete \"" + selectedWord.getWord() +
                    "\"?\n\nThis will permanently this item.");

            // Set button click listeners with sound
            buttonNegative.setOnClickListener(new SoundButtonListener(context,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    }, R.raw.button_back, 0.4f));

            buttonPositive.setOnClickListener(new SoundButtonListener(context,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Delete the word
                            dbHandler.deleteWord(selectedWord.getId());
                            wordModalArrayList.remove(selectedPosition);
                            notifyItemRemoved(selectedPosition);
                            notifyItemRangeChanged(selectedPosition, wordModalArrayList.size());

                            Toast.makeText(context, "Word Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }, R.raw.button_success, 3.0f));

            // Show dialog
            dialog.show();
        }
    }
}
