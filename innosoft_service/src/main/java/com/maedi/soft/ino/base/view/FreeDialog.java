/*
 * Copyright (c) 9/11/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maedi.soft.ino.base.utils.PixelCalc;

public class FreeDialog<T> extends Dialog {

    private FragmentActivity f;

    private final String dialogTag = "DIALOG";

    private static boolean isCancelable;

    private static boolean isCancelableTouchOutside;

    private static boolean isDefaultButtonShow;

    private static boolean isCancelButtonShow;

    private static boolean showTransparentDarkBackground;

    private static WindowSize windowSize;

    private static int[] viewSize;

    private static Layout layout;

    private static int customLayout;

    private static String textPositifButton;

    private static String textNegativeButton;

    private static String bodyText;

    private static int icon;

    private static int styleAnimation;

    public interface CommFreeDialogListener
    {
        void setView(FragmentActivity f, View view, FreeDialog dialog);
    }

    private static CommFreeDialogListener listener;

    public interface CommFreeDialogActionButtonListener
    {
        void ok();

        void cancel();

        void clickIcon(FreeDialog dialog);
    }

    private static CommFreeDialogActionButtonListener buttonListener;

    public enum WindowSize {
        FULL_SIZE,
        WRAP_CONTENT,
        CUSTOM_SIZE;
    }

    public enum Layout {
        DEFAULT,
        CUSTOM_LAYOUT;
    }

    //private int heightDialogWindow;

    private static FreeDialog freeDialog;

    public static class BuilderFreeDialog{

        private WindowSize windowSize = WindowSize.WRAP_CONTENT;

        private Layout layout = Layout.DEFAULT;

        private int[] viewSize = new int[]{0, 0};

        private boolean cancelable = true;

        private boolean cancelableTouchOutside = true;

        private boolean defaultButtonShow = true;

        private boolean cancelButtonShow = true;

        private boolean showTransparentDarkBackground = true;

        private int customLayout = 0;

        private String textPositifButton;

        private String textNegativeButton;

        private String bodyText;

        private int icon = 0;

        private int styleAnimation = com.maedi.soft.ino.R.style.DialogDefaultAnimation;

        private CommFreeDialogListener listener;

        private CommFreeDialogActionButtonListener buttonListener;

        public BuilderFreeDialog setWindowSize(WindowSize windowSize)
        {
            this.windowSize = windowSize;
            return this;
        }

        public BuilderFreeDialog setViewSize(int[] viewSize)
        {
            this.viewSize = viewSize;
            return this;
        }

        public BuilderFreeDialog setCancelable(boolean cancelable)
        {
            this.cancelable = cancelable;
            return this;
        }

        public BuilderFreeDialog setCancelableOnTouchOutside(boolean cancelableTouchOutside)
        {
            this.cancelableTouchOutside = cancelableTouchOutside;
            return this;
        }

        public BuilderFreeDialog setShowDefaultButton(boolean defaultButtonShow)
        {
            this.defaultButtonShow = defaultButtonShow;
            return this;
        }

        public BuilderFreeDialog setShowCancelButton(boolean cancelButtonShow)
        {
            this.cancelButtonShow = cancelButtonShow;
            return this;
        }

        public BuilderFreeDialog setLayout(Layout layout)
        {
            this.layout = layout;
            return this;
        }

        public BuilderFreeDialog setCustomLayout(int customLayout)
        {
            this.customLayout = customLayout;
            return this;
        }

        public BuilderFreeDialog setViewListener(CommFreeDialogListener listener)
        {
            this.listener = listener;
            return this;
        }

        public BuilderFreeDialog setTextPositifButton(String text)
        {
            this.textPositifButton = text;
            return this;
        }

        public BuilderFreeDialog setTextNegativeButton(String text)
        {
            this.textNegativeButton = text;
            return this;
        }

        public BuilderFreeDialog setBodyText(String text)
        {
            this.bodyText = text;
            return this;
        }

        public BuilderFreeDialog setIcon(int icon)
        {
            this.icon = icon;
            return this;
        }

        public BuilderFreeDialog setStyleAnimation(int styleAnimation)
        {
            this.styleAnimation = styleAnimation;
            return this;
        }

        public BuilderFreeDialog showTransparentDarkBackground(boolean b)
        {
            this.showTransparentDarkBackground = b;
            return this;
        }

        public BuilderFreeDialog setActionButtonListener(CommFreeDialogActionButtonListener buttonListener)
        {
            this.buttonListener = buttonListener;
            return this;
        }

        public FreeDialog build()
        {
            return FreeDialog.instance(
                    windowSize, viewSize, layout,
                    cancelable, cancelableTouchOutside, defaultButtonShow,
                    cancelButtonShow, customLayout, textPositifButton,
                    textNegativeButton, bodyText, icon, styleAnimation,
                    showTransparentDarkBackground,
                    buttonListener, listener
            );
        }
    }

    private static FreeDialog instance(
            WindowSize ws, int[] vwSize, Layout lyt,
            boolean cancelable, boolean cancelableTouchOutside, boolean defaulButtonShow,
            boolean cancelButtonShow, int customLyt, String txtPositifButton,
            String txtNegativeButton, String bdyText, int icn, int styleAnim,
            boolean showTransparentDarkBg,
            CommFreeDialogActionButtonListener btnListener,
            CommFreeDialogListener listen)
    {
        windowSize = ws;
        viewSize = vwSize;
        layout = lyt;
        isCancelable = cancelable;
        isCancelableTouchOutside = cancelableTouchOutside;
        isDefaultButtonShow = defaulButtonShow;
        isCancelButtonShow = cancelButtonShow;
        customLayout = customLyt;
        textPositifButton = txtPositifButton;
        textNegativeButton = txtNegativeButton;
        bodyText = bdyText;
        icon = icn;
        styleAnimation = styleAnim;
        showTransparentDarkBackground = showTransparentDarkBg;
        buttonListener = btnListener;
        listener = listen;
        freeDialog = new FreeDialog();
        return freeDialog;
    }

    @Override
    protected void onMCreate(Bundle savedInstanceState) {

    }

    @Override
    protected View onMCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(!showTransparentDarkBackground)getDialog().getWindow().setDimAmount(0.0f);
        if(!isCancelable)getDialog().setCancelable(false);
        if(!isCancelableTouchOutside){
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {}
                    return true;
                }
            });
        }
        switch (layout)
        {
            case CUSTOM_LAYOUT:
                if(customLayout == 0)
                {
                    throw new RuntimeException("Your Layout must be defined !");
                }
                else
                {
                    view = inflater.inflate(customLayout, container);
                    if(null != listener)listener.setView(f, view, this);
                }
                break;
            case DEFAULT:
                view = inflater.inflate(com.maedi.soft.ino.R.layout.dialog_default_style, container);
                LinearLayout mainLayout = (LinearLayout) view.findViewById(com.maedi.soft.ino.R.id.main_layout);

                ImageView imgIcon = (ImageView) view.findViewById(com.maedi.soft.ino.R.id.icon);
                TextView bdText = (TextView) view.findViewById(com.maedi.soft.ino.R.id.body_text);
                bdText.setText(null == bodyText ? "" : bodyText);

                LinearLayout ok = (LinearLayout) view.findViewById(com.maedi.soft.ino.R.id.yes);
                TextView textOk = (TextView) view.findViewById(com.maedi.soft.ino.R.id.text_yes);
                if(null != textPositifButton)textOk.setText(textPositifButton);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDialog().dismiss();
                        if(null != buttonListener)buttonListener.ok();
                    }
                });

                LinearLayout cancel = (LinearLayout) view.findViewById(com.maedi.soft.ino.R.id.not);
                TextView textNot = (TextView) view.findViewById(com.maedi.soft.ino.R.id.text_not);
                if(null != textNegativeButton)textNot.setText(textNegativeButton);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDialog().dismiss();
                        if(null != buttonListener)buttonListener.cancel();
                    }
                });

                imgIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null != buttonListener)buttonListener.clickIcon(freeDialog);
                    }
                });

                if(icon != 0)
                {
                    imgIcon.setImageDrawable(getResources().getDrawable(icon));
                }

                if(!isCancelButtonShow)
                {
                    cancel.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 0);
                    ok.setLayoutParams(params);
                }

                if(!isDefaultButtonShow)
                {
                    LinearLayout defaulButtonLayout = (LinearLayout) view.findViewById(com.maedi.soft.ino.R.id.default_button_layout);
                    defaulButtonLayout.setVisibility(View.GONE);
                    mainLayout.setPadding(0, 0, 0, PixelCalc.DpToPixel(20, f));
                }
                break;
        }
        return view;
    }

    @Override
    protected void onMViewCreated(View v, Bundle savedInstanceState) {

    }

    @Override
    protected void onMActivityCreated(Bundle arg0) {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        switch (windowSize)
        {
            case FULL_SIZE:
                getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                break;
            case WRAP_CONTENT:
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.gravity = Gravity.CENTER;
                getDialog().getWindow().setAttributes(params);
                break;
            case CUSTOM_SIZE:
                if(viewSize[0] == 0 || viewSize[1] == 0)
                    throw new RuntimeException("Your Frame View size must be defined !");
                else
                {
                    params.width = PixelCalc.PixelToDP(f, viewSize[0]);
                    params.height = PixelCalc.PixelToDP(f, viewSize[1]);
                    params.gravity = Gravity.CENTER;
                    getDialog().getWindow().setAttributes(params);
                }
                break;
        }
        getDialog().getWindow().getAttributes().windowAnimations = styleAnimation;
    }

    public void dismiss()
    {
        getDialog().dismiss();
    }

    @Override
    protected void onMDismiss(DialogInterface dialogInterface) {

    }

    @Override
    protected void onMActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    protected void onMStart() {

    }

    @Override
    protected void onMResume() {

    }

    @Override
    protected void onMAttach(Context context) {
        if (context instanceof Activity) {
            f = (FragmentActivity) context;
            FragmentTransaction ft = f.getFragmentManager().beginTransaction();
            Fragment prev = f.getFragmentManager().findFragmentByTag(dialogTag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
        }
    }

    @Override
    protected void onMDetach() {
        if(null != f)
            f = null;

    }

    @Override
    protected void onMStop() {

    }

    @Override
    protected void onMDestroy() {

    }

    @Override
    protected void onMDestroyView() {

    }
}
