package com.disciplesbay.latterhousehq.mychurch.fragment;


import com.disciplesbay.latterhousehq.mychurch.download.DownloadManager;
import com.disciplesbay.latterhousehq.mychurch.download.DownloadManagerService;

public class AllMissionsFragment extends MissionsFragment {

    @Override
    protected DownloadManager setupDownloadManager(DownloadManagerService.DMBinder binder) {
        return binder.getDownloadManager();
    }
}
