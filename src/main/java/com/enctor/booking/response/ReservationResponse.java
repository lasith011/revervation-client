package com.enctor.booking.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ReservationResponse implements GenericResponse {

    private long id;
    private String origin;
    private String destination;
    private String type;
    private int passengerCount;
    private BigDecimal totalPrice;
    private Date date;
    private double distance;
    private int duration;
    private List<String> seats;
}

