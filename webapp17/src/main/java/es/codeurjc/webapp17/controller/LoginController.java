package es.codeurjc.webapp17.controller; 

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/log_in")
    public String showLoginForm(Model model) {
        model.addAttribute("loginError", ""); 
        return "log_in"; 
    }

    @PostMapping("/log_in")
    public String processLogin(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
       
        model.addAttribute("loginError", "Credenciales incorrectas");
        return "log_in";
    }
}
