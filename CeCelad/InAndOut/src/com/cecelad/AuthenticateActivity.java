package com.cecelad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AuthenticateActivity extends Activity {

	private static final String TAG = "InAndOut - AuthenticateActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate);

		final EditText uname = (EditText) findViewById(R.id.username_edittext);
		final EditText passwd = (EditText) findViewById(R.id.password_edittext);
		final Button loginButton = (Button) findViewById(R.id.authenticate_button);

		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (checkPassword(uname.getText(), passwd.getText())) {
					Intent registerIntent = new Intent(
							AuthenticateActivity.this,
							RegisterThingActivity.class);
					startActivity(registerIntent);
				} else {
					uname.setText("");
					passwd.setText("");
				}
			}
		});
	}

	/**
	 * Vérifie si user et password corrects
	 * @param uname
	 * @param passwd
	 * @return
	 */
	private boolean checkPassword(Editable uname, Editable passwd) {
		// Just pretending to extract text and check password
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "Entered the onDestroy() method");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "Entered the onPause() method");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "Entered the onRestart() method");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "Entered the onResume() method");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "Entered the onStart() method");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "Entered the onStop() method");
		super.onStop();
	}
}