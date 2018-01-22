package app.cap.ajm.Util;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public enum Utils {
    INSTANCE;

        public String getCurrentSec(){
            long now = System.currentTimeMillis();
            return  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date(now));
        }

        public String getGeocode(final Context context, double lat, double lng) throws IOException {
            String address = null;
            final Geocoder geocoder = new Geocoder(context, Locale.KOREA);
            List<Address> addr = null;
                addr = geocoder.getFromLocation(lat, lng, 1);
                if (addr!=null&&addr.size()>0){
                    address = addr.get(0).getThoroughfare().toString();
                    Log.w("Main :", address);
                }
            return address;
        }
}
