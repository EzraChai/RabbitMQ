package com.bjpowernode.springboot.service;

public interface TestService {
    void sendMessage(String message);
    void sendFanoutMessage(String message);
    void sendTopicMessage(String message);
}
