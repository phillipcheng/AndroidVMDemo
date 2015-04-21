package com.vol.androidvmdemo;

import android.os.StrictMode;

/**
 * Created by weili5 on 2015/3/1.
 */
public class AndroidUtil {

    public static final void removeStrict() {
        String strVer = android.os.Build.VERSION.RELEASE;
        strVer = strVer.substring(0, 3).trim();
        float fv = Float.valueOf(strVer);
        if (fv > 2.3) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .permitAll()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
//                    .penaltyLog() //打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects() //探测SQLite数据库操作
                    .penaltyLog() //打印logcat
                    .penaltyDeath()
                    .build());
        }
    }


}
