package io.stabilitas.locator.activities;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import io.stabilitas.locator.R;
import io.stabilitas.locator.location.LocationManager;
import io.stabilitas.locator.model.MapItem;
import io.stabilitas.locator.model.Report;
import io.stabilitas.locator.networking.Callback;
import io.stabilitas.locator.networking.NetworkError;
import io.stabilitas.locator.networking.ReportsClient;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class HomeActivity extends FragmentActivity {

    private GoogleMap map;
    private final ReportsClient client = new ReportsClient();
    private List<Report> reports = new ArrayList<>();
    private ClusterManager<MapItem> clusterManager;

    private LocationManager locationManager;
    private Subscription subscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        subscription = locationManager
                .getLocation()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getOnSubscriber());
        locationManager.startRequestLocation();
    }

    @Override
    protected void onPause() {
        locationManager.stopRequestLocation();
        if (subscription != null) {
            subscription.unsubscribe();
        }

        super.onPause();
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        clusterManager = new ClusterManager<>(this, map);

        map.setOnCameraChangeListener(clusterManager);
        map.setMyLocationEnabled(true);

        if (locationManager == null) {
            locationManager = new LocationManager();
        }
        locationManager.connect(this);

        getReports();
    }

    private void getReports() {
        client.getReports(new Callback<List<Report>>() {
            @Override
            public void onSuccess(List<Report> reports) {
                HomeActivity.this.reports.addAll(reports);
                drawMarkers();
            }

            @Override
            public void onError(NetworkError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawMarkers() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Report report : reports) {
                    LatLng position = new LatLng(report.getLatitude(), report.getLongitud());
                    clusterManager.addItem(new MapItem(position));
                }
            }
        });
    }

    private Action1<Location> getOnSubscriber() {
        return new Action1<Location>() {
            @Override
            public void call(Location location) {
                centerMap(location);
            }
        };
    }

    private void centerMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        if (map != null) map.animateCamera(cameraUpdate);
    }


}
