package com.example.magic_code.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magic_code.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final ArrayList<HashMap<String,Object>> localDataSet;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView authorView;
        private final CardView rootView;
        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.titleTextView);
            authorView = (TextView) view.findViewById(R.id.authorTextView);
            rootView = (CardView) view;
        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getAuthorView() {
            return authorView;
        }

        public CardView getRootView() {
            return rootView;
        }
    }

    public CustomAdapter(ArrayList<HashMap<String, Object>> dataSet, Context ctx) {
        localDataSet = dataSet;
        context = ctx;
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.text_row_item, parent, false);
        return new CustomAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText((String) localDataSet.get(position).get("Title"));
        viewHolder.getAuthorView().setText((String) localDataSet.get(position).get("Author"));
        viewHolder.getRootView().setTag((String) localDataSet.get(position).get("ID"));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
