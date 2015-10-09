package ep.weather.jsonweather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.Log;

/**
 * Here i have only use the parseJsonStreamSingle() to get all the streams in the Weather API.
 *
 *
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of JsonWeather objects that contain this data.
 */
public class WeatherJSONParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();

    /**
     * Parse the @a inputStream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonStream(InputStream inputStream)
        throws IOException {
        Log.d(TAG, "parseJsonStream");
        List<JsonWeather> weatherList = new ArrayList<>();
        try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"))) {
            weatherList.add(parseJsonStreamSingle(reader));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "city not found or was country.", e);
        }
        if (checkIfWeatherDataIsValid(weatherList)) return null;
        return weatherList;
    }

    private boolean checkIfWeatherDataIsValid(List<JsonWeather> weatherList) {
        for (JsonWeather jsonWeather : weatherList) {
            if (jsonWeather.getCod() > 400) { // check response code from API
                Log.e(TAG, "input city name is wrong or wasn't found in the API " +jsonWeather.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * Parse a single Json stream and convert it into a JsonWeather
     * object.
     */
    public JsonWeather parseJsonStreamSingle(JsonReader reader)
        throws IOException {
        JsonWeather jsonWeather = new JsonWeather();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case JsonWeather.name_JSON:
                    jsonWeather.setName(reader.nextString());
                    break;
                case JsonWeather.dt_JSON:
                    jsonWeather.setDt(reader.nextLong());
                    break;
                case JsonWeather.id_JSON:
                    jsonWeather.setId(reader.nextLong());
                    break;
                case JsonWeather.cod_JSON:
                    jsonWeather.setCod(reader.nextLong());
                    break;
                case JsonWeather.base_JSON:
                    jsonWeather.setBase(reader.nextString());
                    break;
                case JsonWeather.sys_JSON:
                    jsonWeather.setSys(parseSys(reader));
                    break;
                case JsonWeather.weather_JSON:
                    jsonWeather.setWeather(parseWeathers(reader));
                    break;
                case JsonWeather.main_JSON:
                    jsonWeather.setMain(parseMain(reader));
                    break;
                case JsonWeather.wind_JSON:
                    jsonWeather.setWind(parseWind(reader));
                    break;
                case JsonWeather.coord_JSON:
                    jsonWeather.setCoord(parseCoord(reader));
                    break;
                default:
                    if (reader.hasNext())
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return jsonWeather;
    }


    /**
     * Parse a Json stream and convert it into a List of JsonWeather
     * objects.
     * Not in use
     */
    public List<JsonWeather> parseJsonWeatherArray (JsonReader reader) throws IOException {
            return null;
        }

    /**
     * Parse a Json stream and return a JsonWeather object.
     * Not in use
     */
    public JsonWeather parseJsonWeather(JsonReader reader) 
        throws IOException {
        return null;
    }
    
    /**
     * Parse a Json stream and return a List of Weather objects.
     */
    public List<Weather> parseWeathers(JsonReader reader) {
        List<Weather> weatherList = new ArrayList<>();
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                weatherList.add(parseWeather(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            Log.e(TAG, "Error trying to open or close the reader array", e);
        }
        return weatherList;
    }

    /**
     * Parse a Json stream and return a Weather object.
     */
    public Weather parseWeather(JsonReader reader) {
        Weather weather = new Weather();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case Weather.description_JSON:
                        weather.setDescription(reader.nextString());
                        break;
                    case Weather.icon_JSON:
                        weather.setIcon(reader.nextString());
                        break;
                    case Weather.main_JSON:
                        weather.setMain(reader.nextString());
                        break;
                    case Weather.id_JSON:
                        weather.setId(reader.nextLong());
                        break;
                    default:
                        if (reader.hasNext())
                        reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            Log.e(TAG, "Weather (object) error", e);
        }
        return weather;
    }

    /**
     * Parse a Json stream and return a Main Object.
     */
    public Main parseMain(JsonReader reader)
        throws IOException {
        Main main = new Main();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case Main.grndLevel_JSON:
                        main.setGrndLevel(reader.nextDouble());
                        break;
                    case Main.humidity_JSON:
                        main.setHumidity(reader.nextLong());
                        break;
                    case Main.pressure_JSON:
                        main.setPressure(reader.nextDouble());
                        break;
                    case Main.seaLevel_JSON:
                        main.setSeaLevel(reader.nextDouble());
                        break;
                    case Main.temp_JSON:
                        main.setTemp(reader.nextDouble());
                        break;
                    case Main.tempMax_JSON:
                        main.setTempMax(reader.nextDouble());
                        break;
                    case Main.tempMin_JSON:
                        main.setTempMin(reader.nextDouble());
                        break;
                    default:
                        if (reader.hasNext())
                        reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            Log.e(TAG, "Main error", e);
        }
        return main;
    }


    /**
     * Parse a Json stream and return a Wind Object.
     */
    public Wind parseWind(JsonReader reader) {
        Wind wind = new Wind();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case Wind.deg_JSON :
                        wind.setDeg(reader.nextDouble());
                        break;
                    case Wind.speed_JSON :
                        wind.setSpeed(reader.nextDouble());
                        break;
                    default:
                        if (reader.hasNext())
                        reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            Log.e(TAG, "Wind error", e);
        }
        return wind;
}

    /**
     * Parse a Json stream and return a Sys Object.
     */
    public Sys parseSys(JsonReader reader) {
        Sys sys = new Sys();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case Sys.message_JSON :
                        sys.setMessage(reader.nextDouble());
                        break;
                    case Sys.country_JSON:
                        sys.setCountry(reader.nextString());
                        break;
                    case Sys.sunrise_JSON:
                        sys.setSunrise(reader.nextLong());
                        break;
                    case Sys.sunset_JSON:
                        sys.setSunset(reader.nextLong());
                        break;
                    default:
                        if (reader.hasNext())
                        reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            Log.e(TAG, "Sys error", e);
        }
        return sys;
    }

    private Coord parseCoord(JsonReader reader) {
        Coord coord = new Coord();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case Coord.lon_JSON :
                        coord.setLon(reader.nextDouble());
                        break;
                    case Coord.lat_JSON :
                        coord.setLat(reader.nextDouble());
                    default:
                        if (reader.hasNext())
                            reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            Log.e(TAG, "Coord error", e);
        }
        return coord;
    }

}
