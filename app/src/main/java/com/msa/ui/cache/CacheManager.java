package com.msa.ui.cache;

import android.content.Context;

import java.io.File;



public class CacheManager {

    private Context mContext;

    public CacheManager(Context context){
        mContext = context;
    }

    public void deleteCache() {
        try {
            File dir = mContext.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    private Boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                Boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
