package com.cxsj.runhdu.model.gson;

/**
 * Created by SailFlorve on 2017/5/26 0026.
 * 更新信息Json实体类
 */

public class UpdateInfo {
    private boolean isUpdate;
    private String latestVersion;
    private String currentVersion;
    private String statement;

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }
}
