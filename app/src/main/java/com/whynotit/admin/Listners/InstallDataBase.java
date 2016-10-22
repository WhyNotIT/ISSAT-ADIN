package com.whynotit.admin.Listners;

import android.app.Activity;

import com.whynotit.admin.Dialog.DialaogDownloadDataBase;

/**
 * Created by Harzallah on 17/09/2016.
 */
public interface InstallDataBase {
        void onDownloadFinish(Activity activity, DialaogDownloadDataBase dialogDownloadDataBase, String dataBaseDate);

        void onDownloadFailed();
}
