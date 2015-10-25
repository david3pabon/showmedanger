package io.stabilitas.locator.networking;

import com.squareup.okhttp.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.stabilitas.locator.BuildConfig;
import io.stabilitas.locator.model.Report;

/**
 * Created by David Pabon (@david3pabon)
 */
public class ReportsClient {

    private final OkHttpClient client = new OkHttpClient();

    public void getReports(final Callback<List<Report>> callback) {
        Request request = new Request.Builder()
                .url(BuildConfig.REPORT_ENDPOINT)
                .build();

        client. newCall(request).enqueue(new com.squareup.okhttp.Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                reportError(NetworkError.CONNECTION_ERROR, e.getMessage());
            }

            @Override
            public void onResponse(Response response) {

                if (!response.isSuccessful()) {
                    reportError(response.code(), response.message());
                } else {
                    try {
                        JSONArray jsonReports = new JSONArray(response.body().string());
                        List<Report> reports = new ArrayList<>();

                        for (int i=0; i < jsonReports.length(); i++) {
                            Report report = parseReport(jsonReports.getJSONObject(i));

                            if (report == null) return;
                            else reports.add(report);
                        }

                        if (callback != null) {
                            callback.onSuccess(reports);
                        }

                    } catch (JSONException e) {
                        reportError(NetworkError.PARSING_ERROR, e.getMessage());
                    } catch (IOException e) {
                        reportError(NetworkError.CONNECTION_ERROR, e.getMessage());
                    }
                }

            }

            private void reportError(int code, String message) {
                if (callback != null) {
                    NetworkError error = NetworkError.newInstance(code, message);
                    callback.onError(error);
                }
            }

            private Report parseReport(JSONObject jsonReport) {
                String type = jsonReport.optString("report_type");
                double latitude = 0.0;
                double longitude = 0.0;
                try {
                    JSONObject jsonLocation = jsonReport.getJSONObject("location");
                    if (jsonLocation != null) {
                        latitude = jsonLocation.optDouble("t");
                        longitude = jsonLocation.optDouble("n");
                    }
                } catch (JSONException e) {
                    reportError(NetworkError.PARSING_ERROR, e.getMessage());
                    return null;
                }
                return Report.newInstance(type, latitude, longitude);
            }
        });
    }
}
