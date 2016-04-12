package app.mycamera.com.mycamera.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import app.mycamera.com.mycamera.R;


/**
 * Created by EXUTECH007 on 2015/5/21.
 */
public class PicSelectTypeDialog extends Activity {
    private final String TAG = "PicSelectTypeDialog";

    public final static String IMAGE_PATH = "image_path";
    public final static String IMAGE_CROP = "image_crop";

    public final int REQUEST_SELECT_PIC_FROM_CAMERA = 0;    //拍照
    public final int REQUEST_SELECT_PIC_FROM_GALLERY = 1;    //相册
    public final int REQUEST_SELECT_PHOTO_CROP = 2;    //截取
    public final static String pictureWith="width";//截图的长
    public final static String pictureHeight="height";//截图的宽

    private TextView mTypeCamera;
    private TextView mTypeGallery;
    private String mImagePath;
    private int width,height;
    private Uri mImageUri;
    private boolean mIsCrop = false;        //bCrop:是否出现对图片的剪切界面。true：则出现
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //要验证路径不能为空
        mImagePath = getIntent().getStringExtra(IMAGE_PATH);
        if (TextUtils.isEmpty(mImagePath)) {
//            ULog.e(TAG, "onCreate", "must specify the image path.");
            finish();
            return;
        }
        if (TextUtils.isEmpty(getIntent().getStringExtra(pictureWith))){
            width=2;
        }else {
            width= Integer.valueOf(getIntent().getStringExtra(pictureWith));
        }
        if (TextUtils.isEmpty(getIntent().getStringExtra(pictureHeight))){
            height=1;
        }else {
            height= Integer.valueOf(getIntent().getStringExtra(pictureHeight));
        }

//        ULog.i(TAG, "onCreate", "image path: %s" + mImagePath);
        mFile = new File(mImagePath);
        if (null == mFile) {
//            ULog.e(TAG, "onCreate", "the image path is invalid, please check it.");
            finish();
            return;
        }
        mImageUri = Uri.fromFile(mFile);
        mIsCrop = getIntent().getBooleanExtra(IMAGE_CROP, false);

        setContentView(R.layout.dialog_pic_select_type);
        mTypeCamera = (TextView) findViewById(R.id.type_camera);
        mTypeGallery = (TextView) findViewById(R.id.type_gallery);

        mTypeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTypeCamera();
            }
        });
        mTypeGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTypeGallery();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
//            ULog.e(TAG, "onActivityResult", "request code:" + requestCode + "result code:" + resultCode);
            return;
        }
        switch (requestCode) {
            case REQUEST_SELECT_PIC_FROM_GALLERY:
                if (mIsCrop) {
                    cropImage(data.getData());
                } else {
                    doCropImage();
                }
                break;
            case REQUEST_SELECT_PIC_FROM_CAMERA:
                if (mIsCrop) {
                    cropImage(mImageUri);
                } else {
                    doCropImage();
                }
                break;
            case REQUEST_SELECT_PHOTO_CROP:
                doCropImage();
                break;
        }
    }
    /*
 相册选取
  */
    public void selectTypeGallery() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        }

        startActivityForResult(intent, REQUEST_SELECT_PIC_FROM_GALLERY);
    }

    /*
        拍照
         */
    public void selectTypeCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, REQUEST_SELECT_PIC_FROM_CAMERA);
    }

    /*
    截图方法
     */
    public void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        intent.putExtra("scale", false);     //不允许修改大小
        intent.putExtra("aspectX", 14);
        intent.putExtra("aspectY", 14);
        intent.putExtra("outputX", 640);    //TODO:大小
        intent.putExtra("outputY", 640);
        //已经指定 “REQUEST_SELECT_PHOTO_CROP”为截取返回码
        startActivityForResult(intent, REQUEST_SELECT_PHOTO_CROP);
    }
    /*
    处理截图后的图片
     */
    public void doCropImage() {
//        ULog.e(TAG, "doCropImage", "the image has been crop");
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
            FileOutputStream fileOutputStream = new FileOutputStream(mFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setResult(Activity.RESULT_OK);
        finish();
    }
}
