package com.cxsj.runhdu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cxsj.runhdu.model.HelpItem;
import com.cxsj.runhdu.R;

import java.util.List;

/**
 * Created by chjyp on 2017/5/5.
 * 帮助列表的Adapter
 */


public class HelpAdapter extends ArrayAdapter<HelpItem> {
    private int resourceId;
    public HelpAdapter(Context context, int textViewResourceId, List<HelpItem> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HelpItem help_item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView title = (TextView) view.findViewById(R.id.help_title);
        TextView info = (TextView) view.findViewById(R.id.help_info);
        title.setText(help_item.getTitle());
        info.setText(help_item.getInfo());
        return view;
    }
}