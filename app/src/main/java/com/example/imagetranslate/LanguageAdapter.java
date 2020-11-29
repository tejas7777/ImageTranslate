package com.example.imagetranslate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {
    String[] data;
    String[] code;
    Context context;
    Context parentcontext;
    public LanguageAdapter(String[] langauges, String[] BCP_CODES, Context context){
        this.data = langauges;
        this.code = BCP_CODES;
        this.parentcontext = context;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.simple_list_layout,parent,false);
        context = parent.getContext();
        return new LanguageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, final int position) {
        String title = data[position];
        holder.text.setText(title);
        Drawable drawable = context.getResources().getDrawable(context.getResources()
                .getIdentifier("a"+position, "drawable", context.getPackageName()));
        holder.flagimage.setImageDrawable(drawable);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(parentcontext,TranslateActivity.class);
                nextActivity.putExtra("native_ln",code[position]);
                ((MainActivity)parentcontext).startActivity(nextActivity);
                ((MainActivity)parentcontext).finish();

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder{
        ImageView flagimage;
        TextView text;
        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            flagimage = (ImageView) itemView.findViewById(R.id.flagimage);
            text = (TextView) itemView.findViewById(R.id.text);

        }
    }

}
