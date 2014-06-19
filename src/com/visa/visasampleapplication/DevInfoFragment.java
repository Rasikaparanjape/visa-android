package com.visa.visasampleapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

/**Fragment that opens an AlertDialog with developer information. */
public class DevInfoFragment extends DialogFragment {
	public static DevInfoFragment newInstance() {
    	String message = "This application utilizes the Authorize.Net SDK available on GitHub"
    			+ " under the username AuthorizeNet. Authorize.Net is a wholly owned subsidiary of Visa.";
    	String title = "Developer Information";
		DevInfoFragment frag = new DevInfoFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		frag.setArguments(args);
		return frag;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");
		String message = getArguments().getString("message");
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	TextView titleView = new TextView(getActivity());
    	titleView.setText(title);
    	titleView.setGravity(Gravity.CENTER);
    	titleView.setPadding(15, 15, 15, 15);
    	//titleView.setTextColor(Color.WHITE);
    	titleView.setTextSize(20);
    	builder.setCustomTitle(titleView);
    	
    	TextView messageView = new TextView(getActivity());
    	messageView.setText(message);
    	messageView.setTextSize(15);
    	messageView.setPadding(15, 15, 15, 15);
    	messageView.setGravity(Gravity.CENTER);
    	builder.setView(messageView);
    	builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
    	AlertDialog info = builder.create();
    	setCancelable(false);
    	return info;
	}
}
