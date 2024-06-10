package es.codeurjc.webapp17.controller; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.service.UsrService;

@Controller
public class LoginController {
    @Autowired
    private UsrService userService;

    @GetMapping("/log_in")
    public String showLoginForm(Model model) {
        model.addAttribute("loginError", ""); 
        return "log_in"; 
    }

    @PostMapping("/log_in")
    public String processLogin(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
        if (userService.authenticate(email, password)) {
            return "redirect:/"; // Redirige a la página de inicio o al dashboard
        } else {
            model.addAttribute("loginError", "Credenciales incorrectas");
            return "log_in";
        }
    }
}
