package com.visa.visasampleapplication;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.os.Build;

public class ChargeCardActivity extends Activity {
	
	private EditText cardNumber;
	private EditText expDate;
	
	private String cardNumText;
	
	private int cardNumberLen = 0;
	private int expDateLen = 0;
	
	public ArrayList<String> toArrayList(String cardNumber) {
		ArrayList<String> cardArray = new ArrayList<String>();
		for (int i = 0; i < cardNumber.length(); i++) {
			if ((i != cardNumber.length() - 1) && String.valueOf(cardNumber.charAt(i + 1)).equals(" ")) {
				cardArray.add(String.valueOf(cardNumber.charAt(i)) + String.valueOf(cardNumber.charAt(i + 1)));
				i++;
			} else {
				cardArray.add(String.valueOf(cardNumber.charAt(i)));
			}
			
		}
		return cardArray;
	}
	
	public String ArrayToString(ArrayList<String> cardNumber) {
		String cardString = "";
		for (int i = 0; i < cardNumber.size(); i++) {
			cardString += cardNumber.get(i);
		}
		return cardString;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_card);
		setupUI(findViewById(R.id.charge_card_form));
		getActionBar().show();
		// Auto-format credit card text field at real time
		// TODO: FIX FORMAT :'(
		cardNumber = (EditText) findViewById(R.id.card_number);
		cardNumber.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					ArrayList<String> cardArray = toArrayList(cardNumText);
					Log.d("ChargeCardActivity", "before i remove: " + cardArray.toString());
					//cardArray.remove(cardArray.size() - 1);
					String newCardNumber = ArrayToString(cardArray);
					newCardNumber = newCardNumber.trim();
					Log.d("ChargeCardActivity", "after i remove and convert to string: " + newCardNumber);
					cardNumber.setText(new StringBuilder(newCardNumber));
					cardNumber.setSelection(cardNumber.getText().length());
				}
				return false;
			}
		});
		cardNumber.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				cardNumText = cardNumber.getText().toString();
				cardNumberLen = cardNumber.getText().length();
				if (cardNumberLen == 5 || cardNumberLen == 10 || cardNumberLen == 15) {
					cardNumber.setText(new StringBuilder(cardNumText).insert(cardNumText.length() - 1, " ").toString());
					cardNumber.setSelection(cardNumber.getText().length());
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
				DialogFragment swipeCardFragment = ProgressDialogSpinner.newInstance("Please swipe your credit card", "Processing credit card information...");
				swipeCardFragment.show(getFragmentManager(), "dialog");
				//displayProgressDialogSpinner("Please swipe your credit card", "Processing credit card information...");
				//TODO: PROCESS SWIPE CARD
			}
		});
	}
	

    /** Disable the back button. */
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.charge_card, menu);
		Log.d("menu", "inflated the menu");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.dev_info:
				DialogFragment devInfoFragment = DevInfoFragment.newInstance();
				devInfoFragment.show(getFragmentManager(), "devInfo");
				return true;
			case R.id.logout:
				Intent logoutIntent = new Intent(this, LoginActivity.class);
				
				startActivity(logoutIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	

    /** Fragment that prompts user with a ProgressDialog (spinner) with a cancel button. */
    public static class ProgressDialogSpinner extends DialogFragment {
    	public static ProgressDialogSpinner newInstance (String title, String message) {
    		ProgressDialogSpinner fragment = new ProgressDialogSpinner();
    		Bundle args = new Bundle();
    		args.putString("title",  title);
    		args.putString("message", message);
    		fragment.setArguments(args);
    		return fragment;
    	}
    	
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		String title = getArguments().getString("title");
    		String message = getArguments().getString("message");
    		ProgressDialog alert = new ProgressDialog(getActivity());
    		setCancelable(false);
    		TextView titleView = new TextView(getActivity());
    		titleView.setText(title);
    		titleView.setPadding(15,15,15,15);
    		titleView.setTextColor(Color.WHITE);
    		titleView.setTextSize(20);
    		titleView.setGravity(Gravity.CENTER);
    		alert.setCustomTitle(titleView);
    		
        	TextView messageView = new TextView(getActivity());
        	messageView.setText(message);
        	messageView.setTextSize(15);
        	messageView.setPadding(15, 15, 15, 15);
        	messageView.setGravity(Gravity.CENTER);
        	alert.setView(messageView);
    		alert.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel", new Dialog.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				return;
    			}
    		});
    		//TextView messageView = (TextView) alert.findViewById(android.R.id.message);
    		//messageView.setGravity(Gravity.CENTER);
    		return alert;
    	}
    }

	// Utility functions for ChargeCardActivity.java. */
	/** Dismisses the softkey board outside of EditText area. */
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	/** Iterates through each View in this activity and checks if it is an
	 * instance of EditText and if it is not, register a setOnTouchlistener
	 * to that component. */
	public void setupUI(View view) {
		if(!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(ChargeCardActivity.this);
					return false;
				}
			});
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}
	
	
	/** Sets up the order of all of the EditTexts. */
	private void setupEditText() {
		final EditText cardNumber = (EditText) findViewById(R.id.card_number);
		final EditText expDate = (EditText) findViewById(R.id.expiration_date);
		final EditText cvv2 = (EditText) findViewById(R.id.CVV2);
		final EditText zip = (EditText) findViewById(R.id.zip_code);
		cardNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					expDate.requestFocus();
					return true;
				}
				return false;
			}
			
		});
		expDate.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					cvv2.requestFocus();
					return true;
				}
				return false;
			}
			
		});
		cvv2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					zip.requestFocus();
					return true;
				}
				return false;
			}
		});
		zip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					//TODO: handle submit button here
					return true;
				}
				return false;
			}
		});
	}
}
