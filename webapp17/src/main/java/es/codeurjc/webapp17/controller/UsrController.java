package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.service.UsrService;

@Controller
public class UsrController {

    @Autowired
    private UsrService UsrService;

    @PostMapping("/usr")
    public String createUsr(@RequestParam String name, @RequestParam String email, @RequestParam String password, @RequestParam Boolean admin, Model model) {
        
        UsrService.createUsr(name, email,password, admin);
        return "/contacto"; // Redirige a la página principal después de crear el Usr
    }

     @PostMapping("user/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        UsrService.deleteUsr(id);
        return "redirect:/admin";
    }


}
