package course.labs.contentproviderlab;

import java.util.ArrayList;

import course.labs.contentproviderlab.provider.PlaceBadgesContract;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class PlaceViewActivity extends ListActivity implements
		LocationListener, LoaderCallbacks<Cursor> {
	private static final long FIVE_MINS = 5 * 60 * 1000;

	private static String TAG = "Lab-ContentProvider";

	// The last valid location reading
	private Location mLastLocationReading;

	// The ListView's adapter
	// private PlaceViewAdapter mAdapter;
	private PlaceViewAdapter mCursorAdapter;

	// default minimum time between new location readings
	private long mMinTime = 5000;

	// default minimum distance between old and new readings.
	private float mMinDistance = 1000.0f;

	// Reference to the LocationManager
	private LocationManager mLocationManager;

	// A fake location provider used for testing
	private MockLocationProvider mMockLocationProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // TODO - Set up the app's user interface
        // This class is a ListActivity, so it has its own ListView


        // TODO - add a footerView to the ListView
        // You can use footer_view.xml to define the footer
		LayoutInflater inflater = getLayoutInflater();
		final TextView footerView = (TextView) inflater.inflate(R.layout.footer_view, null, false);
		getListView().addFooterView(footerView);

        // TODO - When the footerView's onClick() method is called, it must issue the
        // following log call
        // log("Entered footerView.OnClickListener.onClick()");
        
        // footerView must respond to user clicks.
        // Must handle 3 cases:
        // 1) The current location is new - download new Place Badge. Issue the
        // following log call:
        // log("Starting Place Download");

        // 2) The current location has been seen before - issue Toast message.
        // Issue the following log call:
        // log("You already have this location badge");
        
        // 3) There is no current location - response is up to you. The best
        // solution is to disable the footerView until you have a location.
        // Issue the following log call:
        // log("Location data is not available");
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		footerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				log("Entered footerView.OnClickListener.onClick()");
				Location currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (currentLocation != null){
					if (!mCursorAdapter.intersects(currentLocation)){
						log("Starting Place Download");
						new PlaceDownloaderTask(PlaceViewActivity.this).execute(currentLocation);
					} else if (mCursorAdapter.intersects(currentLocation))	{
						log("You already have this location badge");
						Toast.makeText(PlaceViewActivity.this, "You already have this location badge.",Toast.LENGTH_SHORT).show();
					}
				}else{
					log("Location data is not available");
					footerView.setClickable(false);
				}

			}
		});
		
		// TODO - Create and set empty PlaceViewAdapter
        // ListView's adapter should be a PlaceViewAdapter called mCursorAdapter
		mCursorAdapter = new PlaceViewAdapter(getApplicationContext(), null, 0);
		setListAdapter(mCursorAdapter);
		
		
		// TODO - Initialize a CursorLoader
		getLoaderManager().initLoader(0, null, this);
        
	}

	@Override
	protected void onResume() {
		super.onResume();

		mMockLocationProvider = new MockLocationProvider(
				LocationManager.NETWORK_PROVIDER, this);

		// TODO - Check NETWORK_PROVIDER for an existing location reading.
		// Only keep this last reading if it is fresh - less than 5 minutes old.

		mLastLocationReading = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (mLastLocationReading != null && age(mLastLocationReading)> FIVE_MINS) {
			mLastLocationReading = null;
		}
		
		
		
		// TODO - Register to receive location updates from NETWORK_PROVIDER
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,mMinTime, mMinDistance,
				this);
		
		
		
	}

	@Override
	protected void onPause() {

		mMockLocationProvider.shutdown();

		// TODO - Unregister for location updates
		mLocationManager.removeUpdates(this);
		
		
		super.onPause();
	}

	public void addNewPlace(PlaceRecord place) {

		log("Entered addNewPlace()");

		mCursorAdapter.add(place);

	}

	@Override
	public void onLocationChanged(Location currentLocation) {

		// TODO - Handle location updates
		// Cases to consider
		// 1) If there is no last location, keep the current location.
		// 2) If the current location is older than the last location, ignore
		// the current location
		// 3) If the current location is newer than the last locations, keep the
		// current location.
		if (mLastLocationReading == null){
			mLastLocationReading = currentLocation;
		}else {
			if(age(currentLocation) < age(mLastLocationReading)){
				mLastLocationReading = currentLocation;
			}
		}

	
	
	
	}

	@Override
	public void onProviderDisabled(String provider) {
		// not implemented
	}

	@Override
	public void onProviderEnabled(String provider) {
		// not implemented
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// not implemented
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		log("Entered onCreateLoader()");
		
		String columnsToExtract[] = new String[] {
				PlaceBadgesContract._ID,
				PlaceBadgesContract.FLAG_BITMAP_PATH,
				PlaceBadgesContract.COUNTRY_NAME,
				PlaceBadgesContract.PLACE_NAME,
				PlaceBadgesContract.LAT,
				PlaceBadgesContract.LON};

		// TODO - Create a new CursorLoader and return it
		CursorLoader cursorLoader = null;
        switch (arg0) {
            case 0:
                // Returns a new CursorLoader
            	cursorLoader = new CursorLoader(
            				this,   // Parent activity context
            				PlaceBadgesContract.CONTENT_URI ,        // Table to query
            				columnsToExtract,     // Projection to return
                            null,            // No selection clause
                            null,            // No selection arguments
                            null             // Default sort order
            			);
            	break;
            default:
                // An invalid id was passed in
            	cursorLoader = null;
        }

        return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> newLoader, Cursor newCursor) {

		// TODO - Swap in the newCursor
		mCursorAdapter.swapCursor(newCursor);
	
    }

	@Override
	public void onLoaderReset(Loader<Cursor> newLoader) {

		// TODO - Swap in a null Cursor
		mCursorAdapter.swapCursor(null);
	
    }

	private long age(Location location) {
		return System.currentTimeMillis() - location.getTime();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.print_badges:
			ArrayList<PlaceRecord> currData = mCursorAdapter.getList();
			for (int i = 0; i < currData.size(); i++) {
				log(currData.get(i).toString());
			}
			return true;
		case R.id.delete_badges:
			mCursorAdapter.removeAllViews();
			return true;
		case R.id.place_one:
			mMockLocationProvider.pushLocation(37.422, -122.084);
			return true;
		case R.id.place_invalid:
			mMockLocationProvider.pushLocation(0, 0);
			return true;
		case R.id.place_two:
			mMockLocationProvider.pushLocation(38.996667, -76.9275);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private static void log(String msg) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i(TAG, msg);
	}
}
