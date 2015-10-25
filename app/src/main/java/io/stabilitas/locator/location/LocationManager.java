package io.stabilitas.locator.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.ReplaySubject;

/**
 * Created by David P
 */
public class LocationManager
        implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    public static final int LOCATION_INTERVAL = 5000;
    public static final int LOCATION_FASTEST_INTERVAL = 1000;

    private GoogleApiClient client;
    private Context context;
    private boolean isLocationRequested;
    private LocationRequest locationRequest;
    private ReplaySubject<Location> locations = ReplaySubject.createWithSize(1);

    private void initClient(Context context) {
        this.context = context;

        client= new GoogleApiClient.Builder(this.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void connect(Context context) {
        initClient(context);
        if (client != null) {
            client.connect();
        }
    }

    public void startRequestLocation() {
        if (!isLocationRequested && client != null && client.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
            isLocationRequested = true;
        }
    }

    public void stopRequestLocation() {
        if (isLocationRequested && client != null && client.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            isLocationRequested = false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(client);

        if (location != null) {
            Log.d("LOCATION", "Lat, Lon: " + location.getLatitude() + "," + location.getLongitude());
            locations.onNext(location);
        }

        startRequestLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("LOCATION", "Error trying to connect to google play services");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("LOCATION", "Error trying to connect to google play services");
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d("LOCATION", "New Location: " + location.getLatitude() + "," + location.getLongitude());

        locations.onNext(location);
    }

    public Observable<Location> getLastLocation() {
        return getLocation().last();
    }

    public Observable<Location> getLocation() {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                subscriber.add(locations.subscribe(subscriber));
            }
        });
    }
}
