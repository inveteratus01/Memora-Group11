package com.example.memora_group11_rhvcbfipg.ui.folderlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memora_group11_rhvcbfipg.R;
import com.example.memora_group11_rhvcbfipg.database.DBHandler;
import com.example.memora_group11_rhvcbfipg.modal.FolderModal;
import com.example.memora_group11_rhvcbfipg.ui.forms.FolderFormActivity;
import com.example.memora_group11_rhvcbfipg.ui.score.StatisticsFolderActivity;
import com.example.memora_group11_rhvcbfipg.ui.wordlist.WordListActivity;
import com.example.memora_group11_rhvcbfipg.utils.SoundButtonListener;

import java.util.ArrayList;

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.CardViewHolder> {

    private ArrayList<FolderModal> folderModalArrayList;

    private Context context;

    private RecyclerView recyclerView;

    public interface OnFolderDeletedListener {
        void onFolderDeleted(ArrayList<FolderModal> updatedData);
    }

    private OnFolderDeletedListener folderDeletedListener;


    public FolderListAdapter(ArrayList<FolderModal> folderModalArrayList, Context context, OnFolderDeletedListener listener) {
        this.folderModalArrayList = folderModalArrayList;
        this.context = context;
        this.folderDeletedListener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_card, parent, false);
        return new CardViewHolder(view, recyclerView, folderDeletedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        FolderModal folder = folderModalArrayList.get(position);
        holder.bind(folder);

        // Set click listener with sound on the card view
        holder.itemView.setOnClickListener(new SoundButtonListener(context,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(
                                context.getString(R.string.folder_preferences),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(context.getString(R.string.current_folder_id), folder.getFolderId());
                        editor.putString(context.getString(R.string.current_folder_name), folder.getFolderName());
                        editor.apply();

                        Intent intent = new Intent(context, WordListActivity.class);
                        context.startActivity(intent);
                    }
                }, R.raw.button_click));
    }

    @Override
    public int getItemCount() {
        return folderModalArrayList.size();
    }

    public void updateData(ArrayList<FolderModal> newData) {
        folderModalArrayList = newData;
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTitle;
        int folderId;
        Context context;
        OnFolderDeletedListener folderDeletedListener;
        private RecyclerView recyclerView;


        public CardViewHolder(@NonNull View itemView, RecyclerView recyclerView, OnFolderDeletedListener folderDeletedListener) {
            super(itemView);
            this.recyclerView = recyclerView;
            this.folderDeletedListener = folderDeletedListener;

            cardView = itemView.findViewById(R.id.cardView);
            textTitle = itemView.findViewById(R.id.textTitle);
            ImageView menuIcon = itemView.findViewById(R.id.menuIcon);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = itemView.getContext();
                    saveFolderIdToSharedPreferences(context, folderId, textTitle.getText().toString());
                    Intent intent = new Intent(context, WordListActivity.class);
                    context.startActivity(intent);
                }
            });

            menuIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showContextMenu(view);
                }
            });
        }

        public void bind(FolderModal folderModal) {
            textTitle.setText(folderModal.getFolderName());
            folderId = folderModal.getFolderId();
//            setRandomBackgroundColor();
            int wordCount = getWordCountInAFolder(folderId);
            TextView textWordCount = itemView.findViewById(R.id.totalWords);
            textWordCount.setText(String.valueOf(wordCount) + " words");
        }

        private int getWordCountInAFolder(int folderId) {
            DBHandler dbHandler = new DBHandler(itemView.getContext());
            return dbHandler.getTotalWordsInAFolder(folderId);
        }

        public void showContextMenu(View view) {
            // Inflate custom menu layout
            View popupView = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_folder_menu, null);

            // Create popup window
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);

            // Set elevation for shadow effect
            popupWindow.setElevation(10);

            // Set up click listeners for menu items
            popupView.findViewById(R.id.menuEditFolder).setOnClickListener(new SoundButtonListener(itemView.getContext(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editFolder();
                            popupWindow.dismiss();
                        }
                    }, R.raw.button_click, 0.3f));

            popupView.findViewById(R.id.menuStatisticsFolder).setOnClickListener(new SoundButtonListener(itemView.getContext(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showStatisticsFolder();
                            popupWindow.dismiss();
                        }
                    }, R.raw.button_click));

            popupView.findViewById(R.id.menuDeleteFolder).setOnClickListener(new SoundButtonListener(itemView.getContext(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteFolder();
                            popupWindow.dismiss();
                        }
                    }, R.raw.button_click));

            // Position the popup just below and to the right of the anchor view
            popupWindow.showAsDropDown(view, -150, 10);
        }

        private void editFolder() {
            Context context = itemView.getContext();
            String folderName = textTitle.getText().toString();
            Intent intent = new Intent(context, FolderFormActivity.class);
            intent.putExtra("folderId", folderId);
            intent.putExtra("folderName", folderName);
            context.startActivity(intent);
        }

        private void deleteFolder() {
            Context context = itemView.getContext();

            // Create a custom layout for the dialog
            View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_folder_dialog, null);
            TextView titleText = dialogView.findViewById(R.id.dialogTitle);
            TextView messageText = dialogView.findViewById(R.id.dialogMessage);
            Button positiveButton = dialogView.findViewById(R.id.buttonPositive);
            Button negativeButton = dialogView.findViewById(R.id.buttonNegative);
            ImageView warningIcon = dialogView.findViewById(R.id.warningIcon);

            // Set dialog content
            titleText.setText("Delete Folder");
            messageText.setText("Are you sure you want to delete \"" + textTitle.getText().toString() +
                    "\"?\n\nThis will permanently delete all words in this folder.");
            warningIcon.setImageResource(R.drawable.ic_warning);

            // Create and configure the AlertDialog with custom view
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();

            // Prevent dialog from closing when touching outside
            alertDialog.setCanceledOnTouchOutside(false);

            // Set button click listeners
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Delete folder logic
                    DBHandler dbHandler = new DBHandler(itemView.getContext());
                    dbHandler.deleteFolder(folderId);
                    ArrayList<FolderModal> updatedData = dbHandler.getFolderNames();

                    // Play delete sound effect
                    new SoundButtonListener(context,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Empty, just for sound
                                }
                            },
                            R.raw.button_success,
                            3.0f
                    ).onClick(null);

                    // Notify adapter of changes
                    if (folderDeletedListener != null) {
                        folderDeletedListener.onFolderDeleted(updatedData);
                    }

                    // Show confirmation toast
                    Toast.makeText(context, "Folder Deleted!", Toast.LENGTH_SHORT).show();

                    // Dismiss the dialog
                    alertDialog.dismiss();
                }
            });

            negativeButton.setOnClickListener(new SoundButtonListener(context,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Just dismiss the dialog
                            alertDialog.dismiss();
                        }
                    }, R.raw.button_back, 0.3f));

            // Show the custom dialog
            alertDialog.show();
        }

        private void showStatisticsFolder() {
            Context context = itemView.getContext();
            String folderName = textTitle.getText().toString();
            Intent intent = new Intent(context, StatisticsFolderActivity.class);
            intent.putExtra("folderId", folderId);
            intent.putExtra("folderName", folderName);
            context.startActivity(intent);
        }


//        private void setRandomBackgroundColor() {
//            TypedArray typedArray = itemView.getResources().obtainTypedArray(R.array.android_colors);
//            int randomColor = typedArray.getColor(new Random().nextInt(typedArray.length()), 0);
//            typedArray.recycle();
//            cardView.setCardBackgroundColor(randomColor);
//        }

        private void saveFolderIdToSharedPreferences(Context context, int folderId, String folderName) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("FolderPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("currentFolderId", folderId);
            editor.putString("currentFolderName", folderName);
            editor.apply();
        }
    }
}

