package com.richydave.quotes.ui.AlertDialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.richydave.quotes.R;

public class InformationDialog extends BaseAlertDialog {
    private static final int ICON = R.drawable.ic_info;
    public InformationDialog(Context context, String tittle, String message) {
        super(context, tittle, message);
    }

    @Override
    public AlertDialog.Builder build() {
        return new AlertDialog.Builder(getContext())
                .setTitle(getTittle())
                .setIcon(ICON)
                .setMessage(getMessage());
    }
}
