package downloaddemo.liyanying.example.com.downloaddemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import downloaddemo.liyanying.example.com.download.dao.DownLoadInfo;
import downloaddemo.liyanying.example.com.download.greendao.DbManger;
import downloaddemo.liyanying.example.com.download.greendao.DownLoadInfoDao;
import downloaddemo.liyanying.example.com.download.greendao.EntityManager;
import downloaddemo.liyanying.example.com.download.listners.DownloadListner;
import downloaddemo.liyanying.example.com.download.DownloadManager;
import downloaddemo.liyanying.example.com.download.listners.ParserXMLListener;
import downloaddemo.liyanying.example.com.download.xmlutils.XmlPull;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private TextView onProgress_tv, analysisTime, runningTv;
    private Button start, pause, cancel, addDownrask, addbddata,deleteData,changeData;
    private String downLoadPath = "https://dl.google.com/dl/android/studio/install/3.0.1.0/android-studio-ide-171.4443003-windows.exe";

    private String downLoadPath360 = "https://dl.360safe.com/360sd/360sd_x64_std_5.0.0.8120A.exe";

    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1" + File.separator + "75m.new.xml";
    private String filePathTo = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1" + File.separator;
    private DownloadListner downloadListner;
    private DownloadManager downloadManager;
    private Context context;
    private DbManger dbManger;
    private ParserXMLListener parserXMLListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        verifyStoragePermissions(MainActivity.this);
        initView();
        //初始化数据库
        dbManger = new DbManger(context, "ceshi.db", filePathTo);
    }


    private long onTotalSize = 0L;

    private void initView() {
        onProgress_tv = findViewById(R.id.onProgress_tv);
        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        cancel = findViewById(R.id.cancel);
        addDownrask = findViewById(R.id.addDownrask);
        analysisTime = findViewById(R.id.analysisTime);
        runningTv = findViewById(R.id.runningTv);
        addbddata = findViewById(R.id.addbddata);
        deleteData=findViewById(R.id.deleteData);
        changeData=findViewById(R.id.changeData);
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        cancel.setOnClickListener(this);
        addDownrask.setOnClickListener(this);
        addbddata.setOnClickListener(this);
        deleteData.setOnClickListener(this);
        changeData.setOnClickListener(this);
        downloadListner = new DownloadListner() {
            @Override
            public void onTotalSize(long totalSize) {
                Log.i("onTotalSize", "totalSize  = " + totalSize + "");
                onTotalSize = totalSize;
            }

            @Override
            public void onCurrentSize(long currentSize) {
                Log.i("onTotalSize", "currentSize  = " + currentSize + "");
                if (onTotalSize != 0L) {
                    double precent = ((double) currentSize / (double) onTotalSize);
                    onProgress_tv.setText(precent + "");
                }
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onError(int errorCode) {

                Toast.makeText(context,"错误" ,Toast.LENGTH_SHORT ).show();
            }
        };
        downloadManager = DownloadManager.getInstance(downLoadPath360, filePathTo, "测试现在22", downloadListner);

        parserXMLListener=new ParserXMLListener() {
            @Override
            public void statrParser() {
                isRun = true;
                TIME = 0;
                startRun();
            }

            @Override
            public void endParser() {
                isRun = false;
            }

            @Override
            public void Error(int errorCode) {

            }
        };

    }

    private String path_saveMap = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1" + File.separator + "sina" + File.separator;

    private int down_id=0;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                downloadManager.startDownload();
                break;
            case R.id.pause:
                downloadManager.pause();
                break;
            case R.id.cancel:
                downloadManager.cancel();
                break;
            case R.id.addDownrask:
                final XmlPull testXmlPull = new XmlPull(filePath, path_saveMap, "963147");
//                RunTime(testXmlPull);
                testXmlPull.parserXMLThread(parserXMLListener);
                break;
            case R.id.addbddata:
                down_id++;
                String ceshi="path";
                DownLoadInfo downLoadInfo = new DownLoadInfo(null,"223", String.valueOf(down_id), "filename", 6516156L, 254653L, ceshi);
                dbManger.insert(downLoadInfo);
                break;
            case R.id.deleteData:
//                dbManger.deleteDataById("3");
                dbManger.deleteDataByClassId("223");
                break;
            case R.id.changeData:
                dbManger.updateDataById("1", null, 0L, 0L, null);
                break;
            default:
                break;
        }
    }


    Handler handler = new Handler();
    private int TIME = 0;
    private boolean isRun;

    private void startRun() {
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isRun) {
                    TIME++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (analysisTime != null) {
                                analysisTime.setText("正在解析  " + TIME + "");
                            }

                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (analysisTime != null) {
                                analysisTime.setText("已完成  时间为：" + TIME);
                            }

                        }
                    });
                    timer.cancel();
                }

            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }


    /**
     * 根据毫秒返回时分秒
     *
     * @param time
     * @return
     */
    public static String getFormatHMS(long time) {
        time = time / 1000;//总秒数
        int s = (int) (time % 60);//秒
        int m = (int) (time / 60);//分
        int h = (int) (time / 3600);//秒
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
