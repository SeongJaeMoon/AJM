package app.cap.ajm.Compatable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.kakao.kakaonavi.Destination;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;
import com.melnykov.fab.FloatingActionButton;
import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapLayout;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import app.cap.ajm.R;

public class SearchActivity extends FragmentActivity implements
		MapView.MapViewEventListener,
		MapView.POIItemEventListener{
    private static final String LOG_TAG = "SearchDemoActivity";
	private FloatingActionButton mypositon;
    private MapView mMapView;
    private EditText mEditTextQuery;
    private Button mButtonSearch;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
    private int n = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
        mMapView = (MapView)findViewById(R.id.map_view);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        mEditTextQuery = (EditText) findViewById(R.id.editTextQuery); // 검색창
        mButtonSearch = (Button) findViewById(R.id.buttonSearch); // 검색버튼
		mypositon = (FloatingActionButton)findViewById(R.id.myserachposition);
        mButtonSearch.setOnClickListener(new OnClickListener() { // 검색버튼 클릭 이벤트 리스너
			@Override
			public void onClick(View v) {
				try {
					String query = mEditTextQuery.getText().toString();
					if (query == null || query.length() == 0) {
						showToast("검색어를 입력하세요.");
						return;
					}
					hideSoftKeyboard(); // 키보드 숨김
					GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
					double latitude = geoCoordinate.latitude; // 위도
					double longitude = geoCoordinate.longitude; // 경도
					int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
					int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
					String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;
					Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
					Toast.makeText(getApplicationContext(), String.format(getString(R.string.search_10km), query),Toast.LENGTH_SHORT).show();
					searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
						@Override
						public void onSuccess(List<Item> itemList) {
							mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
							showResult(itemList); // 검색 결과 보여줌
						}

						@Override
						public void onFail(){
							Log.w("오류: ","오류");
							showToast(getString(R.string.not_connected));
						}
					});
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
		mypositon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
			}
		});
    }


    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
    	
    	private final View mCalloutBalloon;
    	
    	public CustomCalloutBalloonAdapter() {
    		mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
		}

		@Override
		public View getCalloutBalloon(MapPOIItem poiItem) {
			if (poiItem == null) return null;
			Item item = mTagItemMap.get(poiItem.getTag());
			if (item == null) return null;
			ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
			TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
			textViewTitle.setText(item.title);
			TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
			textViewDesc.setText(item.address);
			imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
			return mCalloutBalloon;
		}

		@Override
		public View getPressedCalloutBalloon(MapPOIItem poiItem) {
			return null;
		}
    	
    }
    
    private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditTextQuery.getWindowToken(), 0);
    }

    public void onMapViewInitialized(MapView mapView) {
		ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
	}
    @Override
	protected void onPause(){
		super.onPause();
	}
    @Override
	protected void onDestroy(){
		super.onDestroy();
		mMapView.setShowCurrentLocationMarker(false);

	}

    private void showToast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(SearchActivity.this, text, Toast.LENGTH_SHORT).show();
			}
		});
    }
    
	private void showResult(List<Item> itemList) 	{
		MapPointBounds mapPointBounds = new MapPointBounds();
		
		for (int i = 0; i < itemList.size(); i++) {
			Item item = itemList.get(i);

			MapPOIItem poiItem = new MapPOIItem();
			poiItem.setItemName(item.title+", "+item.distance);
			poiItem.setTag(i);
			MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
			poiItem.setMapPoint(mapPoint);
			mapPointBounds.add(mapPoint);
			poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
			poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
			poiItem.setCustomImageAutoscale(false);
			poiItem.setCustomImageAnchor(0.5f, 1.0f);
			mMapView.addPOIItem(poiItem);
			mTagItemMap.put(poiItem.getTag(), item);
		}
		
		mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
		mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
		MapPOIItem[] poiItems = mMapView.getPOIItems();
		if (poiItems.length > 0) {
			mMapView.selectPOIItem(poiItems[0], false);
		}
	}
	
	private Drawable createDrawableFromUrl(String url) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
	
	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
		Item item = mTagItemMap.get(mapPOIItem.getTag());
	NavigationDialog(item.newAddress, item.latitude, item.longitude);
	}
	
	@Override
	@Deprecated
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
	}

	@Override
	public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
	}

	@Override
	public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
	}

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
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
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
    }
	public void NavigationDialog(final String des, final Double lat, final Double lon){
		android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(SearchActivity.this);
		alertDialog.setTitle(getString(R.string.direction));
		alertDialog.setMessage(getString(R.string.set_map_loc_ok));
		alertDialog.setPositiveButton(getString(R.string.start), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				try {
				if(KakaoNaviService.isKakaoNaviInstalled(getApplicationContext())) {
					com.kakao.kakaonavi.Location kakao = Destination.newBuilder(des, lon, lat).build();
					KakaoNaviParams params = KakaoNaviParams.newBuilder(kakao)
							.setNaviOptions(NaviOptions.newBuilder()
									.setCoordType(CoordType.WGS84)
									.setRpOption(RpOption.NO_AUTO)
									.setStartAngle(200)
									.setVehicleType(VehicleType.TWO_WHEEL).build()).build();
					KakaoNaviService.navigate(SearchActivity.this, params);
				}
				else
				{
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.locnall.KimGiSa"));
					startActivity(intent);
					Toast.makeText(getApplicationContext(), getString(R.string.navi_install),Toast.LENGTH_LONG).show();
				}
			}catch (Exception e){
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),e.toString()+getString(R.string.error_default),Toast.LENGTH_LONG).show();
			}

		}
		});
		alertDialog.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}
}
