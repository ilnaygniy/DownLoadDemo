package downloaddemo.liyanying.example.com.download.greendao;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * greenDao管理类
 */
public class DaoManager {
    private static DaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private DaoManager(Context context, String dbName, String dbSavePath) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(new GreenDaoContext(context, dbSavePath), dbName, null);
        DaoMaster mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public static DaoManager getInstance(Context context, String dbName, String dbSavePath) {
        if (mInstance == null) {
            mInstance = new DaoManager(context, dbName, dbSavePath);
        }
        return mInstance;
    }
}