package downloaddemo.liyanying.example.com.download.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import downloaddemo.liyanying.example.com.download.greendao.DaoMaster;
import downloaddemo.liyanying.example.com.download.greendao.DownLoadInfoDao;


public class DownLoadOpenHelper extends DaoMaster.OpenHelper {

    public DownLoadOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * 数据库升级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //操作数据库的更新
        MigrationHelper.migrate(db,DownLoadInfoDao.class);
    }

}
