package com.visa.visasampleapplication;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.os.Build;

public class ChargeCardActivity extends Activity {
	
	private EditText cardNumber;
	private EditText expDate;
	
	private String cardNumText;
	
	private int cardNumberLen = 0;
	private int expDateLen = 0;
//test
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_card);
		getActionBar().show();
		ScrollView scroll = new ScrollView(this);
		// Auto-format credit card text field at real time
		// TODO: FIX FORMAT :'(
		cardNumber = (EditText) findViewById(R.id.card_number);
		cardNumber.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				cardNumText = cardNumber.getText().toString();
				cardNumberLen = cardNumber.getText().length();
				if (cardNumberLen == 5 || cardNumberLen == 10 || cardNumberLen == 15) {
					cardNumber.setText(new StringBuilder(cardNumText).insert(cardNumText.length() - 1, " ").toString());
					cardNumber.setSelection(cardNumber.getText().length());
					/**
					cardNumber.setOnKeyListener(new View.OnKeyListener() {
						
						@Override
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_DEL){
								cardNumber.setText(new StringBuilder(cardNumText).deleteCharAt(cardNumberLen - 1));
								cardNumber.setText(new StringBuilder(cardNumText).deleteCharAt(cardNumberLen - 2));
							}
							return true;
						}
					}); */

				}				
				if (cardNumText.endsWith(" "))
					return;
			}	
		});
		
		// Auto-format expiration date at real time
		// TODO: FIX FORMAT :'(
		expDate = (EditText) findViewById(R.id.expiration_date);
		expDate.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String expDateText = expDate.getText().toString();
				expDateLen = expDateText.length();
				if (expDateLen == 3) {
					expDate.setText(new StringBuilder(expDateText).insert(expDateText.length() - 1, "/").toString());
					expDate.setSelection(expDateLen);
					//expDate.removeTextChangedListener(this);
				}
				if (expDateText.endsWith(" ")) {
					return;
				}
			}
		});
		
		// respond to swipe card button
		findViewById(R.id.swipe_card_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				displayProgressDialogSpinner("Please swipe your credit card", "Processing credit card information...");
				//TODO: PROCESS SWIPE CARD
			}
		});
	}
	
	/** Prompts user with a ProgressDialog (spinner) with a cancel button. */
	public void displayProgressDialogSpinner(String title, String message) {
		ProgressDialog alert = new ProgressDialog(this);
		alert.setCancelable(false);
		TextView titleView = new TextView(this);
		titleView.setText(title);
		titleView.setPadding(15,15,15,15);
		titleView.setTextColor(Color.WHITE);
		titleView.setTextSize(20);
		titleView.setGravity(Gravity.CENTER);
		
		alert.setCustomTitle(titleView);
		alert.setMessage(message);
		alert.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		Log.d("null-check", "before get button");
		//Button cancelButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
		//Log.d("null-check", "before settypeface");
		//cancelButton.setTypeface(null, Typeface.BOLD);
		Log.d("null-check", "before show");
		alert.show();
		Log.d("null-check", "messageView");
		TextView messageView = (TextView) alert.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
		Log.d("null-check", "before titleView");
		
		//TODO: set the title to center
		//TODO:TextView titleView = (TextView) alert.findViewById(android.R.id.title);
		//TODO:titleView.setGravity(Gravity.CENTER);
		//TODO:Log.d("null-check", "set the title to center");

		
		/**
		ProgressDialog ringProgressDialog = ProgressDialog.show(ChargeCardActivity.this, title, message, false, false, new DialogInterface.OnCancelListener() {
			//public void onClick(DialogInterface dialog, int id) {
				//dialog.dismiss();
			//}
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
			}

		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (Exception e) {
					
				}
			}
		}).start(); */


		/**
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = (ProgressDialog) builder.create();
		alert.setCancelable(true);
		
		
		TextView messageView = (TextView) alert.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER); */
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.charge_card, menu);
		Log.d("menu", "inflated the menu");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
			case R.id.dev_info:
				openDevInfo();
				return true;
			case R.id.logout:
				//TODO: handle logout
				Intent logoutIntent = new Intent(this, LoginActivity.class);
				
				startActivity(logoutIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/** Opens an AlertDialog with developer information. */
    public void openDevInfo() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	TextView title = new TextView(this);
    	title.setText("Developer Information");
    	title.setGravity(Gravity.CENTER);
    	title.setPadding(15, 15, 15, 15);
    	title.setTextColor(Color.WHITE);
    	title.setTextSize(20);
    	builder.setCustomTitle(title);
    	String message = "This application utilizes the Authorize.Net SDK available on GitHub"
    			+ " under the username AuthorizeNet. Authorize.Net is a wholly owned subsidiary of Visa.";
    	builder.setMessage(message).setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
    	AlertDialog info = builder.create();
    	info.show();
    	//TODO: center the title
    	TextView messageView = (TextView) info.findViewById(android.R.id.message);
    	messageView.setTextSize(13);
    	messageView.setGravity(Gravity.CENTER);
    }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_charge_card,
					container, false);
			return rootView;
		}
	}

}
