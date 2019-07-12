package com.example.customrecyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

/**
 * Created by Seaman on 2019-07-12.
 * Banggood Ltd
 */
public class FloorListAdapter extends RecyclerView.Adapter<FloorListAdapter.FloorViewHolder> {

    private Context context;
    private FragmentManager fragmentManager;
    private RecomPagerAdapter recomPagerAdapter;

    private FloorViewHolder mRecom;

    public FloorListAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;

    }

    @NonNull
    @Override
    public FloorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType, parent, false);
        return new FloorViewHolder(view, viewType == R.layout.item_recomd);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull FloorViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.isRecomd) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            lp.height = Utils.getScreenHeight(context) - Utils.dpToPx(80);
            holder.itemView.setLayoutParams(lp);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final FloorViewHolder holder, int position) {
        if (holder.isRecomd) {
            if (recomPagerAdapter == null) {
                recomPagerAdapter = new RecomPagerAdapter(fragmentManager);
                holder.pager.setAdapter(recomPagerAdapter);
                holder.tabs.setupWithViewPager(holder.pager);
            }

        } else {
            String text = "楼层 -- " + position;
            holder.text.setText(text);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Click position = " + holder.getAdapterPosition(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return R.layout.item_recomd;
        }
        return R.layout.item_floor;
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public static class FloorViewHolder extends RecyclerView.ViewHolder {
        public final boolean isRecomd;
        public TextView text;

        private ViewPager pager;
        private TabLayout tabs;

        public FloorViewHolder(@NonNull View itemView, boolean isRecomd) {
            super(itemView);
            this.isRecomd = isRecomd;
            text = itemView.findViewById(R.id.text);

            pager = itemView.findViewById(R.id.pager);
            tabs = itemView.findViewById(R.id.tabs);
        }


    }
}
