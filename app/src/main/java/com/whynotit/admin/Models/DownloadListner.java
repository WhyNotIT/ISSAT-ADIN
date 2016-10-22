package com.whynotit.admin.Models;

/**
 * Created by Harzallah on 17/09/2016.
 */
public interface DownloadListner {
    void onDataBaseIsUpToDate();
    void onDownloadFinish();
    void onInstallationFinish(boolean succes);
}
