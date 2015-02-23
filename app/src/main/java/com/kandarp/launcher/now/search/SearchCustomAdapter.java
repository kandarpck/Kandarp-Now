package com.kandarp.launcher.now.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kandarp.launcher.now.R;

import java.util.List;

/**
 * Created by Kandarp on 2/23/2015.
 */
public class SearchCustomAdapter extends
        com.haarman.listviewanimations.ArrayAdapter<SearchDataRow> {
    Context context;

    public SearchCustomAdapter(Context context, int resourceid,
                               List<SearchDataRow> rowItems) {
        super(rowItems);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        SearchDataRow rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            //Log.e("OMG", "Came here");
            convertView = mInflater.inflate(R.layout.search_now_web_list_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView
                    .findViewById(R.id.search_list_title);
            holder.txtUrl = (TextView)
                    convertView.findViewById(R.id.search_list_url);
            holder.txtContent = (TextView) convertView
                    .findViewById(R.id.search_list_content);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        //Log.e("Adapter", rowItem.getContent() + " ," + rowItem.getUrl());

        holder.txtTitle.setText(rowItem.getTitle());
        holder.txtContent.setText(rowItem.getContent());
        holder.txtUrl.setText(rowItem.getUrl());
        return convertView;
    }

    /* private view holder class */
    private class ViewHolder {
        TextView txtTitle;
        TextView txtUrl;
        TextView txtContent;

    }
}
