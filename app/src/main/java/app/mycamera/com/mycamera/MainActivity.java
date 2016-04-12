package app.mycamera.com.mycamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import app.mycamera.com.mycamera.dialog.PicSelectTypeDialog;

public class MainActivity extends Activity {
    private final int REQUEST_SELECT_PIC = 0;
    public final int REQUEST_SELECT_TIME = 1;    //选择时间正确
    private String rootPath,mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (!CommonUtils.isExitsSdcard()) {
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/app_image/";
//        } else {  如果存在sd卡，就将目录创建到sd卡中:/storage/emulated/0/com.exutech.lanjing
//            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + MyApplication.getInstance().getPackageName() + "/";
//        }
        File destDir = new File(rootPath);
        if (!destDir.exists()) {
//            Log.i(TAG, "filename2:" + file_download);
            destDir.mkdirs();
        }
    }

    public void takePhoto(View view) {
        mImagePath = rootPath +"release_picture.jpg";
        startActivity(new Intent(this, PicSelectTypeDialog.class));
        Intent intent = new Intent(this, PicSelectTypeDialog.class);
        intent.putExtra(PicSelectTypeDialog.IMAGE_PATH, mImagePath);
        intent.putExtra(PicSelectTypeDialog.IMAGE_CROP, true);
        startActivityForResult(intent, REQUEST_SELECT_PIC);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        ULog.i(TAG, "onActivityResult", "resultCode:" + resultCode);
        if (Activity.RESULT_OK != resultCode) {
//            mImagePath = null;
//            ULog.i(TAG, "onActivityResult", "no RESULT_OK");
            return;
        }
        switch (requestCode) {
            case REQUEST_SELECT_PIC:
//                ULog.i(TAG, "onActivityResult", "REQUEST_SELECT_PIC");
                showPicture();
                break;
            case REQUEST_SELECT_TIME:
//                please_select_time.setVisibility(View.GONE);
//                containTime.setVisibility(View.VISIBLE);
//                Bundle bundle = data.getExtras();
//                sdate = bundle.getString("sdate");

//                String[] time = MyTimeUtils.getInstance().times2HMS(MyTimeUtils.getInstance().getStrTime1(sdate));
//                textHour.setText(time[0]);
//                textMinute.setText(time[1]);
////                textHour.setText(bundle.getString("mHour")+"");
////                textMinute.setText(bundle.getString("mMinute")+"");
//                ULog.i(TAG, "onActivityResult", "REQUEST_SELECT_TIME:sdate:" + sdate);
                break;
        }
    }
    //选图片后展示图片
    public void showPicture() {
        ImageView pic;
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        if (bitmap != null) {
            ((ImageView)findViewById(R.id.pic)).setImageBitmap(bitmap);
        }
    }
}
