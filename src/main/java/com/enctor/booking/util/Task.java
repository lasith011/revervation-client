package com.enctor.booking.util;

import com.enctor.booking.response.GenericResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.concurrent.Callable;
@Log4j2
public class Task implements Callable<GenericResponse> {


    private final String url;
    private final String method;
    private final String origin;
    private final String destination;
    private final String date;
    private final String passengerCount;

    public Task(String url, String method, String origin, String destination, String date,
                             String passengerCount) {
        this.url = url;
        this.method = method;
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.passengerCount = passengerCount;
    }

    /**
     * This will be executed per thread.
     */
    @Override
    public GenericResponse call() {
        GenericResponse res = null;
        try {
            BookingClient ticketReserveService = new BookingClient(url);
            res = ticketReserveService.checkAvailability(method, origin, destination,
                    date, passengerCount);
        } catch (IOException e) {
            log.log(Level.FATAL, "Error Occurred : ", e);
        }
        return res;
    }

}
