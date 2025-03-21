package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.example.rentify.R;

import java.util.List;

import models.RequestedItemsInfoContainer;

public class RequestedItemAdapter extends BaseAdapter {

    private Context context;
    private List<RequestedItemsInfoContainer> dataList; // List of data for the TextView (e.g., request details)
    private OnRequestActionListener listener;

    public RequestedItemAdapter(Context context, List<RequestedItemsInfoContainer> dataList, OnRequestActionListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // Inflate the layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.requested_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.req_info_request);
            viewHolder.rejectButton = convertView.findViewById(R.id.reject_btn_request);
            viewHolder.approveButton = convertView.findViewById(R.id.approve_btn_request);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set the text for the TextView
        var requestInfo = dataList.get(position);
        viewHolder.textView.setText(requestInfo.Name + " - " + requestInfo.Category + " - " + requestInfo.Desc);

        // Set button click listeners
        viewHolder.rejectButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(position);
            }
        });

        viewHolder.approveButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApprove(position);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
        Button rejectButton;
        Button approveButton;
    }

    // Interface for button click events
    public interface OnRequestActionListener {
        void onReject(int position);

        void onApprove(int position);
    }
}
