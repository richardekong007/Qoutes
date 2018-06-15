package com.richydave.qoutes.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;

import com.richydave.qoutes.R;

public class FragmentUtil {

    public static void replaceFragment(FragmentManager fManager, Fragment fragment, Bundle args, boolean stackOnBackStack) {

        FragmentTransaction fTransaction;
        if (fragmentManagerIsNotNull(fManager)) {

            fTransaction = fManager.beginTransaction();
            if (fragmentIsNotNull(fragment) && argumentsIsNotNull(args)) {
                fragment.setArguments(args);
            }
            fTransaction.replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName());
            new Handler().post(() -> {
                if (stackOnBackStack) {
                    fTransaction.addToBackStack(null);
                } else {
                    fManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                fTransaction.commitAllowingStateLoss();
            });
        }
    }

    public static void takeOffBackStack(FragmentManager fManager, Fragment fragment) {

        FragmentTransaction fTransaction;
        if (fragmentManagerIsNotNull(fManager) && fragmentIsNotNull(fragment)) {

            fTransaction = fManager.beginTransaction();
            fTransaction.remove(fragment);
            fTransaction.commit();
            fManager.popBackStack();
        }
    }

    private static boolean fragmentManagerIsNotNull(FragmentManager fManager) {
        return fManager != null;
    }

    private static boolean argumentsIsNotNull(Bundle arguments) {
        return arguments != null;
    }

    private static boolean fragmentIsNotNull(Fragment fragment) {
        return fragment != null;
    }
}
