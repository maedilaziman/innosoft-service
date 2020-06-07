package com.maedi.soft.ino.base.presenter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.maedi.soft.ino.base.func_interface.CommTextWatcher;
import com.maedi.soft.ino.base.model.UserData;

import timber.log.Timber;

public class ServiceUserPresent <T> implements CommTextWatcher<T>, TextView.OnEditorActionListener {

    private final String TAG = this.getClass().getName()+"SERVICE_USER_PRESENT";

    private CommTextWatcher commTextWatcher;

    private final String paramVal1Method = "param_val1";

    private final String paramVal2Method = "param_val2";

    private final String paramVal3Method = "param_val3";

    private final String paramVal4Method = "param_val4";

    private final String paramVal5Method = "param_val5";

    private final String paramVal6Method = "param_val6";

    private final String paramVal7Method = "param_val7";

    private final String paramVal8Method = "param_val8";

    private final String paramVal9Method = "param_val9";

    private final String paramVal10Method = "param_val0";

    private final String paramVal11Method = "param_val11";

    private final String paramVal12Method = "param_val12";

    private final String paramVal13Method = "param_val13";

    private final String paramVal14Method = "param_val14";

    private final String paramVal15Method = "param_val15";

    private CommUserPresent listener;

    private UserData user;

    @Override
    public void watchingTextBehavior(T type1, final T type2) {
        View view = (View) type1;
        if(view instanceof EditText)
        {
            EditText e = (EditText) type1;
            e.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    // TODO Auto-generated method stub
                    String tag = (String) type2;
                    if (tag.equalsIgnoreCase(paramVal1Method))
                    {
                        user.setParamVal1(s.toString());
                        listener.setParamVal1(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal2Method))
                    {
                        user.setParamVal2(s.toString());
                        listener.setParamVal2(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal3Method))
                    {
                        user.setParamVal3(s.toString());
                        listener.setParamVal3(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal4Method))
                    {
                        user.setParamVal4(s.toString());
                        listener.setParamVal4(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal5Method))
                    {
                        user.setParamVal5(s.toString());
                        listener.setParamVal5(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal6Method))
                    {
                        user.setParamVal6(s.toString());
                        listener.setParamVal6(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal7Method))
                    {
                        user.setParamVal7(s.toString());
                        listener.setParamVal7(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal8Method))
                    {
                        user.setParamVal8(s.toString());
                        listener.setParamVal8(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal9Method))
                    {
                        user.setParamVal9(s.toString());
                        listener.setParamVal9(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal10Method))
                    {
                        user.setParamVal10(s.toString());
                        listener.setParamVal10(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal11Method))
                    {
                        user.setParamVal11(s.toString());
                        listener.setParamVal11(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal12Method))
                    {
                        user.setParamVal12(s.toString());
                        listener.setParamVal12(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal13Method))
                    {
                        user.setParamVal13(s.toString());
                        listener.setParamVal13(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal14Method))
                    {
                        user.setParamVal14(s.toString());
                        listener.setParamVal14(s.toString(), "");
                    }
                    else if (tag.equalsIgnoreCase(paramVal15Method))
                    {
                        user.setParamVal15(s.toString());
                        listener.setParamVal15(s.toString(), "");
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    // TODO Auto-generated method stub
                    listener.setText_BeforeTextChanged(s.toString(), "");
                }

                @Override
                public void afterTextChanged(Editable s) {

                    // TODO Auto-generated method stub
                    listener.setText_AfterTextChanged(s.toString(), "");
                }
            });

            e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    String tag = (String) type2;
                    if (tag.equalsIgnoreCase(paramVal1Method))
                    {
                        listener.hasFocusView1(hasFocus);
                    }
                    else if (tag.equalsIgnoreCase(paramVal2Method))
                    {
                        listener.hasFocusView2(hasFocus);
                    }
                    else if (tag.equalsIgnoreCase(paramVal3Method))
                    {
                        listener.hasFocusView3(hasFocus);
                    }
                    else if (tag.equalsIgnoreCase(paramVal4Method))
                    {
                        listener.hasFocusView4(hasFocus);
                    }
                    else if (tag.equalsIgnoreCase(paramVal5Method))
                    {
                        listener.hasFocusView5(hasFocus);
                    }
                    else if (tag.equalsIgnoreCase(paramVal6Method))
                    {
                        listener.hasFocusView6(hasFocus);
                    }
                    else if (tag.equalsIgnoreCase(paramVal7Method))
                    {
                        listener.hasFocusView7(hasFocus);
                    }
                    else if (tag.equalsIgnoreCase(paramVal8Method))
                    {
                        listener.hasFocusView8(hasFocus);
                    }
                    else if (tag.equalsIgnoreCase(paramVal9Method))
                    {
                        listener.hasFocusView9(hasFocus);
                    }
                    else if (tag.equalsIgnoreCase(paramVal10Method))
                    {
                        listener.hasFocusView10(hasFocus);
                    }
                }
            });
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
            String text = v.getText().toString();
            closeSoftKeyboard(v);
            listener.doneEditText(text);
            return true;
        }
        return false;
    }

    public interface CommUserPresent<T>
    {
        String setParamVal1(String paramVal1, T info);

        String setParamVal2(String paramVal2, T info);

        String setParamVal3(String paramVal3, T info);

        String setParamVal4(String paramVal4, T info);

        String setParamVal5(String paramVal5, T info);

        String setParamVal6(String paramVal6, T info);

        String setParamVal7(String paramVal7, T info);

        String setParamVal8(String paramVal8, T info);

        String setParamVal9(String paramVal9, T info);

        String setParamVal10(String paramVal10, T info);

        String setParamVal11(String paramVal11, T info);

        String setParamVal12(String paramVal12, T info);

        String setParamVal13(String paramVal13, T info);

        String setParamVal14(String paramVal14, T info);

        String setParamVal15(String paramVal15, T info);

        String setText_BeforeTextChanged(String text, T info);

        String setText_AfterTextChanged(String text, T info);

        String doneEditText(String s);

        boolean hasFocusView1(boolean b);

        boolean hasFocusView2(boolean b);

        boolean hasFocusView3(boolean b);

        boolean hasFocusView4(boolean b);

        boolean hasFocusView5(boolean b);

        boolean hasFocusView6(boolean b);

        boolean hasFocusView7(boolean b);

        boolean hasFocusView8(boolean b);

        boolean hasFocusView9(boolean b);

        boolean hasFocusView10(boolean b);
    }

    public ServiceUserPresent(CommUserPresent listener)
    {
        this.listener = listener;
        this.user = new UserData();
    }

    private ServiceUserPresent(CommUserPresent listener, UserData user)
    {
        this.listener = listener;
        this.user = user;
    }

    public void setCommWatchingTextBehavior(CommTextWatcher commTextWatcher)
    {
        this.commTextWatcher = commTextWatcher;
    }

    public void setParamVal1(EditText editText)
    {
        commTextWatcher.watchingTextBehavior(editText, paramVal1Method);
    }

    public void setParamVal2(EditText editText)
    {
        commTextWatcher.watchingTextBehavior(editText, paramVal2Method);
    }

    public void setParamVal3(EditText editText)
    {
        commTextWatcher.watchingTextBehavior(editText, paramVal3Method);
    }

    public void setParamVal4(EditText editText)
    {
        commTextWatcher.watchingTextBehavior(editText, paramVal4Method);
    }

    public void setParamVal5(EditText editText)
    {
        commTextWatcher.watchingTextBehavior(editText, paramVal5Method);
    }

    public void setParamVal6(EditText editText)
    {
        commTextWatcher.watchingTextBehavior(editText, paramVal6Method);
    }

    public void setParamVal7(EditText editText)
    {
        commTextWatcher.watchingTextBehavior(editText, paramVal7Method);
    }

    public void setParamVal8(EditText editText)
    {
        commTextWatcher.watchingTextBehavior(editText, paramVal8Method);
    }

    public void setParamVal9(EditText editText)
    {
        commTextWatcher.watchingTextBehavior(editText, paramVal9Method);
    }

    private void closeSoftKeyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}