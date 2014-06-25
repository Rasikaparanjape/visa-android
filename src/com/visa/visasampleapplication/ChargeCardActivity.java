package com.visa.visasampleapplication;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import com.visa.visasampleapplication.LoginActivity.UserLoginTask;
import com.visa.visasampleapplication.PaymentTransaction.IAnetTransactionCallBackProcessAIMPayment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import net.authorize.TransactionType;
import net.authorize.aim.Result;
import net.authorize.aim.Transaction;
import net.authorize.aim.cardpresent.DeviceType;
import net.authorize.aim.cardpresent.MarketType;
import net.authorize.auth.SessionTokenAuthentication;
import net.authorize.data.Order;
import net.authorize.data.creditcard.CreditCard;
import net.authorize.util.Luhn;
import net.authorize.data.Customer;
import net.authorize.data.Order;
import net.authorize.data.OrderItem;

public class ChargeCardActivity extends Activity {
    private static final int CREDIT_CARD_LENGTH = 16;
    private static final int CREDIT_CARD_LENGTH_W_SPACE = 19;
    
	private EditText cardNumber;
    private EditText expDate;
    private EditText cvv2;
    private EditText zipcode;
    
    private String cardNumText;
    
    private int cardNumberLen = 0;
    private int expDateLen = 0;
    
    private static CreditCard creditCard;
    private static Order testOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_card);
        setupUI(findViewById(R.id.charge_card_form));
        getActionBar().show();
        
        // Auto-format credit card text field at real time
        cardNumber = (EditText) findViewById(R.id.card_number);
        cardNumber.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cardNumText = cardNumber.getText().toString();
                cardNumberLen = cardNumber.getText().length();
                if ((cardNumberLen == 5 || cardNumberLen == 10 || cardNumberLen == 15) 
                        && !(String.valueOf(cardNumber.getText().toString().charAt(cardNumberLen - 1))
                                .equals(" "))) {
                    cardNumber.setText(new StringBuilder(cardNumText).insert(cardNumText.length() - 1, " ").toString());
                    cardNumber.setSelection(cardNumber.getText().length());
                }
                if (cardNumText.endsWith(" ")) {
                    return;
                }
                if ((cardNumberLen == CREDIT_CARD_LENGTH_W_SPACE)) {
                	cardNumText = cardNumber.getText().toString();
                	cardNumText.replace(" ", "");
                	Log.d("card number","card number: " + cardNumText);
                	if (Luhn.isCardValid(cardNumText)) {
                		Drawable checkMark = getResources().getDrawable(R.drawable.ic_check_mark);
                		Bitmap checkMarkBitmap = ((BitmapDrawable) checkMark).getBitmap();
                		Drawable scaledCheckMark = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(checkMarkBitmap, 50, 50, true));
                		cardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, scaledCheckMark, null);
                	} else {
                		Drawable delete = getResources().getDrawable(R.drawable.ic_delete);
                		Bitmap deleteBitmap = ((BitmapDrawable) delete).getBitmap();
                		Drawable scaledDelete = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(deleteBitmap, 50, 50, true));
                		cardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, scaledDelete, null);
                	}
                } else {
                	cardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }    
        });

        // Auto-format expiration date at real time
        expDate = (EditText) findViewById(R.id.expiration_date);
        expDate.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String expDateText = expDate.getText().toString();
                expDateLen = expDateText.length();
                if (expDateLen == 3 && !(String.valueOf(expDate.getText().toString().charAt(expDateLen - 1)).equals("/"))) {
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
                DialogFragment swipeCardFragment = ProgressDialogSpinner.newInstance(getString(R.string.swipe_card_alert), getString(R.string.swipe_card_alert_message));
                swipeCardFragment.show(getFragmentManager(), "swipecard");
                //TODO: PROCESS SWIPE CARD
            }
        });
        cvv2 = (EditText) findViewById(R.id.CVV2);
        findViewById(R.id.question_mark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment cvv2Info = DevInfoFragment.newInstance(getString(R.string.cvv2_info_message), getString(R.string.cvv2_info_title));
                cvv2Info.show(getFragmentManager(), "swipecard");
            }
        });
        // response correlated to pressing submit on the keypad
        zipcode = (EditText) findViewById(R.id.zip_code);
        zipcode
            .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        attemptSubmit();
                        processCreditCardInfo();
                        return true;
                    }
                    return false;
                }
            });
        // respond to submit button
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                attemptSubmit();
                processCreditCardInfo();
                
            }
        });
    }
    
    
    /** Create a test order. - for testing purposes only */
    public void createTestOrder() {
    	testOrder = Order.createOrder();
    	testOrder.setTotalAmount(new BigDecimal(20));
    	OrderItem testItem1 = OrderItem.createOrderItem();
    	testItem1.setItemId("test1ID");
    	testItem1.setItemName("test1Name");
    	testItem1.setItemDescription("test1Description");
    	testItem1.setItemQuantity(new BigDecimal(1));
    	testItem1.setItemPrice(new BigDecimal(20));
    	testOrder.addOrderItem(testItem1);
    	testOrder.setPurchaseOrderNumber("9999");
    }
    /** Process all credit card information and continue to submit a transaction. */
    public void processCreditCardInfo() {
    	createTestOrder();
    	creditCard = CreditCard.createCreditCard();
    	String[] expDateArray = expDate.getText().toString().split("/");
    	creditCard.setExpirationMonth(expDateArray[0]);
    	creditCard.setExpirationYear(expDateArray[1]);
    	creditCard.setCreditCardNumber(cardNumText);
    	creditCard.setCardCode(cvv2.getText().toString());
    	creditCard.setAnetDuplicatemaskedCardNumber(cardNumText.substring(12, 15)); // check dis
    	FragmentManager manager = getFragmentManager();
    	ProcessingPaymentFragment ppf = new ProcessingPaymentFragment();
    	manager.beginTransaction().add(ppf, "task").commit();
    	processTransaction(ppf.resultReceiver, getIntent());
    	Log.d("hello", "DO YOU FUCKING FINISH YOU FUCK");
    }
    
    public static void processTransaction(ResultReceiver receiver, Intent intent) {
    	Log.d("hello", "I AM IN PROCESS TRANSACTION");
    	Bundle transactionBundle = new Bundle();
    	transactionBundle.putString("START_TRANSACTION", "PROCESSING_PAYMENT");
    	receiver.send(SampleReceiver.TRANSACTION_STARTED, transactionBundle);
    	Bundle b = new Bundle();
    	String transactionType = "EXTRA_AIM_TRANSACTION_TYPE";
    	//if (transactionType.equals(TransactionType.AUTH_CAPTURE.getNVPValue())) {
    		DeviceType oldDeviceType = LoginActivity._merchant.getDeviceType();
    		MarketType oldMarketType = LoginActivity._merchant.getMarketType();
    		Result result = aimTransaction(net.authorize.TransactionType.AUTH_CAPTURE);
    		LoginActivity._merchant.setDeviceType(oldDeviceType);
    		LoginActivity._merchant.setMarketType(oldMarketType);
    		if (result != null) {
    			b.putSerializable("EXTRA_AIM_RESULT", result);
    			updateSessionToken(result);
    			receiver.send(SampleReceiver.TRANSACTION_COMPLETE, b);
    		}
    	//}
    }
    
    private static net.authorize.aim.Result aimTransaction(TransactionType transactionType) {
    	Log.d("hello", "I AM IN AIM TRANSACTION");
    	if (transactionType == TransactionType.AUTH_CAPTURE) {
    		Transaction transaction = LoginActivity._merchant.createAIMTransaction(transactionType, testOrder.getTotalAmount());
    		transaction.setCreditCard(creditCard);
    		transaction.setOrder(testOrder);
    		transaction.setCustomer(Customer.createCustomer());
    		return (Result) LoginActivity._merchant.postTransaction(transaction);
    	} else {
    		return null;
    	}
    }
    
    public static void updateSessionToken(net.authorize.xml.Result result) {
    	Log.d("hello", "i am in update session");
    	try {
    		SessionTokenAuthentication sessionTokenAuthentication = SessionTokenAuthentication.createMerchantAuthentication(
    				LoginActivity._merchant.getMerchantAuthentication().getName(), result.getSessionToken(), LoginActivity.deviceID);
    		if ((result.getSessionToken() != null) && (sessionTokenAuthentication != null)) {
    			LoginActivity._merchant.setMerchantAuthentication(sessionTokenAuthentication);;
    		}
    	} catch (Exception e) { }
    }
    public static class ProcessingPaymentFragment extends Fragment implements PaymentTransaction.IAnetTransactionCallBackProcessAIMPayment {
    	
    	static PaymentTransaction resultReceiver = new PaymentTransaction();
    	
    	@Override
    	public void onCreate(Bundle savedInstanceState) {
    		Log.d("hello", "DO YOU GO IN HERE");
    		super.onCreate(savedInstanceState);
    		setRetainInstance(true);
    		resultReceiver = new PaymentTransaction();
    		resultReceiver.setRecieverCallBack(this);
    	}
    	@Override
    	public void onTransactionComplete(final Result result) {
    		if (result.isApproved()) {
    			((ChargeCardActivity) getActivity()).displayToast(getString(R.string.completed_transaction));
    		}
    	}

		@Override
		public void onTransactionStarted(String message) {
			((ChargeCardActivity) getActivity()).displayToast("Transaction is being processed");
		}

		@Override
		public void onError(String message, Exception e) {
			((ChargeCardActivity) getActivity()).displayToast("Error");
			
		}
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.dev_info:
                DialogFragment devInfoFragment = DevInfoFragment.newInstance(getString(R.string.dev_info_message), getString(R.string.dev_info_title));
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
            
            TextView titleView = new TextView(getActivity());
            titleView.setText(title);
            titleView.setPadding(15,15,15,15);
            titleView.setTextSize(20);
            titleView.setGravity(Gravity.CENTER);
            alert.setCustomTitle(titleView);
            
            TextView messageView = new TextView(getActivity());
            messageView.setText(message);
            messageView.setTextSize(15);
            messageView.setPadding(15, 15, 15, 15);
            messageView.setGravity(Gravity.CENTER);
            alert.setMessage(message);
            //alert.setView(messageView);
            alert.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            setCancelable(false);
            return alert;
        }
    }

    // Utility functions for ChargeCardActivity.java.

    /** Dismisses the soft-key board outside of EditText area. */
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
    
    public void attemptSubmit() {
        cardNumber = (EditText) findViewById(R.id.card_number);
        expDate = (EditText) findViewById(R.id.expiration_date);
        cvv2 = (EditText) findViewById(R.id.CVV2);
        zipcode = (EditText) findViewById(R.id.zip_code);
        
        // Reset errors
        cardNumber.setError(null);
        expDate.setError(null);
        cvv2.setError(null);
        zipcode.setError(null);
        
        View focusView = null;
        boolean cancel = false;
        
        
        // Store the value at the time of the transaction process
        String cardNumberString = cardNumber.getText().toString();
        String expDateString = expDate.getText().toString();
        String cvv2String = cvv2.getText().toString();
        String zipcodeString = zipcode.getText().toString();
        
        // Check for valid card number
        if (TextUtils.isEmpty(cardNumberString)) {
            cardNumber.setError(getString(R.string.error_field_required));
            focusView = cardNumber;
            cancel = true;
        } else if (cardNumberString.length() != 19) {
            cardNumber.setError(getString(R.string.error_invalid_card_number));
            focusView = cardNumber;
            cancel = true;
        } else {
            cardNumberString = cardNumberString.replace(" ", "");
            if (!(TextUtils.isDigitsOnly(cardNumberString))) {
                cardNumber.setError(getString(R.string.error_invalid_card_number));
                focusView = cardNumber;
                cancel = true;
            }
        }
        
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(1);
        // Check for valid expiration date
        if (TextUtils.isEmpty(expDateString)) {
            expDate.setError(getString(R.string.error_field_required));
            focusView = expDate;
            cancel = true;
        } else if (expDateString.length() < 5) {
            expDate.setError(getString(R.string.error_invalid_expdate));
            focusView = expDate;
            cancel = true;
        } else if (!(TextUtils.isDigitsOnly(expDateString.substring(0, 2)))
                && !(TextUtils.isDigitsOnly(expDateString.substring(3, 5)))) {
            expDate.setError(getString(R.string.error_invalid_expdate));
            focusView = expDate;
            cancel = true;
        } else if ((Integer.valueOf(expDateString.substring(0, 2)) > 12)
                || (Integer.valueOf(expDateString.substring(0, 2)) < 0)) {
            expDate.setError(getString(R.string.error_invalid_expdate));
            focusView = expDate;
            cancel = true;
        } else if ((Integer.valueOf(expDateString.substring(3, 5)) < Integer.valueOf(year))) {
            expDate.setError(getString(R.string.error_invalid_expdate));
            focusView = expDate;
            cancel = true;
        }
        
        // Check for valid cvv2
        if (TextUtils.isEmpty(cvv2String)) {
            cvv2.setError(getString(R.string.error_field_required));
            focusView = cvv2;
            cancel = true;
        } else if (cvv2String.length() < 3) {
            cvv2.setError(getString(R.string.error_invalid_cvv2));
            focusView = cvv2;
            cancel = true;
        } else if (!(TextUtils.isDigitsOnly(cvv2String))) {
            cvv2.setError(getString(R.string.error_invalid_cvv2));
            focusView = cvv2;
            cancel = true;
        }
        
        // Check for valid zipcode
        if (TextUtils.isEmpty(zipcodeString)) {
            zipcode.setError(getString(R.string.error_field_required));
            focusView = zipcode;
            cancel = true;
        } else if (zipcode.length() < 5) {
            cvv2.setError(getString(R.string.error_invalid_zipcode));
            focusView = zipcode;
            cancel = true;
        } else if (!(TextUtils.isDigitsOnly(zipcodeString))) {
            cvv2.setError(getString(R.string.error_invalid_zipcode));
            focusView = zipcode;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
      //  } else {
      //      displayToast(getString(R.string.completed_transaction));
      //  }
    }
    
    

    /** Displays a toast after the transaction has been processed and dismisses the keyboard. */
    public void displayToast(String message) {
        hideSoftKeyboard(this);
        Context currentContext = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast completedTransactionToast = Toast.makeText(currentContext, message, duration);
        completedTransactionToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        completedTransactionToast.show();
    }
    
    /** Sets up the order of all of the EditTexts. */
    @SuppressWarnings("unused")
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
    }
}

