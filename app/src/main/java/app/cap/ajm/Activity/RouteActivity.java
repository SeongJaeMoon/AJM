package app.cap.ajm.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;
import com.melnykov.fab.FloatingActionButton;
import java.io.IOException;
import java.util.List;
import app.cap.ajm.R;
import android.view.MotionEvent;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.Destination;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;

public class RouteActivity extends FragmentActivity implements
        MapView.MapViewEventListener,
        MapView.POIItemEventListener,
        MapView.CurrentLocationEventListener{
    private LocationListener locationListener;
    private static final String LOG_TAG = "RouteActivity";
    private static final int PLACE_PIKER_REQUEST = 1;
    private static final int PLACE_PIKER_REQUEST_EP = 2;
    private EditText spEditext;
    private EditText epEditext;
    private Button findbutton;
    private Button findStartLocation;
    private Button mSearchbymap;
    private MapView mapView;
    private GoogleApiClient mGoogleApiClient;
    private FloatingActionButton myposition;
    public LocationManager locationManager;
    private boolean mapsSelection=false;
    double latitude, longitude;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(getApplicationContext(),getString(R.string.google_api_error),Toast.LENGTH_SHORT).show();
            }
        }).build();
                mGoogleApiClient.connect();


                final Geocoder geocoder = new Geocoder(this);
                // 다음지도 불러오기
                spEditext = (EditText)findViewById(R.id.etOrigin);
                epEditext = (EditText)findViewById(R.id.etDestination);
                findbutton = (Button)findViewById(R.id.btnFindPath);
                findStartLocation = (Button)findViewById(R.id.myFindPath);
                mSearchbymap = (Button)findViewById(R.id.myMapPath);
                myposition = (FloatingActionButton)findViewById(R.id.mypositions);
                mapView = (MapView) findViewById(R.id.map_view);

                mapView.setCurrentLocationEventListener(this);
                mapView.setHDMapTileEnabled(true); // 고해상도 지도 타일 사용
                mapView.setMapViewEventListener(this);
                mapView.setPOIItemEventListener(this);

                findbutton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                String sped1 = spEditext.getText().toString();
                String eped1 = epEditext.getText().toString();
                if (sped1 == null || sped1.length() == 0)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.input_start), Toast.LENGTH_SHORT).show();
                    return;
                } else if (eped1 == null || eped1.length() == 0)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.input_end), Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                    {
                    List<Address> list = null;
                    List<Address> list1 = null;
                    try {
                        list = geocoder.getFromLocationName(sped1, 10);
                        list1 = geocoder.getFromLocationName(eped1, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (list != null && list1 != null)
                    {
                        if (list.size() == 0 || list1.size() == 0)
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_route), Toast.LENGTH_SHORT).show();
                        }
                        else
                            {
                            Address addr = list.get(0);
                            Address addr1 = list1.get(0);
                            double splat = addr.getLatitude();
                            double splon = addr.getLongitude();
                            double edlat = addr1.getLatitude();
                            double edlon = addr1.getLongitude();
                                try {
                                    if(KakaoNaviService.isKakaoNaviInstalled(getApplicationContext())) {
                                        Location kakao = Destination.newBuilder(eped1, edlon, edlat).build();
                                        KakaoNaviParams params = KakaoNaviParams.newBuilder(kakao)
                                                .setNaviOptions(NaviOptions.newBuilder()
                                                        .setCoordType(CoordType.WGS84)
                                                        .setRpOption(RpOption.NO_AUTO)
                                                        .setStartX(splat)
                                                        .setStartY(splon)
                                                        .setStartAngle(200)
                                                        .setVehicleType(VehicleType.TWO_WHEEL).build()).build();
                                        KakaoNaviService.navigate(RouteActivity.this, params);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.locnall.KimGiSa"));
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), getString(R.string.navi_install),Toast.LENGTH_LONG).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                        }
                    }
                }

            }
            });
        //현재 위치를 출발지로
        findStartLocation.setOnClickListener(new View.OnClickListener() {
        @Override
         public void onClick(View v) {
            try {
                spEditext.setText("");

            if (ContextCompat.checkSelfPermission(RouteActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED&&
                    ContextCompat.checkSelfPermission(RouteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_DENIED) {
                locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(android.location.Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                latitude = locationManager.getLastKnownLocation(locationProvider).getLatitude();
                longitude = locationManager.getLastKnownLocation(locationProvider).getLongitude();

                List<Address> list = null;
                list = geocoder.getFromLocation(latitude, longitude, 10);
                if (list != null) {
                    if (list.size() == 0)
                        Toast.makeText(getApplicationContext(), getString(R.string.error_route), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.set_current_loc), Toast.LENGTH_SHORT).show();
                    spEditext.setText(list.get(0).getAddressLine(0));
                }
            }
            }catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),e.toString()+getString(R.string.error_route),Toast.LENGTH_SHORT).show();
            }
         }
         });

        //지도에서 도착지 선택
         mSearchbymap.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (!mapsSelection) {
                     mapsSelection = true;
                     mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                     mapView.setShowCurrentLocationMarker(false);
                     Toast.makeText(getApplicationContext(), getString(R.string.set_map_loc), Toast.LENGTH_LONG).show();
                 }
                 else {
                     Toast.makeText(getApplicationContext(), getString(R.string.exit_map_loc),Toast.LENGTH_SHORT).show();
                     mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                     mapsSelection=false;
                 }
             }
         });

        spEditext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        spEditext.setText("");
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(RouteActivity.this);
                        startActivityForResult(intent, PLACE_PIKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.google_api_error), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        epEditext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        epEditext.setText("");
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(RouteActivity.this);
                        startActivityForResult(intent, PLACE_PIKER_REQUEST_EP);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.google_api_error), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        myposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            }
        });
    }


        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PIKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                spEditext.setText(place.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(LOG_TAG, status.getStatusMessage());
                Toast.makeText(getApplicationContext(), getString(R.string.google_api_error), Toast.LENGTH_LONG).show();

            }
        }
        else if(requestCode == PLACE_PIKER_REQUEST_EP){
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                epEditext.setText(place.getAddress());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(LOG_TAG, status.getStatusMessage());
                Toast.makeText(getApplicationContext(), getString(R.string.google_api_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        if(mapsSelection){
            Toast.makeText(getApplication(),getString(R.string.long_press),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        if(mapsSelection){
            Toast.makeText(getApplication(),getString(R.string.long_press),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        Geocoder geocoder = new Geocoder(this);
    if (mapsSelection){
        try {
            MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
            List<Address> list = null;
            list = geocoder.getFromLocation(mapPointGeo.latitude, mapPointGeo.longitude, 10);
            if (list != null) {
                if (list.size() == 0)
                    Toast.makeText(getApplicationContext(), getString(R.string.error_address), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.set_map_loc_ok), Toast.LENGTH_LONG).show();
                epEditext.setText(list.get(0).getAddressLine(0));
                mapsSelection=false;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString()+getString(R.string.error_default),Toast.LENGTH_LONG).show();
        }
    }
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
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude,mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude), 2, true);
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
    protected void onPause() {
        super.onPause();
        if (locationManager!=null){
            locationManager.removeUpdates(locationListener);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager!=null){
            locationManager.removeUpdates(locationListener);
        }
        mapView.setShowCurrentLocationMarker(false);
    }
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
    }
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }
    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }
    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }
}
