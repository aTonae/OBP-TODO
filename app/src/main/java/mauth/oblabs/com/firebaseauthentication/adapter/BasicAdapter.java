package raven.oblabs.com.raven.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mauth.oblabs.com.firebaseauthentication.R;


/**
 * Created by android on 8/3/17.
 */

public class BasicAdapter extends RecyclerView.Adapter<BasicAdapter.Holder> {
    List list;
    Context context;


    public BasicAdapter(List list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_new, parent, false);
        context = parent.getContext();
        view.setOnClickListener(itemCliked());
        return new Holder(view);
    }

    private View.OnClickListener itemCliked(  ) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        };
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {





    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class Holder extends RecyclerView.ViewHolder {




        public Holder(View itemView) {
            super(itemView);



        }
    }


}
