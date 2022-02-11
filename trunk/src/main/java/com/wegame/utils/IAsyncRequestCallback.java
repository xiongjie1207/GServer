package com.wegame.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

public interface IAsyncRequestCallback<T> extends SuccessCallback<ResponseEntity<T>>, FailureCallback {
}
