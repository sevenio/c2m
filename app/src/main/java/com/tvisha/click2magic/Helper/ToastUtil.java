package com.tvisha.click2magic.Helper;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.Toast;

public class ToastUtil {

	static ToastUtil toastUtil=new ToastUtil();
	public static ToastUtil 	getInstance(){
		if(toastUtil==null){
			toastUtil=new ToastUtil();
		}
		return toastUtil;
	}

	Toast toast;

	public void showToast(Context context,String msg) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	public void showToast(Context context,int resId) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		toast.show();
	}
	public void showSnackBar(View view,String message){
		Snackbar snackbar = Snackbar.make(view,message, Snackbar.LENGTH_LONG).setAction("Action", null);
		snackbar.show();
	}
}
