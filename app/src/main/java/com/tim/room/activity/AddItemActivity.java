package com.tim.room.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.tim.room.R;
import com.tim.room.model.Items;
import com.tim.room.model.YouDaoTrans;
import com.tim.room.model.imageRequest;
import com.tim.room.model.imageResultResponse;
import com.tim.room.model.imageSendResponse;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.utils.ImageUtils;
import com.tim.room.utils.LocaleUtil;
import com.tim.room.utils.UploadImage;
import com.tim.room.view.LoadingProgressDialog;
import com.tim.room.view.TagContainerLayout;
import com.tim.room.view.TagView;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;
import static com.tim.room.app.AppConfig.IMG_BASE_URL;
import static com.tim.room.utils.CommonUtil.containerHeight;

public class AddItemActivity extends AppCompatActivity implements ImageUtils.ImageAttachmentListener {
    private final static String TAG = AddItemActivity.class.getSimpleName();
    private final static int INTENT_REQUEST_CATE = 10;
    // 运行sample前需要配置以下字段为有效的值
    private OSS oss;
    public static String endpoint;
    public static String accessKeyId;
    public static String accessKeySecret;
    public static String testBucket;

    Context mContext;
    EditText edt_brand, edt_title, edt_cate, edt_date, edt_exp_date, edt_tag, edt_season;
    Switch st_global;
    TagContainerLayout mTagContainerLayout;
    TextView tv_cate_id;
    Button btn_try;
    Button btn_color1, btn_color2, btn_color3;
    ImageButton btn_add_tag;
    ScrollView scrollView;
    private int mYear, mMonth, mDay;
    SimpleDateFormat sdf;
    ImageUtils imageutils;
    public static ImageView imageAdd;
    private Bitmap bitmap;
    private String file_name;
    UploadImage uploadImage;

    //避免重复上传
    boolean isUpload;
    String RecToken;
    RESTFulService imageAIService;

    public static LoadingProgressDialog dialog;

    String[] seasonArray = {"Spring/Fall", "Summer", "Winter"};
    boolean[] flags;

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
        this.mContext = getApplicationContext();

        dialog = new LoadingProgressDialog(this);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        configurOSS();
        findView();
        setView();
        setListener();
        sdf = new SimpleDateFormat("yyyy-MMM-dd");
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
        imageutils = new ImageUtils(this);
        ViewGroup.LayoutParams params = imageAdd.getLayoutParams();
        params.height = containerHeight(this, 2);
        imageAdd.setLayoutParams(params);
        edt_cate.setFocusable(false);
        edt_cate.setClickable(true);
        edt_date.setFocusable(false);
        edt_date.setClickable(true);
        edt_exp_date.setFocusable(false);
        edt_exp_date.setClickable(true);
        edt_season.setFocusable(false);
        edt_season.setClickable(true);
        isUpload = false;
        RecToken = "";
    }

    private void findView() {
        imageAdd = (ImageView) findViewById(R.id.add_item_img);
        edt_brand = (EditText) findViewById(R.id.edt_brand);
        edt_title = (EditText) findViewById(R.id.edt_title);
        edt_cate = (EditText) findViewById(R.id.edt_cate);
        tv_cate_id = (TextView) findViewById(R.id.cate_id);
        btn_color1 = (Button) findViewById(R.id.btn_color1);
        btn_color2 = (Button) findViewById(R.id.btn_color2);
        btn_color3 = (Button) findViewById(R.id.btn_color3);
        edt_date = (EditText) findViewById(R.id.edt_date);
        edt_exp_date = (EditText) findViewById(R.id.edt_exp_date);
        st_global = (Switch) findViewById(R.id.st_global);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        mTagContainerLayout = (TagContainerLayout) findViewById(R.id.tagcontainerLayout);
        btn_add_tag = (ImageButton) findViewById(R.id.btn_add_tag);
        edt_tag = (EditText) findViewById(R.id.edt_tag);
        edt_season = (EditText) findViewById(R.id.edt_season);
        btn_try = (Button) findViewById(R.id.btn_try);
    }

    private void setListener() {
        btn_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageAdd.getTag() != null) {
//                    dialog.show();
                    edt_title.setText("");
                    final String path = imageAdd.getTag().toString();
                    final String uploadObject = path.substring(path.lastIndexOf("/") + 1, path.length());
                    final String uploadUrl = String.valueOf(session.getUser().getId()) + "/";
                    //**************<Image AI start>*********************
                    final ProgressDialog progressBar = new ProgressDialog(AddItemActivity.this);
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Recognition in progress ...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();

                    if (!isUpload) {
                        PutObjectRequest put = new PutObjectRequest(testBucket, uploadUrl + uploadObject, path);
                        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

                            @Override
                            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                                imageAIService = RESTFulServiceImp.createCloudSightService(RESTFulService.class);
                                imageRequest request = new imageRequest();
                                Log.v("wxl", "requestURL: " + IMG_BASE_URL + uploadUrl + uploadObject);
                                progressBar.setProgress(20);
                                //+ "?x-oss-process=image/resize,m_lfit,h_150,w_150/format,png"
//                            getSupportActionBar().setTitle("Recognition in progress...");

                                request.setImage_request(new imageRequest.ImageRequestBean("en", IMG_BASE_URL + uploadUrl + uploadObject + "?x-oss-process=image/resize,m_lfit,h_150,w_150/format,png"));
                                imageAIService.sendImage(request)
                                        .subscribeOn(Schedulers.io())//在IO线程请求执行
                                        .observeOn(AndroidSchedulers.mainThread())//回到主线程去处理请求结果
                                        .subscribe(new Consumer<imageSendResponse>() {
                                            @Override
                                            public void accept(imageSendResponse imageSendResponse) throws Exception {
                                                Log.v("wxl", "request1: " + imageSendResponse.getStatus());
                                                Log.v("wxl", "request1: " + imageSendResponse.getUrl());
                                                Log.v("wxl", "request1: " + imageSendResponse.getToken());
                                                final String token = imageSendResponse.getToken();
                                                if (!token.isEmpty()) {
                                                    new CountDownTimer(10 * 1000, 1000) {//等待10秒
                                                        @Override
                                                        public void onTick(long millisUntilFinished) {
                                                            progressBar.setProgress(20 + (int) (10000 - millisUntilFinished) / 150);
                                                        }

                                                        @Override
                                                        public void onFinish() {
                                                            imageAIService.imageResponse(token)
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe(new Consumer<imageResultResponse>() {
                                                                        @Override
                                                                        public void accept(imageResultResponse imageResultResponse) throws Exception {
                                                                            Log.v("wxl", "request2: " + imageResultResponse.getStatus());
                                                                            Log.v("wxl", "request2: " + imageResultResponse.getUrl());
                                                                            Log.v("wxl", "request2: " + imageResultResponse.getToken());
                                                                            Log.v("wxl", "request2: " + imageResultResponse.getName());
                                                                            if (imageResultResponse.getStatus().equals("completed")) {
                                                                                if (LocaleUtil.getLocale(mContext.getApplicationContext()).equals(LocaleUtil.SIMP_CHINESE)) {
                                                                                    RESTFulService transService = RESTFulServiceImp.createTranslateService(RESTFulService.class);
                                                                                    transService.transWord(imageResultResponse.getName())
                                                                                            .subscribeOn(Schedulers.io())
                                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                                            .subscribe(new Consumer<YouDaoTrans>() {
                                                                                                @Override
                                                                                                public void accept(YouDaoTrans youDaoTrans) throws Exception {
                                                                                                    edt_title.setText(youDaoTrans.getTranslation().get(0));
                                                                                                    RecToken = "";
                                                                                                    isUpload = false;
                                                                                                }
                                                                                            });
                                                                                } else {
                                                                                    edt_title.setText(imageResultResponse.getName());
                                                                                    RecToken = "";
                                                                                    isUpload = false;
                                                                                }
                                                                            } else {
                                                                                isUpload = true;
                                                                                RecToken = imageResultResponse.getToken();
                                                                                Toast.makeText(mContext, "System busy, Try again", Toast.LENGTH_LONG).show();

                                                                            }
                                                                            progressBar.dismiss();
                                                                        }
                                                                    });
                                                        }
                                                    }.start();
                                                }
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                RecToken = "";
                                                isUpload = false;
                                                Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_LONG).show();
                                                progressBar.dismiss();
                                            }
                                        });
                            }

                            @Override
                            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {

                            }
                        });
                    } else {
                        imageAIService.imageResponse(RecToken)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<imageResultResponse>() {
                                    @Override
                                    public void accept(imageResultResponse imageResultResponse) throws Exception {
                                        Log.v("wxl", "request2: " + imageResultResponse.getStatus());
                                        Log.v("wxl", "request2: " + imageResultResponse.getUrl());
                                        Log.v("wxl", "request2: " + imageResultResponse.getToken());
                                        Log.v("wxl", "request2: " + imageResultResponse.getName());
                                        progressBar.setProgress(100);
                                        if (imageResultResponse.getStatus().equals("completed")) {
                                            if (LocaleUtil.getLocale(mContext.getApplicationContext()).equals(LocaleUtil.SIMP_CHINESE)) {
                                                RESTFulService transService = RESTFulServiceImp.createTranslateService(RESTFulService.class);
                                                transService.transWord(imageResultResponse.getName())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new Consumer<YouDaoTrans>() {
                                                            @Override
                                                            public void accept(YouDaoTrans youDaoTrans) throws Exception {
                                                                edt_title.setText(youDaoTrans.getTranslation().get(0));
                                                                RecToken = "";
                                                                isUpload = false;
                                                            }
                                                        });
                                            } else {
                                                edt_title.setText(imageResultResponse.getName());
                                                RecToken = "";
                                                isUpload = false;
                                            }
                                        } else {
                                            RecToken = imageResultResponse.getToken();
                                            Toast.makeText(mContext, "System busy, Try again", Toast.LENGTH_LONG).show();
                                        }
                                        progressBar.dismiss();
                                    }
                                });
                    }//**************<Image AI end>*********************
                }
            }
        });
        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageutils.imagepicker(1);
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
        btn_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_tag.getText().toString().trim().equals("")) {
                    mTagContainerLayout.addTag(edt_tag.getText().toString());
                    edt_tag.setText("");
                }
            }
        });
        mTagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(final int position, String text) {
                AlertDialog dialog = new AlertDialog.Builder(AddItemActivity.this)
                        .setTitle("long click")
                        .setMessage("You will delete this tag!")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTagContainerLayout.removeTag(position);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
        edt_exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(AddItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Date date = setDateFormat(year, month, day);
                        edt_exp_date.setText(date.toString());
                    }

                }, mYear, mMonth, mDay).show();
            }
        });
        edt_season.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedSeason = edt_season.getText().toString().trim();
                flags = new boolean[]{false, false, false};
                if (!selectedSeason.isEmpty()) {
                    String[] tempSeasonArray = selectedSeason.split(",");
                    for (int i = 0; i < tempSeasonArray.length; i++) {
                        for (int j = 0; j < seasonArray.length; j++) {
                            if (tempSeasonArray[i].equals(seasonArray[j])) {
                                flags[j] = true;
                            }
                        }
                    }
                }
                new AlertDialog.Builder(AddItemActivity.this)
                        .setTitle("Select season")
                        .setIcon(R.drawable.item_season)
                        .setMultiChoiceItems(seasonArray, flags, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which, boolean b) {
                                flags[which] = b;
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                String result = "";
                                for (int i = 0; i < flags.length; i++) {
                                    if (flags[i]) {
                                        result = result + seasonArray[i] + ",";
                                    }
                                }
                                if (!result.isEmpty()) {
                                    edt_season.setText(result.substring(0, result.length() - 1));
                                } else {
                                    edt_season.setText(result);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
//        edt_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final Calendar c = Calendar.getInstance();
//                mYear = c.get(Calendar.YEAR);
//                mMonth = c.get(Calendar.MONTH);
//                mDay = c.get(Calendar.DAY_OF_MONTH);
//                new DatePickerDialog(AddItemActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int day) {
//                        Date date = setDateFormat(year, month, day);
//                        edt_date.setText(date.toString());
//                    }
//
//                }, mYear, mMonth, mDay).show();
//            }
//        });
//

//        btn_color1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final ColorPicker colorPicker = new ColorPicker(AddItemActivity.this);
//                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
//                    @Override
//                    public void onChooseColor(int position, int color) {
//                        Log.v(TAG, "Color: " + color);
//                        btn_color1.setBackgroundColor(color);
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                }).show();
//            }
//        });
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
                if (!checkUpload()) {
                    Toast.makeText(AddItemActivity.this, "Please check your input", Toast.LENGTH_SHORT).show();
                } else {
                    final String path = imageAdd.getTag().toString();
                    final String uploadObject = path.substring(path.lastIndexOf("/") + 1, path.length());
                    final String uploadUrl = String.valueOf(session.getUser().getId()) + "/";

                    RESTFulService addItemService = RESTFulServiceImp.createService(RESTFulService.class);
                    Items itemObject = new Items();
                    itemObject.setUser(session.getUser());
//                    itemObject.setBrand(edt_brand.getText().toString());
                    itemObject.setTitle(edt_title.getText().toString());
//                    itemObject.setDate(edt_date.getText().toString());
                    itemObject.setDate(null);
                    if (edt_exp_date.getText().toString().isEmpty()) {
                        itemObject.setExpDate(null);
                    } else {
                        itemObject.setExpDate(edt_exp_date.getText().toString());
                    }

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String nowDatetime = df.format(Calendar.getInstance().getTime());
                    itemObject.setCreated(nowDatetime);
                    itemObject.setTags(mTagContainerLayout.getTags());
                    if (!edt_season.getText().toString().isEmpty()) {
                        String[] tempSeasonArray = edt_season.getText().toString().split(",");
                        itemObject.setSeasons(Arrays.asList(tempSeasonArray));
                    }
                    if (st_global.isChecked()) {
                        itemObject.setGlobal("1");
                    } else {
                        itemObject.setGlobal("0");
                    }
                    itemObject.setImageName(uploadObject);
                    itemObject.setCateId(Integer.valueOf(tv_cate_id.getText().toString()));
                    uploadImage = new UploadImage(oss, testBucket, uploadUrl + uploadObject, path);
                    addItemService.addItem(itemObject).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        uploadImage.asyncPutObjectFromLocalFile();
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
            case android.R.id.home:
                setResult(RESULT_OK); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean checkUpload() {
        Boolean result = true;
        if (imageAdd.getTag() == null || edt_title.getText().toString().trim().isEmpty() || tv_cate_id.getText().toString().trim().isEmpty()) {
            result = false;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_REQUEST_CATE) {
                String cate_name = data.getStringExtra("cate_name");
                String cate_id = data.getStringExtra("cate_id");
                edt_cate.setText(cate_name);
                tv_cate_id.setText(cate_id);
                Log.v(TAG, "cate_id: " + cate_id);
            } else {
                imageutils.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap = file;
        this.file_name = filename;
        Log.v(TAG, "bitmap: " + file.toString());
        Log.v(TAG, "Uri: " + uri.toString());
        Log.v(TAG, "UriPath: " + uri.getPath());
        Log.v(TAG, "filename: " + filename.toString());
        imageAdd.setImageBitmap(file);

        String path = Environment.getExternalStorageDirectory() + File.separator + "RoomImages" + File.separator;
        String file_path = imageutils.createImage(file, filename, path, false);
        imageAdd.setTag(file_path);
        //init upload var
        isUpload = false;
        RecToken = "";
    }

    private Date setDateFormat(int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime());
        return sqlDate;
    }
    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {//Pick image result
//                Uri imageUri = CropImage.getPickImageResultUri(this, data);
//
//                Log.v(TAG, "URI: " + imageUri);
//                startCropImageActivity(imageUri);
//            }
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {//Cropped image result
//                final CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                imageBtn.setImageURI(result.getUri());
//                Log.v(TAG, "CROPEDURI: " + result.getUri());
//                final String compressImage = compressImages(result.getUri().getPath(), result.getUri().getPath(), 50);
//                Log.v(TAG, "COMPRESSEDURI: " + compressImage);
//                imageBtn.setTag(compressImage);
//            }
//            if (requestCode == INTENT_REQUEST_CATE) {
//                String cate_name = data.getStringExtra("cate_name");
//                String cate_id = data.getStringExtra("cate_id");
//                edt_cate.setText(cate_name);
//                tv_cate_id.setText(cate_id);
//                Log.v(TAG, "cate_id: " + cate_id);
//            }
//        }
//    }

}
