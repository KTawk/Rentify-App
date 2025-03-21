package adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.rentify.EditItemActivity;
import com.example.rentify.R;
import com.example.rentify.ViewItemsActivity;
import com.example.rentify.WelcomeActivity;

import java.time.format.DateTimeFormatter;
import java.util.List;

import database.context.DataContext;
import database.entities.RequestedItem;
import helpers.ImageHelper;
import models.ItemRowInfoContainer;
import models.UserInfo;

public class ItemItemAdapter extends BaseAdapter {


    private final Context context;
    private DataContext _db;
    private final List<ItemRowInfoContainer> itemList;

    public ItemItemAdapter(Context context, List<ItemRowInfoContainer> itemList) {
        this.context = context;
        this.itemList = itemList;
        this._db = new DataContext(context);
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
            convertView = inflater.inflate(R.layout.item_row, parent, false);
        }

        var item = itemList.get(position);
        var allCategories = _db.Categories.GetAll();

        ImageView imageView = convertView.findViewById(R.id.image_ItemRow);
        TextView nameView = convertView.findViewById(R.id.name_itemRow);
        TextView descView = convertView.findViewById(R.id.desc_itemRow);
        TextView fromDateView = convertView.findViewById(R.id.fromDate_itemRow);
        TextView toDateView = convertView.findViewById(R.id.toDate_itemRow);
        TextView priceView = convertView.findViewById(R.id.price_itemRow);
        TextView categoryView = convertView.findViewById(R.id.category_itemRow);
        ImageButton editBtnView = convertView.findViewById(R.id.editBtn_itemRow);
        ImageButton deleteBtnViewView = convertView.findViewById(R.id.deleteBtn_itemRow);

        // Handle image display
        var imageBitmap = (item.ImageBinary != null && !item.ImageBinary.isEmpty())
                ? ImageHelper.binaryStringToBitmap(item.ImageBinary)
                : null;

        if (imageBitmap != null)
            imageView.setImageBitmap(imageBitmap);
        else
            imageView.setImageResource(R.drawable.placeholder_image);

        nameView.setText(item.Name);
        descView.setText(item.Description);
        fromDateView.setText("From : " + item.AbleFromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        toDateView.setText("To : " + item.AbleToDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        priceView.setText(item.Fee + " $");
        categoryView.setText(allCategories.stream().filter(z -> z.Id == item.CategoryId).findFirst().orElseThrow().Name);


        if (UserInfo.Role.Name.equals("Renter")) {
            editBtnView.setVisibility(View.INVISIBLE);
            deleteBtnViewView.setVisibility(View.INVISIBLE);
        } else {
            convertView.findViewById(R.id.requestBtn_itemRow).setVisibility(View.INVISIBLE);
        }

        convertView.findViewById(R.id.requestBtn_itemRow).setOnClickListener((buttonView) -> {
            _db.RequestedItems.Insert(new RequestedItem(item.ItemId, UserInfo.User.Id, item.LessorId, false));
            Toast.makeText(context, "Request was sent to lesser", Toast.LENGTH_SHORT).show();
        });

        editBtnView.setOnClickListener((buttonView) -> {
            // Create an intent to start EditItemActivity
            Intent intent = new Intent(context, EditItemActivity.class);

            // Pass item data to EditItemActivity
            intent.putExtra("ITEM_ID", String.valueOf(item.ItemId)); // Pass the item ID

            // Start EditItemActivity
            context.startActivity(intent);
            Toast.makeText(context, "Editing item...", Toast.LENGTH_SHORT).show();
        });

        deleteBtnViewView.setOnClickListener((buttonView) -> {
            _db.Items.DeleteById(item.ItemId);
            ((ViewItemsActivity) context).LoadItems();
            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
        });
        return convertView;
    }
}
