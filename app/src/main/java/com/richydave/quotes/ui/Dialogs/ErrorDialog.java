package com.richydave.quotes.ui.Dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.richydave.quotes.R;

public class ErrorDialog extends BaseDialog {

    private static final int ICON = R.drawable.ic_error;

    public ErrorDialog(Context context, String title, String message) {
        super(context,title,message);
    }

    @Override
    public AlertDialog.Builder build() {
        return new AlertDialog.Builder(getContext())
                .setTitle(getTittle())
                .setIcon(ICON)
                .setMessage(getMessage());
    }
}
