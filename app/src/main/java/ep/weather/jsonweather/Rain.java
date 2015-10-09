package ep.weather.jsonweather;

/**
 * Created by Eduardo on 03/06/2015.
 */
public class Rain {

    public final static String threeH_JSON = "3h";

    private long mThreeH;

    public static String getThreeH_JSON() {
        return threeH_JSON;
    }

    public long getThreeH() {
        return mThreeH;
    }

    public void setThreeH(long threeH) {
        this.mThreeH = threeH;
    }
}
