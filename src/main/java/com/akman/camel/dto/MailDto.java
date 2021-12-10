package com.akman.camel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailDto implements Serializable {
    static int counter = 1;
    private int id = counter++;

    private String message;
    private String to;
    private String receivedTime;
}