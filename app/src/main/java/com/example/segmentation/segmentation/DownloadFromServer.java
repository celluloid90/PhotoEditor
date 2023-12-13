package com.example.segmentation.segmentation;

import static com.example.segmentation.segmentation.ApiSegmentationManager.SEGMENTED_FOLDER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.segmentation.segmentation.models.SegmentedImageDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadFromServer {
    private final Context mContext;
    private ArrayList<String> mEmojiCategoryNameList;
    public static final String CONTENTS_PATH = "contents_path";
    public static final String CONTENTS_NAME = "contents_name";

    public DownloadFromServer(Context context, ArrayList<String> emojiCategoryNameList) {
        mContext = context;
        this.mEmojiCategoryNameList = emojiCategoryNameList;
    }

    public DownloadFromServer(Context context) {
        mContext = context;
    }

    public void downloadSegmentedImage(String imagePath, SegmentedImageDownloadListener listener) {
        String root = String.valueOf(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        if (isCategoryDirCreated(mContext, SEGMENTED_FOLDER)) {
            try {
                String[] thumbDownloadArgs = {imagePath, root + File.separator + SEGMENTED_FOLDER + File.separator + "segmentedImage.png"};

                DownloadTask thumbDownloadTask = new DownloadTask(mContext, listener);
                thumbDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, thumbDownloadArgs);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static boolean isCategoryDirCreated(Context context, String DirName) {
        String root = String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        File newDir = new File(root + File.separator + DirName);
        if (!newDir.exists()) {
            return newDir.mkdirs();
        }
        return true;
    }

    private static void ensureZipPathSafety(final File outputFile, final String destDirectory) throws Exception {
        String destDirCanonicalPath = (new File(destDirectory)).getCanonicalPath();
        String outputFilecanonicalPath = outputFile.getCanonicalPath();
        if (!outputFilecanonicalPath.startsWith(destDirCanonicalPath)) {
            throw new Exception(String.format("Found Zip Path Traversal Vulnerability with %s", destDirCanonicalPath));
        }
    }

    private static class DownloadTask extends AsyncTask<String, Integer, String> {
        private boolean isDownloadLandingItemPack = false;
        @SuppressLint("StaticFieldLeak")
        private final Context context;
        private final SegmentedImageDownloadListener segmentedImageDownloadListener;

        public DownloadTask(Context context, SegmentedImageDownloadListener listener) {
            this.segmentedImageDownloadListener = listener;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... args) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String destinationFilePath = "";
            File tempFile;

            try {
                URL url = new URL(args[0]);
                destinationFilePath = args[1];

                tempFile = new File(destinationFilePath);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                // download the file
                input = connection.getInputStream();

                tempFile.createNewFile();
                output = new FileOutputStream(tempFile);

                byte[] data = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                }

                if (connection != null) connection.disconnect();
            }

            return "Download Completed!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (segmentedImageDownloadListener != null) {
                ArrayList<String> filePath = Utils.getFolderContents(SEGMENTED_FOLDER, context, CONTENTS_PATH);
                if (filePath.size() > 0) {
                    String path = "file://" + filePath.get(0);
                    segmentedImageDownloadListener.onCompleted(Uri.parse(path), null);
                } else {
                    segmentedImageDownloadListener.onError("Download Error!");
                }
            }
        }
    }
}
