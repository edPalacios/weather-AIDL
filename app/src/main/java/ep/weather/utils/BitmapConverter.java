package ep.weather.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by Eduardo on 04/06/2015.
 */
public class BitmapConverter {

    public static Bitmap byteToBitmap(byte[] imageBytes) {
        Bitmap bitmap = null;
        if (imageBytes != null) {
             bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return  bitmap;
    }

    public static byte[] BitmapToByte(Bitmap bitmap) {
//        Bitmap bitmap = BitmapFactory.decodeFile("/path/images/image.jpg");  // We have to do this outside
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
