package com.tim.room.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tim.room.R;
import com.tim.room.helper.ColorPicker;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.utils.UploadImage;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;
import static com.tim.room.utils.CommonUtil.containerHeight;
import static com.tim.room.utils.CompressImage.compressImages;

public class AddItemActivity extends AppCompatActivity {
    private final static String TAG = AddItemActivity.class.getSimpleName();
    private final static int INTENT_REQUEST_CATE = 10;
    // 运行sample前需要配置以下字段为有效的值
    private OSS oss;
    public static String endpoint;
    public static String accessKeyId;
    public static String accessKeySecret;
    public static String testBucket;

    public static ImageButton imageBtn;
    EditText edt_brand, edt_title, edt_cate;
    TextView tv_cate_id;
    Button btn_color1, btn_color2, btn_color3;

    private void loadConfiguration() throws PackageManager.NameNotFoundException {
        ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        endpoint = appInfo.metaData.getString("com.alibaba.app.oss_endpoint");
        accessKeyId = appInfo.metaData.getString("com.alibaba.app.oss_ak");
        accessKeySecret = appInfo.metaData.getString("com.alibaba.app.oss_sk");
        testBucket = appInfo.metaData.getString("com.alibaba.app.oss_bucketname");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        configurOSS();
        findView();
        setView();
        setListener();
    }

    private void configurOSS() {
        // 从AndroidManifest.xml文件中读取配置信息
        try {
            this.loadConfiguration();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider, conf);
    }

    private void setView() {
        ViewGroup.LayoutParams params = imageBtn.getLayoutParams();
        params.height = containerHeight(this, 3);
        imageBtn.setLayoutParams(params);
        edt_cate.setFocusable(false);
        edt_cate.setClickable(true);
    }

    private void findView() {
        imageBtn = (ImageButton) findViewById(R.id.btn_add_item_img);
        edt_brand = (EditText) findViewById(R.id.edt_brand);
        edt_title = (EditText) findViewById(R.id.edt_title);
        edt_cate = (EditText) findViewById(R.id.edt_cate);
        tv_cate_id = (TextView) findViewById(R.id.cate_id);
        btn_color1 = (Button) findViewById(R.id.btn_color1);
        btn_color2 = (Button) findViewById(R.id.btn_color2);
        btn_color3 = (Button) findViewById(R.id.btn_color3);
    }

    private void setListener() {
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(AddItemActivity.this);
            }
        });
        edt_cate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddItemActivity.this, ItemCateActivity.class);
                startActivityForResult(intent, INTENT_REQUEST_CATE);
//                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        });
        btn_color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ColorPicker colorPicker = new ColorPicker(AddItemActivity.this);
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        Log.v(TAG, "Color: " + color);
                        btn_color1.setBackgroundColor(color);
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {//Pick image result
                Uri imageUri = CropImage.getPickImageResultUri(this, data);

                Log.v(TAG, "URI: " + imageUri);
                startCropImageActivity(imageUri);
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {//Cropped image result
                final CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageBtn.setImageURI(result.getUri());
                Log.v(TAG, "CROPEDURI: " + result.getUri());
                final String compressImage = compressImages(result.getUri().getPath(), result.getUri().getPath(), 50);
                Log.v(TAG, "COMPRESSEDURI: " + compressImage);
                imageBtn.setTag(compressImage);
            }
            if (requestCode == INTENT_REQUEST_CATE) {
                String cate_name = data.getStringExtra("cate_name");
                String cate_id = data.getStringExtra("cate_id");
                edt_cate.setText(cate_name);
                tv_cate_id.setText(cate_id);
                Log.v(TAG, "cate_id: " + cate_id);
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
//                .setMinCropResultSize(100, 100)
//                .setMaxCropResultSize(1000, 1000)
                .start(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_btn_add_item:
                final String path = imageBtn.getTag().toString();
                final String uploadObject = path.substring(path.lastIndexOf("/") + 1, path.length());
                final String uploadUrl = String.valueOf(session.getUser().getId()) + "/";

                RESTFulService addItemService = RESTFulServiceImp.createService(RESTFulService.class);
                Items itemObject = new Items();
                itemObject.setUserId(session.getUser().getId());
                itemObject.setBrand(edt_brand.getText().toString());
                itemObject.setTitle(edt_title.getText().toString());
                itemObject.setImageName(uploadObject);
                itemObject.setCateId(Integer.valueOf(tv_cate_id.getText().toString()));
                addItemService.addItem(itemObject).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    new UploadImage(oss, testBucket, uploadUrl + uploadObject, path).asyncPutObjectFromLocalFile();
                                }
                            }).start();
                        } else {
                            Log.v(TAG, "ERROR: add item error");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.v(TAG, "Error message: " + throwable.getMessage());
                    }
                });


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
