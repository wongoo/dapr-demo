package com.github.wongoo.dapr.pay.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * self definition model
 *
 * @author wongoo
 */
@Getter
@Setter
@Builder
public class PayResult implements Serializable {
    private long orderId;
    private int code;
    private String message;
}
