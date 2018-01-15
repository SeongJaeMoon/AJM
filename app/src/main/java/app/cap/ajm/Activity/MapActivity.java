package app.cap.ajm.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import app.cap.ajm.Helper.TrackDBhelper;
import app.cap.ajm.Model.TrackPoint;
import app.cap.ajm.R;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import static net.daum.mf.map.api.MapPoint.mapPointWithGeoCoord;

public class MapActivity extends FragmentActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener{
    private List<TrackPoint> trackPointList;
    private TrackDBhelper trackDBhelper;
    private MapView mapView;
    private MapPOIItem mapPOIItem, mapPOIItem1;
    private MapPolyline mapPolyline;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.setHDMapTileEnabled(true); // 고해상도 지도 타일 사용
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        trackDBhelper = new TrackDBhelper(getApplicationContext());
        trackDBhelper.open();
        trackPointList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
        final String starts = intent.getStringExtra("startTime");
        final String ends = intent.getStringExtra("endTime");
        trackPointList = trackDBhelper.fetchBetweenTime(starts, ends);
        if (trackPointList!=null) {
            TrackPoint trackPoint = trackPointList.get(0);
            double startLat = trackPoint.getLat();
            double startLng = trackPoint.getLng();
            String startTitle = getString(R.string.start_positon);
            MapPoint startMarkerPoint = mapPointWithGeoCoord(startLat, startLng);
            mapPOIItem = new MapPOIItem();
            mapPOIItem.setItemName(startTitle);
            mapPOIItem.setTag(0);
            mapPOIItem.setMapPoint(startMarkerPoint);
            mapPOIItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            mapPOIItem.setCustomImageResourceId(R.drawable.custom_poi_marker_start);
            mapPOIItem.setCustomImageAutoscale(false);
            mapPOIItem.setCustomImageAnchor(0.5f, 1.0f);
            mapView.addPOIItem(mapPOIItem);
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        trackPointList = trackDBhelper.fetchBetweenTime(starts, ends);
                        mapPolyline = new MapPolyline();
                        if (trackPointList != null && trackPointList.size() > 0) {
                            for (i = 0; i < trackPointList.size()-1; i++) {
                                mapPolyline.setLineColor(Color.argb(128, 50, 0, 255));
                                mapPolyline.addPoint(MapPoint.mapPointWithGeoCoord(trackPointList.get(i).getLat(), trackPointList.get(i).getLng()));
                                mapView.addPolyline(mapPolyline);
                            }
                        }
                    }

                });
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TrackPoint trackPoint1 = trackPointList.get(i);
            double endLat = trackPoint1.getLat();
            double endLng = trackPoint1.getLng();
            String endTitle = getString(R.string.arrive_position);
            MapPoint endMarkerPoint = mapPointWithGeoCoord(endLat, endLng);
            mapPOIItem1 = new MapPOIItem();
            mapPOIItem1.setItemName(endTitle);
            mapPOIItem1.setTag(0);
            mapPOIItem1.setMapPoint(endMarkerPoint);
            mapPOIItem1.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            mapPOIItem1.setCustomImageResourceId(R.drawable.custom_poi_marker_end);
            mapPOIItem1.setCustomImageAutoscale(false);
            mapPOIItem1.setCustomImageAnchor(0.5f, 1.0f);
            mapView.addPOIItem(mapPOIItem1);
            trackDBhelper.close();
            MapPointBounds bounds = new MapPointBounds(startMarkerPoint, endMarkerPoint);
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, 20, 0f, 12f));

        }else {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_data),Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplication(), getString(R.string.wrong_map), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }
    @Override
    public void onMapViewInitialized(MapView mapView) {
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        mapView.setMapCenterPointAndZoomLevel(mapPointWithGeoCoord(mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude,mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude), 2, true);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {}

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {}
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.removeAllPOIItems();
        mapView.removeAllPolylines();
    }

}
