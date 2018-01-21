package com.wyb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by VIBHAV on 04-10-2017.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private Context context;
    private List<UserMenu> menus;

    public MenuAdapter(Context context, List<UserMenu> menus) {
        this.context = context;
        this.menus = menus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_card,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, int position) {

        holder.food.setText(menus.get(position).getFood());
        holder.cost.setText(menus.get(position).getCost());

    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView food;
        public TextView cost;


        public ViewHolder(View itemView) {
            super(itemView);
            food = (TextView) itemView.findViewById(R.id.food);
            cost = (TextView) itemView.findViewById(R.id.cost);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
