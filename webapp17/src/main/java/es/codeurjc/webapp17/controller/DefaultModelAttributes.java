package es.codeurjc.webapp17.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class DefaultModelAttributes {

    @ModelAttribute("userName")
    public String userName() {
        return "Invitado";
    }   

}