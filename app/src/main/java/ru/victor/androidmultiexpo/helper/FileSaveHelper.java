package ru.victor.androidmultiexpo.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Виктор on 13.02.2016.
 */
public class FileSaveHelper {

    private Context mFileContext;
    private File mDirectory;
    private File mFile;

    public FileSaveHelper(Context context) {
        mFileContext = context;
    }

    public void createInstagramIntent() {

        String type = "image/*";
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        Uri uri = Uri.fromFile(mFile);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        mFileContext.startActivity(Intent.createChooser(share, "Share to"));
    }

    public void saveImageOnSD(Bitmap bitmap) {

        createDirectory();

        mFile = null;
        mFile = new File(mDirectory.getPath() + "/" + "image_"
                + System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } finally {
                if (fos != null) fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        galleryAddPic(mFile.toString());

        Toast.makeText(mFileContext, "mFile saved,fileName = " + mFile, Toast.LENGTH_SHORT).show();

    }

    // создание папки для хранения изображений на SD
    private void createDirectory() {
        mDirectory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "AndroidMultiexpo");
        if (!mDirectory.exists())
            mDirectory.mkdirs();
    }

    //обновление галереи
    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mFileContext.sendBroadcast(mediaScanIntent);
    }
}
