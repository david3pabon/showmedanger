package io.stabilitas.locator.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import io.stabilitas.locator.R;
import io.stabilitas.locator.model.MapItem;
import io.stabilitas.locator.model.Report;
import io.stabilitas.locator.networking.Callback;
import io.stabilitas.locator.networking.NetworkError;
import io.stabilitas.locator.networking.ReportsClient;

public class HomeActivity extends FragmentActivity {

    private GoogleMap map;
    private final ReportsClient client = new ReportsClient();
    private List<Report> reports = new ArrayList<>();
    private ClusterManager<MapItem> clusterManager;

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
                for (Report report: reports) {
                    LatLng position = new LatLng(report.getLatitude(), report.getLongitud());
                    clusterManager.addItem(new MapItem(position));
                }
            }
        });
    }


}
