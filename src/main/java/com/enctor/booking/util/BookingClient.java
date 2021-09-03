package com.enctor.booking.util;

import com.enctor.booking.response.ErrorResponse;
import com.enctor.booking.response.GenericResponse;
import com.enctor.booking.response.ReservationResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingClient {

    private static final Logger LOGGER = Logger.getLogger(BookingClient.class.getName());

    /**
     * The web service URL
     */
    private final String url;

    private final Gson gson = new Gson();

    public BookingClient(String url) {
        this.url = url;
    }

    /**
     * Check availability of seats and reserve seats.
     */
    public GenericResponse checkAvailability(String method, String origin, String destination, String date,
                                             String passengerCount) throws IOException {
        GenericResponse res = null;
        HttpURLConnection connection = null;
        try {
            String apiUrl = getUrlWithParameters(url, origin, destination, date, passengerCount);
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.connect();
            res = getResponseByConnection(connection);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error Occurred : ", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return res;
    }

    /**
     * Get response by connection.
     */
    private GenericResponse getResponseByConnection(HttpURLConnection connection) throws IOException {
        int resCode = connection.getResponseCode();
        if (resCode == 200) {
            return getResponse(connection.getInputStream(), ReservationResponse.class);
        } else {
            return getResponse(connection.getErrorStream(), ErrorResponse.class);
        }
    }

    /**
     * Get response by generic type.
     */
    private <T> T getResponse(InputStream is, Class<T> clazz) {
        try {
            Reader reader = new InputStreamReader(is, Charset.defaultCharset());
            return gson.fromJson(reader, clazz);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Json Parse Error Occurred : ", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error Occurred : ", e);
            }
        }
        return null;
    }

    /**
     * Generate the URL.
     */
    private String getUrlWithParameters(String url, String origin, String destination, String date, String passengerCount) {
        String params = "?origin=" + origin + "&destination=" + destination + "&date="
                + date + "&passengerCount=" + passengerCount;
        return url + params;
    }
}
