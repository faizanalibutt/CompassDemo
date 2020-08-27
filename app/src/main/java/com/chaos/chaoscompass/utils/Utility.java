package com.chaos.chaoscompass.utils;

import android.util.Log;

import androidx.annotation.DrawableRes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    private static final String TAG = "Utility";
    private static final SimpleDateFormat SunDateFormat = new SimpleDateFormat("HH:mm", Locale.UK);


    // Based on weather code data found at:
    // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
    /*@DrawableRes
    public static int getIconResourceForWeatherCondition(int weatherId) {
        Log.d(TAG, "getIconResourceForWeatherCondition() called with: weatherId = [" + weatherId + "]");

        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm_light;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain_light;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain_light;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow_light;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain_light;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow_light;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog_light;
        } else if (weatherId == 781) {
            return R.drawable.ic_storm_light;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear_light;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds_light;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy_light;
        }
        return -1;
    }*/


    /*@DrawableRes
    public static int getIconResourceForWeatherConditionDark(int weatherId) {
        Log.d(TAG, "getIconResourceForWeatherCondition() called with: weatherId = [" + weatherId + "]");
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm_dark;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain_dark;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain_dark;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow_dark;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain_dark;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow_dark;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog_dark;
        } else if (weatherId == 781) {
            return R.drawable.ic_storm_dark;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear_dark;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds_dark;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy_dark;
        }
        return -1;
    }*/


    public static String formatDms(float decimal) {
        int du = (int) decimal;
        int fen = (((int) ((decimal - du) * 3600))) / 60;
        int miao = (((int) ((decimal - du) * 3600))) % 60;
        return du + "°" + fen + "'" + miao + "\"";
    }

    public static String getDirectionText(float degree) {
        final float step = 22.5f;
        if (degree >= 0 && degree < step || degree > 360 - step) {
            return "N";
        }
        if (degree >= step && degree < step * 3) {
            return "NE";
        }
        if (degree >= step * 3 && degree < step * 5) {
            return "E";
        }
        if (degree >= step * 5 && degree < step * 7) {
            return "SE";
        }
        if (degree >= step * 7 && degree < step * 9) {
            return "S";
        }
        if (degree >= step * 9 && degree < step * 11) {
            return "SW";
        }
        if (degree >= step * 11 && degree < step * 13) {
            return "W";
        }
        if (degree >= step * 13 && degree < step * 15) {
            return "NW";
        }
        return "";
    }

    public static String formatTemperature(double temp) {
        return String.format(Locale.US, "%.0f°F", temp);
    }

    public static String formatTemperatureCelsius(double temp) {
        return String.format(Locale.US, "%.0f°C", temp);
    }

    public static float toKelvinToFahrenheit(Double tempKelvin) {
        return (float) (((9 * tempKelvin) / 5) - 459.67);
    }

    public static float toFahrenheitToCelsius(double tempFahrenheit) {
        return (float) ((5.0 / 9.0) * (tempFahrenheit - 32));
    }

    public static float toCelsiusToFahrenheit(double tempCelsius) {
        return (float) ((9.0 / 5.0) * (tempCelsius + 32));
    }

    public static float toKelvinToCelsius(Double tempKelvin) {
        return toFahrenheitToCelsius(toKelvinToFahrenheit(tempKelvin));
    }


    public static String getFormattedSunSet(long sunSet) {
        return SunDateFormat.format(new Date(sunSet * 1000));
    }

    public static String getFormattedSunRise(long sunRise) {
        return SunDateFormat.format(new Date(sunRise * 1000));
    }

    public static String getFormattedPressure(Double pressure) {
        return String.format(Locale.US, "%s hPa", pressure);
    }

    public static String getFormattedHumidity(Double humidity) {
        return String.format(Locale.US, "%s %%", humidity);
    }

    public static String getFormattedAltitude(Double altitude) {
        return String.format(Locale.US, "%d m", altitude);
    }

}
