package es.codeurjc.webapp17.controller; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UsrService userService;

    @Autowired(required = false)
    private Usr currentUsr;

    @GetMapping("/log_in")
    public String showLoginForm(Model model) {
        model.addAttribute("loginError", ""); 
        return "log_in"; 
    }

    @PostMapping("/log_in")
    public String processLogin(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        if (userService.authenticate(email, password)) {
            currentUsr = userService.getUsr(email);
            session.setAttribute("user", currentUsr);
            return "redirect:/"; // Redirige a la página de inicio o al dashboard
        } else {
            model.addAttribute("loginError", "Credenciales incorrectas");
            return "log_in";
        }
    }
}
