package com.example.easy.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PicCompress {

    public static Bitmap bitmap;

    public static Bitmap SampleRateCompress(String filepath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(filepath, options);
        return bitmap;
    }
}
