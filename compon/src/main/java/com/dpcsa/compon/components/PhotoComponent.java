package com.dpcsa.compon.components;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.custom_components.SimpleImageView;
import com.dpcsa.compon.glide.GlideApp;
import com.dpcsa.compon.glide.GlideRequest;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.PermissionsResult;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.tools.Constants;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.circleCropTransform;

public class PhotoComponent extends BaseComponent{

    View view;
    private Uri photoURI;
    private String photoPath;
    private String imgPath;

    public PhotoComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        view = parentLayout.findViewById(paramMV.paramView.viewId);
        if (view == null) {
            iBase.log( "Не найден View в " + multiComponent.nameComponent);
            return;
        }
        paramMV.startActual = false;
        view.setOnClickListener(clickListener);
    }

    public String getFilePath() {
        return imgPath + "";
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            obtainingPermits();
        }
    };

    public void obtainingPermits() {
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            obtainingPermitsStorage();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions();
            }
        }
    }

    public void requestPermissions() {
        activity.addPermissionsResult(Constants.REQUEST_CODE_CAMERA, permissionsResult);
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                Constants.REQUEST_CODE_CAMERA);
    }

    public PermissionsResult permissionsResult = new PermissionsResult() {
        @Override
        public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == Constants.REQUEST_CODE_CAMERA && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    obtainingPermitsStorage();
                }
            }
        }
    };

    private void obtainingPermitsStorage() {
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            startPhoto();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissionsStorage();
            }
        }
    }


    public void requestPermissionsStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.addPermissionsResult(Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE, permissionsResultStorage);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    public PermissionsResult permissionsResultStorage = new PermissionsResult() {
        @Override
        public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPhoto();
                }
            }
        }
    };

    private void startPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            iBase.log("Error occurred while creating the File");
        }
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(activity,
                    activity.getPackageName() +".provider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        List<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentList = addIntentsToList(activity, intentList, pickIntent);
        intentList = addIntentsToList(activity, intentList, takePictureIntent);
        Intent chooserIntent = null;
        String st;
        if (paramMV.paramView.idStringExtra == 0) {
            st = "";
        } else {
            st = activity.getString(paramMV.paramView.idStringExtra);
        }
        if(intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), st);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }
        activity.addForResult(Constants.REQUEST_CODE_PHOTO, activityResult);
        activity.startActivityForResult(chooserIntent, Constants.REQUEST_CODE_PHOTO);
    }

    private List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            targetedIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            list.add(targetedIntent);
        }
        return list;
    }

    private ActivityResult activityResult = new ActivityResult() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse) {
            if(resultCode == activity.RESULT_OK) {
                Uri selectedImage;
                if (data == null) {
                    selectedImage = null;
                } else {
                    selectedImage = data.getData();
                }
                if (selectedImage != null) {
                    imgPath = selectedImage.toString();
                    showImg(selectedImage);
//                    img.setImageURI(selectedImage);
                } else {
                    imgPath = photoURI.toString();
                    showImg(photoURI);
//                    img.setImageURI(photoURI);
                }
            }
        }
    };

    private void showImg(Uri uri) {
        if (paramMV.paramView.layoutTypeId != null && paramMV.paramView.layoutTypeId.length > 0) {
            ImageView img;
            int ik = paramMV.paramView.layoutTypeId.length;
            for (int i = 0; i < ik; i++) {
                img = (ImageView) parentLayout.findViewById(paramMV.paramView.layoutTypeId[i]);
                if (img != null) {
                    GlideRequest gr = GlideApp.with(activity).load(uri);
                    if (img instanceof SimpleImageView) {
                        SimpleImageView simg = (SimpleImageView) img;
                        if (simg.getBlur() > 0) {
                            gr.apply(bitmapTransform(new BlurTransformation(simg.getBlur())));
                        }
                        if (simg.isOval()) {
                            gr.apply(circleCropTransform());
                        }
//                        gr.transform(new RoundedCornersTransformation(120, 0));
                    }
                    gr.into(img);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);
        photoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void changeData(Field field) {

    }
}
