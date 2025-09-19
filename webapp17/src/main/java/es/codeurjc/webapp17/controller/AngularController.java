package es.codeurjc.webapp17.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AngularController {
    
    @GetMapping("/new/")
    public String angularIndex() {
        return "forward:/new/index.html";
    }
    
    @GetMapping("/new")  // Sin slash final también
    public String angularIndexNoSlash() {
        return "forward:/new/index.html";
    }
}
