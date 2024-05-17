package es.codeurjc.webapp17.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String adminPage(Model model) {
        // Aquí verificas si el usuario es administrador
        boolean isAdmin = true/* lógica para verificar si el usuario es administrador */;

        if (isAdmin) {
            // Si el usuario es administrador, se muestra la página de administración
            return "admin";
        } else {
            // Si el usuario no es administrador, se redirige a la página de error
            return "redirect:/error-no-admin";
        }
    }

    @GetMapping("/error-no-admin")
    public String errorNoAdminPage() {
        // Página de error para usuarios que no son administradores
        return "error-no-admin";
    }
}
