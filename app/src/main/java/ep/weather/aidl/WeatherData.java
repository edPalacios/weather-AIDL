package ep.weather.aidl;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 *
 * This class is a Plain Old Java Object (POJO) used for data
 * transport within the WeatherService app.  This POJO implements the
 * Parcelable interface to enable IPC between the WeatherActivity and
 * the WeatherServiceSync and WeatherServiceAsync. It represents the
 * response Json obtained from the Open Weather Map API, e.g., a call
 * to http://api.openweathermap.org/data/2.5/weather?q=Nashville,TN
 * might return the following Json data:
 */
public class WeatherData implements Parcelable, Serializable {
    /*
     * These data members are the local variables that will store the
     * WeatherData's state
     */
    public String mName;
    public String mIconCode;
    public double mSpeed;
    public double mDeg;
    public double mTemp;
    public long mHumidity;
    public long mSunrise;
    public long mSunset;
    public double mLon;
    public double mLat;
    public String mCountry;
    public byte[] mIcon;

    public WeatherData(String mName, double mTemp, double mLon, double mLat) {
        this.mName = mName;
        this.mTemp = mTemp;
        this.mLon = mLon;
        this.mLat = mLat;
    }

    /**
     * Constructor
     *
     * @param name
     * @param speed
     * @param deg
     * @param temp
     * @param humidity
     * @param sunrise
     * @param sunset
     */
    public WeatherData(String name,
                       String icon,
                       double speed,
                       double deg,
                       double temp,
                       long humidity,
                       long sunrise,
                       long sunset,
                       double lon,
                       double lat,
                       String country) {
        mName = name;
        mIconCode = icon;
        mSpeed = speed;
        mDeg = deg;
        mTemp = temp;
        mHumidity = humidity;
        mSunrise = sunrise;
        mSunset = sunset;
        mLon = lon;
        mLat = lat;
        mCountry = country;
    }

    /**
     * Provides a printable representation of this object.
     */
    @Override
    public String toString() {
        return "WeatherData [name=" + mName
            + ", icon code=" +mIconCode
            + ", speed=" + mSpeed
            + ", deg=" + mDeg
            + ", temp=" + mTemp
            + ", humidity=" + mHumidity
            + ", sunrise=" + mSunrise
            + ", sunset=" + mSunset
            + ", lon=" + mLon
            + ", lat=" + mLat
            + ", country=" + mCountry + "]";
    }

    /*
     * BELOW THIS is related to Parcelable Interface.
     */

    /**
     * A bitmask indicating the set of special object types marshaled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write this instance out to byte contiguous memory.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mIconCode);
        dest.writeDouble(mSpeed);
        dest.writeDouble(mDeg);
        dest.writeDouble(mTemp);
        dest.writeLong(mHumidity);
        dest.writeLong(mSunrise);
        dest.writeLong(mSunset);
        dest.writeDouble(mLon);
        dest.writeDouble(mLat);
        dest.writeString(mCountry);
    }

    /**
     * Private constructor provided for the CREATOR interface, which
     * is used to de-marshal an WeatherData from the Parcel of data.
     * <p>
     * The order of reading in variables HAS TO MATCH the order in
     * writeToParcel(Parcel, int)
     *
     * @param in
     */
    private WeatherData(Parcel in) {
        mName = in.readString();
        mIconCode = in.readString();
        mSpeed = in.readDouble();
        mDeg = in.readDouble();
        mTemp = in.readDouble();
        mHumidity = in.readLong();
        mSunrise = in.readLong();
        mSunset = in.readLong();
        mLon = in.readDouble();
        mLat = in.readDouble();
        mCountry = in.readString();
    }

    /**
     * public Parcelable.Creator for WeatherData, which is an
     * interface that must be implemented and provided as a public
     * CREATOR field that generates instances of your Parcelable class
     * from a Parcel.
     */
    public static final Creator<WeatherData> CREATOR =
        new Creator<WeatherData>() {
            public WeatherData createFromParcel(Parcel in) {
                return new WeatherData(in);
            }

            public WeatherData[] newArray(int size) {
                return new WeatherData[size];
            }
        };
}
