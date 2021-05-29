package com.bjpowernode.rabbitmq.service;

public interface ReceiveService {
    void receiveMessage();
    void directReceive(String message);
    void findoutReceive(String message);
    void findout02Receive(String message);

}
