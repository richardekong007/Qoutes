package com.richydave.quotes.ui.menu;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CustomPopupMenu {

    private PopupMenu popupMenu;

    private Context context;

    private View view;

    private int resourceMenu;

    public CustomPopupMenu(Context context, View view, int menuRes) {
        this.context = context;
        this.view = view;
        this.resourceMenu = menuRes;
    }

    public PopupMenu createMenu() {
        popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(resourceMenu);
        forcefullySetMenuIcons(popupMenu);
        popupMenu.show();
        return popupMenu;
    }

    private void forcefullySetMenuIcons(PopupMenu menu) {
        try {
            Field[] fields = menu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(menu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
