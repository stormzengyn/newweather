package com.example.newweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("wind_dir")
    public String winddir;

    @SerializedName("wind_sc")
    public String windsc;

    @SerializedName("wind_spd")
    public String windspd;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;

    }

}
