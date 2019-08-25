package com.maedi.soft.ino.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.maedi.soft.ino.R;

import java.util.List;

public class AlertDialog extends DialogFragment {
	
	String dialogTag;
	String title;
	int icon;
	String message;
	boolean showNegativeButton;
	Context context;
	String tag;

	private AlertDialogFragmentListener listener;
	private boolean isInForeground=true;

	public interface AlertDialogFragmentListener {
		void onAlertOKClicked();
	}
	
	public AlertDialog() {}

	public static AlertDialog newInstance(Context context, String title, int icon, String message, boolean showNegativeButton) {
		AlertDialog frag = new AlertDialog();
		if(null != context)frag.context = context;
		frag.title = title;
		frag.icon = icon;
		frag.message = message;
		frag.showNegativeButton = showNegativeButton;
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("icon", String.valueOf(icon));
		args.putString("message", message);
		args.putBoolean("showNegativeButton", showNegativeButton);
		frag.setArguments(args);
		return frag;
	}

	public static AlertDialog newInstance(Context context, String title, int icon, String message, boolean showNegativeButton, AlertDialogFragmentListener listener) {
		AlertDialog frag = new AlertDialog();
		if(null != context)frag.context = context;
		frag.title = title;
		frag.icon = icon;
		frag.message = message;
		frag.showNegativeButton = showNegativeButton;
		frag.listener = listener;
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("icon", String.valueOf(icon));
		args.putString("message", message);
		args.putBoolean("showNegativeButton", showNegativeButton);
		frag.setArguments(args);
		return frag;
	}

	public static AlertDialog newInstance(Context context, String title, int icon, String message, boolean showNegativeButton, String tag) {
		AlertDialog frag = new AlertDialog();
		if(null != context)frag.context = context;
		frag.title = title;
		frag.icon = icon;
		frag.message = message;
		frag.showNegativeButton = showNegativeButton;
		frag.tag = tag;
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("icon", String.valueOf(icon));
		args.putString("message", message);
		args.putBoolean("showNegativeButton", showNegativeButton);
		args.putString("tag", tag);
		frag.setArguments(args);
		return frag;
	}

	public void setListener(AlertDialogFragmentListener listener) {
		this.listener = listener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());

		String message = getArguments().getString("message");

		if (null == message) {message="";}
		alertDialogBuilder.setMessage(message);

		int icon = Integer.parseInt(getArguments().getString("icon"));
		if(icon > 0)
		{
			alertDialogBuilder.setIcon(icon);
		}

		setCancelable(alertDialogBuilder);
		
		alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

			   if(null == getArguments().getString("tag"))
			   {
			   		//add bussiness logic here if needed
			   }
			   else{
				   //add bussiness logic here if needed
			   }
			   if (listener != null) {
				   listener.onAlertOKClicked();
			   }
           }
		});
		
		boolean showNegativeButton = getArguments().getBoolean("showNegativeButton");
		if(showNegativeButton){
			alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
	           @Override
	           public void onClick(DialogInterface dialog, int which) {
	               dialog.dismiss();
				   destroyActivity();
	           }
			});
		}
		
		return alertDialogBuilder.create();
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
	    super.onActivityCreated(arg0);        
	    getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogInOut;
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
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		destroyActivity();
	};

	private void setCancelable(android.app.AlertDialog.Builder alertDialogBuilder){
		if(null == getArguments().getString("tag")){alertDialogBuilder.setCancelable(true);}
		else{
			alertDialogBuilder.setCancelable(false);
		}
	}

	private void destroyActivity(){
		if(null == getArguments().getString("tag")){} else{}
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
