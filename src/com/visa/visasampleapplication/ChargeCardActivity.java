package com.visa.visasampleapplication;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.os.Build;

public class ChargeCardActivity extends Activity {
	
	private EditText cardNumber;
	private int cardNumberLen = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_card);
		cardNumber = (EditText) findViewById(R.id.card_number);
		cardNumber.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String cardNumText = cardNumber.getText().toString();
				cardNumberLen = cardNumText.length();
				if (cardNumText.endsWith(" "))
					return;
				if (cardNumberLen == 5 || cardNumberLen == 10 || cardNumberLen == 15) {
					cardNumber.setText(new StringBuilder(cardNumText).insert(cardNumText.length() - 1, " ").toString());
				}
				
			}	
		});

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.charge_card, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
			case R.id.dev_info:
				//TODO:handle dev_info
				return true;
			case R.id.logout:
				//TODO: handle logout
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
