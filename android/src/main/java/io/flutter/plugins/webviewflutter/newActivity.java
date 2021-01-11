package io.flutter.plugins.webviewflutter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;

import androidx.annotation.Nullable;

public class newActivity extends Activity {
    private static ValueCallback<Uri[]> mUploadMessageArray;

    public static void getfilePathCallback(ValueCallback<Uri[]> filePathCallback){
        mUploadMessageArray = filePathCallback;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        showBottomDialog();

//        AlertDialog.Builder builder = new AlertDialog.Builder(newActivity.this,R.layout.dialog);
//        builder.setTitle("请选择");
//        final String[] sex = {"打开相册", "打开相机", "未知操作"};
//        builder.setItems(sex, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case 0:
//                        openAblum();
//                        break;
//                    case 1:
//                        openCarem();
//                        break;
//                    case 2:
//                        Toast.makeText(newActivity.this, "未知操作", Toast.LENGTH_SHORT).show();
//                        onActivityResult(1,1,null);
//                        break;
//                    default:
//                        finish();
//                        break;
//                }
//            }
//
//        });
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                Log.i("TAG","SetOnCancel");
//                onActivityResult(1,1,null);
//            }
//        });
//        builder.show();
    }

    private void openAblum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//任意类型文件
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }

    private void openCarem(){
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //系统常量， 启动相机的关键
        startActivityForResult(openCameraIntent, 2); // 参数常量为自定义的request code, 在取返回结果时有用
    }

    private void showBottomDialog(){
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.dialog_custom_layout,null);
        dialog.setContentView(view);
        //点击其他空白处，退出dialog。
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //这样可以使返回值为null。
                onActivityResult(1,1,null);
            }
        });
        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();

        dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openCarem();
            }
        });

        dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openAblum();
            }
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onActivityResult(1,1,null);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //防止退出时，data没有数据，导致闪退。
        Log.i("TAG","forResult");
        if(data != null){
            Uri uri = data.getData();
            Log.i("TAG","! "+data.getClass()+" * "+data);
            Log.i("TAG","URi "+uri);

            if(uri==null){
                //好像时部分机型会出现的问题，我的mix3就遇到了。
                //拍照返回的时候uri为空，但是data里有inline-data。
                Log.i("TAG", String.valueOf(data));
                Bundle bundle = data.getExtras();
                try {
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                    Uri[] results = new Uri[]{uri};
                    mUploadMessageArray.onReceiveValue(results);
                }catch (Exception e){
                    //当不拍照返回相机时，获取到uri也没数据。
                    mUploadMessageArray.onReceiveValue(null);
                }
            }else{
                Uri[] results = new Uri[]{uri};
                mUploadMessageArray.onReceiveValue(results);
            }

        }else{
            Log.i("TAG","onReceveValue");
            mUploadMessageArray.onReceiveValue(null);
        }
        finish();
    }


}
