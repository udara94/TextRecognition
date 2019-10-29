package com.vogella.android.textrecognistion;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.BitmapFactory.decodeFile;
import static android.graphics.BitmapFactory.decodeStream;

public class CommonUtils {

    public static String getPath(Context context, Uri uri) {
        String path = "";
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index;
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        }
        return path;
    }

    public static File createTempFile(File file) {
        File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/com.jessicathornsby.myapplication");
        if (!directory.exists() || !directory.isDirectory()) {

            directory.mkdirs();
        }
        if (file == null) {
            file = new File(directory, "orig.jpg");
        }
        return file;
    }

    public static Bitmap resizePhoto(File imageFile, Context context, Uri uri, ImageView view) {
        BitmapFactory.Options newOptions = new BitmapFactory.Options();
        try {
            decodeStream(context.getContentResolver().openInputStream(uri), null, newOptions);
            int photoHeight = newOptions.outHeight;
            int photoWidth = newOptions.outWidth;

            newOptions.inSampleSize = Math.min(photoWidth / view.getWidth(), photoHeight / view.getHeight());
            return compressPhoto(imageFile, decodeStream(context.getContentResolver().openInputStream(uri), null, newOptions));
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static Bitmap resizePhoto(File imageFile, String path, ImageView view) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        decodeFile(path, options);
        int photoHeight = options.outHeight;
        int photoWidth = options.outWidth;
        options.inSampleSize = Math.min(photoWidth / view.getWidth(), photoHeight / view.getHeight());
        return compressPhoto(imageFile, decodeFile(path, options));
    }

    private static Bitmap compressPhoto(File photoFile, Bitmap bitmap) {
        try {
            FileOutputStream fOutput = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fOutput);
            fOutput.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return bitmap;
    }
}
