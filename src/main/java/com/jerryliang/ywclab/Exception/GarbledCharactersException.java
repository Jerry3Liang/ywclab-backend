package com.jerryliang.ywclab.Exception;

public class GarbledCharactersException extends RuntimeException{
    public GarbledCharactersException(String message){
        super(message);
    }
}
