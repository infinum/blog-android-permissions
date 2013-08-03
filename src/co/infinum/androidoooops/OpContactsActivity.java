package co.infinum.androidoooops;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class OpContactsActivity extends ListActivity {

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new ReadContactsAsyncTask().execute();
	}

	private class ReadContactsAsyncTask extends
			AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(OpContactsActivity.this,
					"Loading contacts", "");
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {

			return readContacts();
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			ArrayAdapter<String> adapter = null;
			if (result.size() > 0) {
				adapter = new ArrayAdapter<String>(OpContactsActivity.this,
						android.R.layout.simple_list_item_1,
						result.toArray(new String[result.size()]));
			} else {
				adapter = new ArrayAdapter<String>(
						OpContactsActivity.this,
						android.R.layout.simple_list_item_1,
						new String[] { "Empty contacts - either you don't have any contacts and I feel sorry for you OR App Ops blocked the permission to read you contacts ;)" });
			}

			setListAdapter(adapter);

			if (!isFinishing() && progressDialog != null
					&& progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}

	}
	
	private ArrayList<String> readContacts() {
		ArrayList<String> contactData = new ArrayList<String>();
		ContentResolver cr = getContentResolver();
		Cursor cursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		while (cursor.moveToNext()) {
			try {
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String hasPhone = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (Integer
						.parseInt(cursor.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor phones = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					while (phones.moveToNext()) {
						String phoneNumber = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						contactData.add(name + ": " + phoneNumber);
					}
					phones.close();
				}
			} catch (Exception e) {
			}
		}
		return contactData;
	}
}
