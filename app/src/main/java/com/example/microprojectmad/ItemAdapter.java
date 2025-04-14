package com.example.microprojectmad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_home_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.title.setText("Item : " + item.getTitle());
        holder.date.setText("Date : " + item.getDate());
        holder.uname.setText("Reported By : " + item.getUsername());
        Glide.with(holder.itemView.getContext())
                .load(item.getImage())  // image URL from your API
                .placeholder(R.drawable.man)  // fallback while loading
                .error(R.drawable.man)        // fallback if loading fails
                .into(holder.itemImage);


        String rid = item.getReportId();

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, activity_itemdetail.class);
            intent.putExtra("report_id", rid);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, uname,reportId;
        ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            uname = itemView.findViewById(R.id.reportedby);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}
