package com.sulibo.libo.entity;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 水明 on 2018/8/7.
 */
@Entity
public class ImageFile  {
    @org.greenrobot.greendao.annotation.Id(autoincrement = true)
    @NotNull
    private Long id;
    private boolean isUploaded;
    private String fileName;
    private String createDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsUploaded() {
        return this.isUploaded;
    }

    public void setIsUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }

    private String  notice;

    @Generated(hash = 136480442)
    public ImageFile(@NotNull Long id, boolean isUploaded, String fileName,
            String createDate, String notice) {
        this.id = id;
        this.isUploaded = isUploaded;
        this.fileName = fileName;
        this.createDate = createDate;
        this.notice = notice;
    }

    @Generated(hash = 586844562)
    public ImageFile() {
    }


}
