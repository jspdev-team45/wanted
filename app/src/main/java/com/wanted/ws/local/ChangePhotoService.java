package com.wanted.ws.local;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wanted.R;
import com.wanted.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xli2 on 2015/11/13.
 */
public class ChangePhotoService {
    private Uri outputFileUri;
    private final int SELECT_IMAGE = 1;

    private Context context;

    private ImageView targetView;

    private static final ChangePhotoService instance = new ChangePhotoService();

    private ChangePhotoService() {}

    public static ChangePhotoService getInstance() {
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTargetView(ImageView targetView) {
        this.targetView = targetView;
    }

    public ImageView getTargetView() {
        return targetView;
    }

    /**
     *
     */
    public void openImageIntent() {
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator +
                                    "Wanted" + File.separator);
        root.mkdir();
        final String filename = new FileUtil().getUniqueImageFilename();
        final File sdImageDirectory = new File(root, filename);
        outputFileUri = Uri.fromFile(sdImageDirectory);

        // camera
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // filesystem
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // chooser of filesystem options
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // add camera options
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                               cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        ((Activity)context).startActivityForResult(chooserIntent, SELECT_IMAGE);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public Uri getImageFromResult(int requestCode, int resultCode, Intent data) {
        boolean isCamera;
        Uri selectedImageUri = null;

        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_IMAGE) {
            if (data == null) {
                isCamera = true;
            }
            else {
                String action = data.getAction();
                if (action == null)
                    isCamera = false;
                else
                    isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }

            if (isCamera)
                selectedImageUri = outputFileUri;
            else
                selectedImageUri = (data == null ? null : data.getData());
        }
        return selectedImageUri;
    }
}
