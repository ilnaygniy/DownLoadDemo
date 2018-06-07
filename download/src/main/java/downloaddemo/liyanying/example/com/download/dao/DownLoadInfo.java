package downloaddemo.liyanying.example.com.download.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class DownLoadInfo {
    @Id
    private Long id;
    private String class_ID;
    private String down_id;
    private String filename;
    private Long totalSize;
    private Long currentSize;
    private String path;
    @Generated(hash = 302019153)
    public DownLoadInfo(Long id, String class_ID, String down_id, String filename,
            Long totalSize, Long currentSize, String path) {
        this.id = id;
        this.class_ID = class_ID;
        this.down_id = down_id;
        this.filename = filename;
        this.totalSize = totalSize;
        this.currentSize = currentSize;
        this.path = path;
    }
    @Generated(hash = 1743687477)
    public DownLoadInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDown_id() {
        return this.down_id;
    }
    public void setDown_id(String down_id) {
        this.down_id = down_id;
    }
    public String getFilename() {
        return this.filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public long getTotalSize() {
        return this.totalSize;
    }
    public long getCurrentSize() {
        return this.currentSize;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
    public void setCurrentSize(Long currentSize) {
        this.currentSize = currentSize;
    }
    public String getClass_ID() {
        return this.class_ID;
    }
    public void setClass_ID(String class_ID) {
        this.class_ID = class_ID;
    }
}
