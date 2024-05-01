package es.codeurjc.webapp17;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String index() {
        return "index"; // Devuelve la plantilla index.html
    }
}