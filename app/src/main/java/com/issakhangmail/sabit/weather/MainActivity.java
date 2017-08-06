package com.issakhangmail.sabit.weather;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.jar.Manifest;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private CurrentWeather mCurrentWeather;

    public static  final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.rainLabelValue) TextView mRainValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.refreshImageView) ImageView mrefreshImage;
    @BindView(R.id.locationLabel) TextView mLocationLabel;
    @BindView(R.id.textUV) TextView mUVLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final double latitude = 43.238949;
        final double longitude = 76.889709;

        mrefreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });


        getForecast(latitude, longitude);

        Log.d(TAG, "Common logs");
    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = "6c4761f9449b408710d19da6ecda1ef3";
        String openWeatherKey = "1a89a96cb40116a6bc5fe558a81f4d15";


        String darkSkyUrl = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;
        String openWeatherUrl = "http://samples.openweathermap.org/data/2.5/uvi?lat="+latitude+"&lon="+longitude+"&appid="+openWeatherKey;

        if(isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(darkSkyUrl).build();
            Request request2 = new Request.Builder().url(openWeatherUrl).build();

            Call call = client.newCall(request);
            Call call2 = client.newCall(request2);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetailts(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception Caught: ", e);
                    }

                    catch (JSONException e) {
                        Log.e(TAG, "Exception Caught: ", e);
                    }


                }

            });

            call2.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData2 = response.body().string();
                        Log.v(TAG, jsonData2);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetailts(jsonData2);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception Caught: ", e);
                    }

                    catch (JSONException e) {
                        Log.e(TAG, "Exception Caught: ", e);
                    }


                }
            });

        }

        else {

            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    private void updateDisplay() {
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("Время: " + mCurrentWeather.getFormattedTime());
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mRainValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        mLocationLabel.setText(mCurrentWeather.getTimeZone());
        mUVLabel.setText(mCurrentWeather.getUV() + "");

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private CurrentWeather getCurrentDetailts(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
       // JSONObject forecast2 = new JSONObject(jsonData2);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "FROM JSON" + "=" + timezone);


        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(forecast.getString("timezone"));
       // currentWeather.setUV(forecast2.getLong("value"));


        return currentWeather;
    }









    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
            AlertDialogFragment dialog = new AlertDialogFragment();

        dialog.show(getFragmentManager(), "error_dialog");
    }
}
