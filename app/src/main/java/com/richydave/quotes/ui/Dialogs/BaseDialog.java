package com.richydave.quotes.ui.Dialogs;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Context;


abstract class BaseDialog {

    private Context context;

    private String tittle;

    private String message;

    public BaseDialog(Context context, String tittle, String message) {
        this.context = context;
        this.tittle = tittle;
        this.message = message;

    }

    public String getTittle() {
        return tittle;
    }

    public String getMessage() {
        return message;
    }

    public Context getContext() {
        return context;
    }

    abstract AlertDialog.Builder build();
}
