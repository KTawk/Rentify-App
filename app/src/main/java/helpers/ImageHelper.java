package helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class ImageHelper {


    /**
     * Convert a Bitmap image to a Base64 encoded string.
     *
     * @param bitmap The Bitmap image to convert.
     * @return A Base64 encoded string representing the image.
     */
    public static String bitmapToBinaryString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Retrieve the Base64 encoded string from an ImageView's image.
     *
     * @param uploadedImageView The ImageView containing the image.
     * @return The Base64 encoded string representing the image, or null if no image is set.
     */
    public static String getImageBase64FromImageView(ImageView uploadedImageView) {
        Drawable drawable = uploadedImageView.getDrawable();

        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return bitmapToBinaryString(bitmap);
        } else {
            // Handle the case where there is no valid image or it's not a BitmapDrawable
            return null; // or you could return an empty string or handle differently
        }
    }

    public static Bitmap binaryStringToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            Log.e("ImageHelper", "Failed to decode Base64 string: " + e.getMessage());
            return null;
        }
    }

}
