package org.example.socialnetwork.service;
public class ServiceException extends RuntimeException {
    public ServiceException(){}

    public ServiceException(String mesaj){
        super(mesaj);
    }
}
