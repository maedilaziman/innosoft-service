package com.maedi.soft.ino.base.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.maedi.soft.ino.R;

import java.util.List;

@SuppressLint("TimberArgCount")
public class LoadingContentDialog extends DialogFragment {
	
	String dialogTag;
	String title;
	int icon;
	String message;
	String taskKey;
	ProgressDialog dialog;
	private boolean isInForeground=true;
	
	public LoadingContentDialog() {}

	public static LoadingContentDialog newInstance(String title, int icon, String message, boolean cancelableOnTouchOutside, boolean cancelable, String taskKey) {
		LoadingContentDialog frag = new LoadingContentDialog();
		frag.title = title;
		frag.icon = icon;
		frag.message = message;
		frag.taskKey = taskKey;
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("icon", String.valueOf(icon));
		args.putString("message", message);
		args.putBoolean("cancelableOnTouchOutside", cancelableOnTouchOutside);
		args.putBoolean("cancelable", cancelable);
		args.putString("taskKey", taskKey);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		dialog = new ProgressDialog(getActivity()){
			@Override
			public void onBackPressed() {
				dialog.setCancelable(getArguments().getBoolean("cancelable"));
			}
		};
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_loading_content_window, null);

		dialog.setCanceledOnTouchOutside(getArguments().getBoolean("cancelableOnTouchOutside"));
		dialog.setCancelable(getArguments().getBoolean("cancelable"));
		dialog.getWindow().getAttributes().windowAnimations = R.style.DialogProgressAnimation;
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setProgressStyle(R.style.CustomLoadingDialog);
		dialog.show();
		dialog.setContentView(view);

		dialog.setIndeterminate(true);

		return dialog;
	}

	
	@Override
	public void onCancel(android.content.DialogInterface dialog) {
		super.onCancel(dialog);
	};

	/**
	 * Dismisses the dialog from the fragment manager. We need to make sure we get the right dialog reference
	 * here which is why we obtain the dialog fragment manually from the fragment manager
	 * @param manager
	 */
	public void dismiss(FragmentManager manager)
	{
		FragmentTransaction ft = manager.beginTransaction();
		LoadingContentDialog dialog = (LoadingContentDialog)manager.findFragmentByTag(dialogTag);
		if (dialog != null) {
			dialog.dismiss();
		}
		ft.commitAllowingStateLoss();
	}
	
	@Override
	public void show(FragmentManager manager, String tag)
	{
		if(isInForeground) {
			dialogTag = tag;
			dismissAllDialogs(manager);
			try {
				FragmentTransaction ft = manager.beginTransaction();
				ft.add(this, dialogTag);
				ft.commitAllowingStateLoss();
			} catch (IllegalStateException e) {}
		}
	}

	public void dismissAllDialogs(FragmentManager manager) {
		List<Fragment> fragments = manager.getFragments();

		if (fragments == null)
			return;

		for (Fragment fragment : fragments) {
			if (fragment instanceof AlertDialog) {
				AlertDialog dialogFragment = (AlertDialog) fragment;
				dialogFragment.dismissAllowingStateLoss();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isInForeground=false;
	}

	@Override
	public void onResume() {
		super.onResume();
		isInForeground=true;
	}
}
