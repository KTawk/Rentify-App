package adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentify.R;
import com.example.rentify.WelcomeActivity;

import java.util.List;

import database.context.DataContext;
import models.UserRowInfoContainer;

public class UserItemAdapter extends BaseAdapter {

    private final Context context;
    private DataContext _db;
    private final List<UserRowInfoContainer> itemList;

    public UserItemAdapter(Context context, List<UserRowInfoContainer> itemList) {
        this.context = context;
        _db = new DataContext(context);
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the view if it's not already created
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.user_row, parent, false);
        }

        var item = itemList.get(position);

        TextView itemText = convertView.findViewById(R.id.user_row_info);
        Switch itemSwitch = convertView.findViewById(R.id.user_row_status);
        ImageButton itemButton = convertView.findViewById(R.id.user_row_delete);

        itemText.setText(item.getDisplayText());

        itemSwitch.setChecked(item.isSwitchOn());

        // toggle user status
        itemSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSwitchOn(isChecked);  // Update model with new switch state

            var user = _db.Users.GetById(item.UserId);

            user.IsDisabled = !isChecked;

            _db.Users.Update(user);


            ((WelcomeActivity)context).LoadUsers();
        });

        itemButton.setOnClickListener(v -> {
            _db.Users.DeleteById(item.UserId);

            ((WelcomeActivity)context).LoadUsers();
        });

        return convertView;
    }
}