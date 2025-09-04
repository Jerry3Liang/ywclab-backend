package com.jerryliang.ywclab.Exception;

public class EvaluationLimitExceededException extends RuntimeException{
    public EvaluationLimitExceededException(String message) {
        super(message);
    }
}
