package com.github.cmput301f21t44.hellohabits.view.habitevent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtil {
    public static String imageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        String baseStr = Base64.encodeToString(buffer, Base64.DEFAULT);
        return baseStr;
    }

    public static Bitmap base64ToImage(String bitmap64) {
        byte[] bytes = Base64.decode(bitmap64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;

    }


    public static void handleImageBeforeApi19(Activity activity, ImageView imageView, Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(activity, uri, null);
        displayImage(imageView, imagePath);
    }

    @TargetApi(19)
    public static void handleImageOnApi19(Activity activity, ImageView imageView, Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);

            if (TextUtils.equals(uri.getAuthority(), "com.android.providers.media.documents")) {
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            } else if (TextUtils.equals(uri.getAuthority(), "com.android.providers.downloads.documents")) {
                if (documentId != null && documentId.startsWith("msf:")) {
                    resolveMSFContent(activity, imageView, uri, documentId);
                    return;
                }
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                imagePath = getImagePath(activity, contentUri, null);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(activity, uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }

        displayImage(imageView, imagePath);
    }

    public static String getImagePath(Activity activity, Uri uri, String selection) {
        String path = null;
        Cursor cursor = activity.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    public static String resolveMSFContent(Activity activity, ImageView imageView, Uri uri, String documentId) {
        // return this function to imageBase64 in CreateEditHabitEventFragment
        File file = new File(activity.getCacheDir(), "temp_file" + activity.getContentResolver().getType(uri).split("/")[1]);

        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);

            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);

            return ImageUtil.imageToBase64(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }


    public static String displayImage(ImageView imageView, String imagePath) {
        // return this to imageBase64 in CreateEditHabitEventFragment.java
        Log.d("tag", "displayImage: ------------" + imagePath);

        try {
            new FileInputStream(imagePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
            return ImageUtil.imageToBase64(bitmap);
        }
        return "";
    }


}
