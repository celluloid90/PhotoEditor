package com.example.segmentation.segmentation;

import static com.example.segmentation.segmentation.ApiSegmentationManager.SEGMENTED_FOLDER;
import static com.example.segmentation.segmentation.DownloadFromServer.CONTENTS_NAME;
import static com.example.segmentation.segmentation.DownloadFromServer.CONTENTS_PATH;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import com.example.photo_editor.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author: Raihan Uddin Piash (raihan.uddin@braincraftapps.com)
 * @project: Segmentation
 * @date: On 11/6/23 at 1:46 PM
 */
public class Utils {
    public static ArrayList<String> getFolderContents(String directoryName, Context context, String valueType) {
        ArrayList<String> stickerList = new ArrayList<>();
        File[] listFile;
        String root = String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        File file = new File(root + File.separator, directoryName);

        if (file.isDirectory()) {
            listFile = file.listFiles();
            if (listFile != null) {
                for (File value : listFile) {
                    if (valueType.equalsIgnoreCase(CONTENTS_PATH)) {
                        if (!value.getAbsolutePath().endsWith(".zip")) {
                            stickerList.add(value.getAbsolutePath());
                        }
                    } else if (valueType.equalsIgnoreCase(CONTENTS_NAME)) {
                        stickerList.add(value.getName());
                    }
                }
            }
        }
        return stickerList;
    }

    public static void deleteFolderContents(String directoryName, Context context) {
        File[] listFile;
        String root = String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        File file = new File(root + File.separator, directoryName);

        if (file.isDirectory()) {
            listFile = file.listFiles();
            if (listFile != null) {
                for (File value : listFile) {
                    if (!value.getAbsolutePath().endsWith(".zip")) {
                        value.delete();
                    }
                }
            }
        }
    }

    public static File saveBeforeSegmentationBitmapToStorage(Bitmap bitmap, Context context) {
        File file = new File(getOutputPath(context));
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private static String getOutputPath(Context context) {
        String root = String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        File myDir = new File(root + File.separator + SEGMENTED_FOLDER);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fName = "before_segmentation.png";
        File file = new File(myDir, fName);
        file.getAbsolutePath();

        if (file.exists()) file.delete();

        return file.getAbsolutePath();
    }

    public static Bitmap rotateBitmap(Context context, String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(context, src);

            if (orientation == 1) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static int getExifOrientation(Context context, String src) throws IOException {
        int orientation = 1;
        InputStream stream = null;
        try {
            ExifInterface exifInterface;
            if (src.startsWith("content")) {
                stream = context.getContentResolver().openInputStream(Uri.parse(src));
                exifInterface = new ExifInterface(stream);
            } else if (src.startsWith("file")) {
                stream = context.getContentResolver().openInputStream(Uri.fromFile(new File(src)));
                exifInterface = new ExifInterface(stream);
            } else {
                File file = new File(src);
                if (!file.exists()) {
                    return 0;
                }
                exifInterface = new ExifInterface(file.getAbsolutePath());
            }
            return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (SecurityException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) stream.close();
        }

        return orientation;
    }
}
