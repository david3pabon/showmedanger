package io.stabilitas.locator.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by David Pabon (@david3pabon)
 */
public class MapItem implements ClusterItem {
    private final LatLng position;

    public MapItem(LatLng position) {
        this.position = position;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }
}
