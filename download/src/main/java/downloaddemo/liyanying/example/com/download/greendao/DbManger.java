package downloaddemo.liyanying.example.com.download.greendao;

import android.content.Context;
import android.widget.Toast;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import downloaddemo.liyanying.example.com.download.dao.DownLoadInfo;
import downloaddemo.liyanying.example.com.download.greendao.DownLoadInfoDao;
import downloaddemo.liyanying.example.com.download.greendao.EntityManager;

public class DbManger {
    private DownLoadInfoDao downLoadInfoDao;

    public DbManger(Context context, String dbName, String dbSavePath) {
        downLoadInfoDao = EntityManager.getInstance().getUserDao(context, dbName, dbSavePath);

    }

    /**
     * 添加数据
     */
    public long insert(DownLoadInfo downLoadInfo) {
        return downLoadInfoDao.insert(downLoadInfo);
    }

    /**
     * 删除数据
     *
     * @param downLoadInfo
     */
    public void delete(DownLoadInfo downLoadInfo) {
        downLoadInfoDao.delete(downLoadInfo);
    }

    /**
     * 根据down_ID删除数据
     *
     * @param down_ID
     */
    public void deleteDataById(String down_ID) {
        List<DownLoadInfo> listDownLoadInfo = queryDataBydownId(down_ID);
        for (int i = 0; i < listDownLoadInfo.size(); i++) {
            DownLoadInfo downLoadInfo = listDownLoadInfo.get(i);
            downLoadInfoDao.delete(downLoadInfo);
        }
    }

    /**
     * 根据ClassId删除数据
     */
    public void deleteDataByClassId(String ClassId) {
        List<DownLoadInfo> listDownLoadInfo = queryDataByclassId(ClassId);
        for (int i = 0; i < listDownLoadInfo.size(); i++) {
            DownLoadInfo downLoadInfo = listDownLoadInfo.get(i);
            downLoadInfoDao.delete(downLoadInfo);
        }
    }

    /**
     * 根据id更改数据
     */
    public void updateDataById(String downId, String filename, Long totalSize,
                               Long currentSize, String path) {
        List<DownLoadInfo> listDownLoadInfo = queryDataBydownId(downId);
        for (int i = 0; i < listDownLoadInfo.size(); i++) {
            DownLoadInfo findDownInfo = listDownLoadInfo.get(i);
            if (findDownInfo != null) {
                if (filename != null) {
                    findDownInfo.setFilename(filename);
                }
                if (totalSize != null) {
                    findDownInfo.setTotalSize(totalSize);
                }
                if (currentSize != null) {
                    findDownInfo.setCurrentSize(currentSize);
                }
                if (path != null) {
                    findDownInfo.setPath(path);
                }
                downLoadInfoDao.update(findDownInfo);
            }
        }


    }

    /**
     * 根据down_ID查询数据
     */
    public List<DownLoadInfo> queryDataBydownId(String down_ID) {
        QueryBuilder qb = downLoadInfoDao.queryBuilder();
        List<DownLoadInfo> users = qb.where(DownLoadInfoDao.Properties.Down_id.eq(down_ID)).list();

        return users;
    }

    /**
     * 根据down_ID查询数据
     */
    public List<DownLoadInfo> queryDataByclassId(String classId) {
        QueryBuilder qb = downLoadInfoDao.queryBuilder();
        List<DownLoadInfo> users = qb.where(DownLoadInfoDao.Properties.Class_ID.eq(classId)).list();

        return users;
    }
}
