package com.example.licentav1.advice;

import lombok.Data;

import java.util.Date;


@Data
public class ErrorObject {
    private Integer statusCode;
    private String message;
    private Date timestamp;
}
