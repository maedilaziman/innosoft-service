package com.maedi.example.easy.service.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.maedi.example.easy.service.R;

import java.util.ArrayList;

public class ListUniversalSheet extends RecyclerView.Adapter<ListUniversalSheet.ViewHolder> {

    Context context;
    int layoutResourceId;
    ArrayList data = null;

    public ListUniversalSheet(Context context, int layoutResourceId, ArrayList data) {
        if(layoutResourceId != 0)this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public ListUniversalSheet.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        return new ListUniversalSheet.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vholder, final int position) {
        if(data.size() > 0) {
            String title = (String) data.get(position);
            vholder.title.setText(title);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        LinearLayout mainLayout;

        public ViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.title);
            this.mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}