package co.infinum.androidoooops;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class OooopsActivity extends Activity implements OnClickListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private ImageButton btnTakePhoto;
	private ImageButton btnLocaiton;
	private ImageButton btnContacts;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private LocationManager lm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oooops);

		btnTakePhoto = (ImageButton) findViewById(R.id.btn_take_photo);
		btnLocaiton = (ImageButton) findViewById(R.id.btn_locate_me);
		btnContacts = (ImageButton) findViewById(R.id.btn_contacts);

		btnTakePhoto.setOnClickListener(this);
		btnLocaiton.setOnClickListener(this);
		btnContacts.setOnClickListener(this);

		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(5000);
		
		mLocationClient = new LocationClient(this, this, this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		if (lm != null){
			lm.removeUpdates(locationListenerGps);
			lm.removeUpdates(locationListenerNetwork); 
		}

		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_take_photo:
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			startActivityForResult(intent, 100);
			break;
		case R.id.btn_locate_me:
			lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerNetwork);
			
			if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

				Location lastLocation = mLocationClient.getLastLocation();
				if (lastLocation == null) {
					Toast.makeText(this, "PLAY SERVICES: No location available!",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							this,
							"PLAY SERVICES Locaiton: lat-" + lastLocation.getLatitude()
									+ ", long-" + lastLocation.getLongitude(),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "PLAY SERVICES: not available",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.btn_contacts:
			Intent contactsIntent = new Intent(this, OpContactsActivity.class);
			startActivity(contactsIntent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onConnected(Bundle arg0) {
//		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		String msg = "PLAY SERVICES Updated Location: "
				+ Double.toString(location.getLatitude()) + ","
				+ Double.toString(location.getLongitude());
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	protected android.location.LocationListener locationListenerGps = new android.location.LocationListener() {
		public void onLocationChanged(Location location) {
			Toast.makeText(OooopsActivity.this, "LOCATION MANAGER: retrieved gps", Toast.LENGTH_SHORT).show();
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	protected android.location.LocationListener locationListenerNetwork = new android.location.LocationListener() {
		public void onLocationChanged(Location location) {
			Toast.makeText(OooopsActivity.this, "LOCATION MANAGER: retrieved network", Toast.LENGTH_SHORT).show();
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
}
