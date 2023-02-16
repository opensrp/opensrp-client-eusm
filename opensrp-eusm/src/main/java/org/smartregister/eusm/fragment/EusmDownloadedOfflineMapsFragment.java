package org.smartregister.eusm.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import org.smartregister.tasking.fragment.DownloadedOfflineMapsFragment;
import org.smartregister.tasking.presenter.DownloadedOfflineMapsPresenter;

public class EusmDownloadedOfflineMapsFragment extends DownloadedOfflineMapsFragment {

    public static EusmDownloadedOfflineMapsFragment newInstance(Bundle bundle, @NonNull Context context) {
        EusmDownloadedOfflineMapsFragment fragment = new EusmDownloadedOfflineMapsFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        fragment.setPresenter(new DownloadedOfflineMapsPresenter(fragment, context));
        return fragment;
    }
    
    @Override
    public void displayToast(String message) {
        // Do nothing
    }

    @Override
    public void displayError(int title, String message) {
        // Do nothing
    }

}
