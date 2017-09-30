package com.joshkryo.schedule.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by king on 30/09/2017.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/tasks")
public class TaskController {
    @RequestMapping("test")
    public String isRunning(){
        return "is running";
    }
}
