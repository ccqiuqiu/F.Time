package com.ccqiuqiu.ftime.Utils;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * Created by cc on 2015/11/24.
 */
public class DbUtils {
    public static DbManager.DaoConfig getDaoConfig(){
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("ftime.db")
                //.setDbDir()
                .setDbVersion(1)
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        //
                    }
                });
        return daoConfig;
    }

    public static DbManager getDbManager(){
        DbManager db = x.getDb(DbUtils.getDaoConfig());
        return db;
    }
}
