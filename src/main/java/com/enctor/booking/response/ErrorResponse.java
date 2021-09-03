package com.enctor.booking.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrorResponse implements GenericResponse {
    private int code;
    private String message;
}
