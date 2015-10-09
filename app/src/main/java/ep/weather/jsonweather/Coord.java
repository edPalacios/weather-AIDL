package ep.weather.jsonweather;

/**
 * Created by Eduardo on 03/06/2015.
 */
public class Coord {

    public final static String lon_JSON = "lon";
    public final static String lat_JSON = "lat";

    private double mLon;
    private double mLat;

    public double getLon() {
        return mLon;
    }


    public void setLon(double lon) {
        mLon = lon;
    }


    public double getLat() {
        return mLat;
    }


    public void setLat(double lat) {
        mLat = lat;
    }
}
