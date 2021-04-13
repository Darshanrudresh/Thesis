package com.advantest.myapplication.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author darshan.rudresh
 */
@Controller
public class UserInterface {

    @GetMapping("/")
    public String startup() {
        return "dashboard";
    }

    @GetMapping("/logs.html")
    public String upload() {
        return "logs";
    }

    @GetMapping("/dashboard.html")
    public String reload() {
        return "dashboard";
    }
}
