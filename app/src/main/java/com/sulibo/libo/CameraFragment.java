package com.sulibo.libo;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.otto.Subscribe;
import com.sulibo.FileSystemService;
import com.sulibo.services.ActivityResultBus;
import com.sulibo.services.ActivityResultEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import android.app.Fragment;


import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment {


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {

            return;
        }
        switch (requestCode) {
            //相机返回 CODE_CAMERA_REQUEST
            case CODE_CAMERA_REQUEST:
                 cropImageUri = Uri.fromFile(giveImageName(giveAnameByRadim()));
                   PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                 break;
            //相册返回 CODE_GALLERY_REQUEST
            case CODE_GALLERY_REQUEST:

                if (hasSdcard()) {
                    cropImageUri = Uri.fromFile(giveImageName(giveAnameByRadim()));
                    Uri newUri = Uri.parse(PhotoUtils.getPath(getActivity(), data.getData()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        newUri = FileProvider.getUriForFile(getActivity(), "com.sulibo.libo.fileprovider", new File(newUri.getPath()));
                    }
                      PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                } else {
                    ToastUtils.showShort(getActivity(), "设备没有SD卡！");
                }
                break;
            //裁剪返回
            case CODE_RESULT_REQUEST:
              Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, getActivity());
              if (bitmap != null) {
                   showImages(bitmap);
               }
                break;
            default:
        }


    }


    private String giveAnameByRadim() {
        Date date = new Date();
        return String.valueOf(date.getTime());
    }


    private static final String TAG = "PhotoImageFragment";
    @BindView(R.id.imageView2)
    ImageView photo;
    @BindView(R.id.button2)
    Button takePic;
    @BindView(R.id.button3)
    Button gallery;
    Unbinder unbinder;


    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + ".fileprovider/photo.jpg");

    private File fiORAGE_PE = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");

    private File giveImageName(String imageName) {

        return new File(Environment.getExternalStorageDirectory().getPath() + "/" + imageName + ".jpg");
    }

    private Uri imageUri;
    private Uri cropImageUri;
    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, null, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /* **/
    @OnClick({R.id.button2, R.id.button3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button2:
                autoObtainCameraPermission();
                break;
            case R.id.button3:
                autoObtainStoragePermission();
                break;
            default:
        }
    }


    /**
     * 动态申请sdcard读写权限
     */
    private void autoObtainStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
            }
        } else {
            PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
        }
    }

    /**
     * 申请访问相机权限
     * <p>
     * private void autoObtainCameraPermission() {
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
     * if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
     * || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
     * <p>
     * if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
     * ToastUtils.showShort(getActivity(), "您已经拒绝过一次");
     * }
     * requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
     * } else {//有权限直接调用系统相机拍照
     * if (hasSdcard()) {
     * imageUri = Uri.fromFile(fileUri);
     * //通过FileProvider创建一个content类型的Uri
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
     * imageUri = FileProvider.getUriForFile(this.getActivity(), "com.sulibo.libo.fileprovider", fileUri);
     * }
     * Log.d(TAG, "onRequestPermissionsResult:开始 ");
     * PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
     * } else {
     * ToastUtils.showShort(getActivity(), "设备没有SD卡！");
     * }
     * }
     * }
     * }
     **/
    private String imgPathOri;

    private void autoObtainCameraPermission() {
        imageUri = Uri.fromFile(fileUri);
        //通过FileProvider创建一个content类型的Uri


        File imageFile = null;
        try {
            imageFile = createOriImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        imageUri = FileProvider.getUriForFile(this.getActivity(), "com.sulibo.libo.fileprovider", imageFile);

        PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);

    }


    /**
     * 创建原图像保存的文件
     *
     * @return
     * @throws IOException
     */
    private File createOriImageFile() throws IOException {
        String imgNameOri = "HomePic_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File pictureDirOri = new File(this.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + ".fileprovider/");
        if (!pictureDirOri.exists()) {
            pictureDirOri.mkdirs();
        }
        File image = File.createTempFile(
                imgNameOri,         /* prefix */
                ".jpg",             /* suffix */
                pictureDirOri       /* directory */
        );
        imgPathOri = image.getAbsolutePath();
        return image;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult:开始 ");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "requestCode " + requestCode);
        switch (requestCode) {


            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (hasSdcard()) {
                        imageUri = Uri.fromFile(fileUri);
                        Log.d(TAG, "imageUri " + imageUri.toString());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //通过FileProvider创建一个content类型的Uri
                            imageUri = FileProvider.getUriForFile(getActivity(), "com.sulibo.libo", fileUri);
                        }
                        PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        ToastUtils.showShort(getActivity(), "设备没有SD卡！");
                    }
                } else {
                    Log.d(TAG, "grantResults.length " + grantResults.length);
                    Log.d(TAG, "grantResults[0]:  " + grantResults[0]);
                    Log.d(TAG, "grantResults[1]:  " + grantResults[1]);
                    Log.d(TAG, "CAMERA_PERMISSIONS_REQUEST_CODE:  " + CAMERA_PERMISSIONS_REQUEST_CODE);
                    Log.d(TAG, "PackageManager.PERMISSION_GRANTED:  " + PackageManager.PERMISSION_GRANTED);
                    ToastUtils.showShort(getActivity(), "请允许打开相机1！！");
                }
                break;
            }
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {
                    ToastUtils.showShort(getActivity(), "请允许打操作SDCard！！");
                }
                break;
            default:
        }
    }


    private void showImages(Bitmap bitmap) {
        photo.setImageBitmap(bitmap);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


}