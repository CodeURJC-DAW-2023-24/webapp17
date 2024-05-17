package es.codeurjc.webapp17.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/contacto")
public class ContactoController {

    @GetMapping
    public String mostrarFormularioContacto() {
        return "contacto";
    }

    @PostMapping
    public String procesarFormularioContacto(@RequestParam("name") String name, 
                                             @RequestParam("email") String email, 
                                             @RequestParam("message") String message,
                                             Model model) {
        // Procesar datos del formulario

        // Agregar un atributo al modelo para indicar que el formulario se ha enviado correctamente
        model.addAttribute("enviado", true);

        // Devolver la misma página
        return "contacto";
    }
}
