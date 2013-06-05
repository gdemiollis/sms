package com.and1droid.smsquebec;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText message;
    private EditText destinataire;
    private TextView textViewChar;
    int maxChar;
    protected SharedPreferences defaultSharedPreferences;
    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (EditText) findViewById(R.id.editTextMessage);
        destinataire = (EditText) findViewById(R.id.editTextDestinataire);
        textViewChar = (TextView) findViewById(R.id.textViewChar);
        message.addTextChangedListener(watcher);
        if (getIntent() != null && Intent.ACTION_SENDTO.equals(getIntent().getAction())) {
            String destinationNumber = getIntent().getDataString();
            try {
                destinationNumber = URLDecoder.decode(destinationNumber, "UTF-8").replace("smsto:", "").replace("sms:", "");
                destinataire.setText(PhoneNumberUtils.formatNumber(destinationNumber));
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(this, R.string.cannot_get_number, Toast.LENGTH_LONG).show();
            }
        }
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int charMax = getResources().getInteger(R.integer.maxchar);
        maxChar = computeCharCount(charMax);
        textViewChar.setText(String.format(getString(R.string.char_avalaible), maxChar - message.getText().toString().length()));
        // Programmaticaly set maxlength
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(maxChar);
        message.setFilters(filterArray);
    }

    protected int computeCharCount(int charMax) {
        boolean isSignature = isSignature();
        if (isSignature) {
            int length = getSignature().length() + 2;
            charMax = charMax - length;
        }
        return charMax;
    }

    private String getSignature() {
        return defaultSharedPreferences.getString(SettingsActivity.SIGNATURE_KEY, "");
    }

    private boolean isSignature() {
        return defaultSharedPreferences.getBoolean(SettingsActivity.CHECK_SIGNATURE_KEY, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            startActivity(SettingsActivity.newIntent(this));
            return true;
        case R.id.action_send:
            sendMessage();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMessage() {
        String dest = destinataire.getText().toString();
        String mess = message.getText().toString();
        if (TextUtils.isEmpty(dest)) {
            Toast toast = Toast.makeText(MainActivity.this, R.string.please_num, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            return;
        }
        if (dest.length() < 10) {
            Toast toast = Toast.makeText(MainActivity.this, R.string.please_set_10_number, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();return;
        }
        if (TextUtils.isEmpty(mess)) {
            Toast toast = Toast.makeText(MainActivity.this, R.string.please_message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();return;
        }
        Texto texto = getTexto(dest, mess);
        new SendMessageTask().execute(texto);
    }

    private Texto getTexto(String dest, String mess) {
        Texto texto = new Texto(dest, mess);
        boolean addSignature = isSignature();
        if (addSignature) {
            texto.setFrom(getSignature());
        }
        return texto;
    }

    private class SendMessageTask extends AsyncTask<Texto, Void, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "", MainActivity.this.getString(R.string.sending));
        }

        @Override
        protected Boolean doInBackground(Texto... params) {
            try {
                TelusMessenger.send(params[0]);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result) {
                Toast.makeText(MainActivity.this, R.string.sent, Toast.LENGTH_LONG).show();
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.send_error).setTitle(R.string.error).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create().show();
            }
        }
    }

    public void pickContact(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts")).setType(Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = { Phone.NUMBER, Phone.DISPLAY_NAME };
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();
                String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                destinataire.setText(number);
            }
        }
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable editable) {
            textViewChar.setText(String.format(getString(R.string.char_avalaible), maxChar - editable.length()));
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

    };
}
