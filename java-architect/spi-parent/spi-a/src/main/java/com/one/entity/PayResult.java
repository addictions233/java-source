package com.one.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PayResult {

    private boolean success;

    private String message;

    public PayResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
