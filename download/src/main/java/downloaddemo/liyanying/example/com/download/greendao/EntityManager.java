package downloaddemo.liyanying.example.com.download.greendao;

import android.content.Context;

import downloaddemo.liyanying.example.com.download.dao.DownLoadInfo;

public class EntityManager {
    private static EntityManager entityManager;
    public DownLoadInfoDao userDao;

    /**
     * 创建User表实例
     *
     * @return
     */
    public DownLoadInfoDao getUserDao(Context context, String dbName, String dbSavePath) {
        userDao = DaoManager.getInstance(context, dbName, dbSavePath).getSession().getDownLoadInfoDao();
        return userDao;
    }

    /**
     * 创建单例
     *
     * @return
     */
    public static EntityManager getInstance() {
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        return entityManager;
    }

    public static void insert(DownLoadInfoDao downLoadInfoDao, DownLoadInfo downLoadInfo) {
        downLoadInfoDao.insert(downLoadInfo);
    }
}