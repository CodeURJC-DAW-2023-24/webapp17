package es.codeurjc.webapp17.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.codeurjc.webapp17.entity.Usr;
import jakarta.servlet.http.HttpSession;

@Controller
public class ContactController {

    /**
     * Handles GET requests to the contact page.
     *
     * This method checks if a user is logged in and whether they have admin
     * privileges.
     * It sets a flag in the model indicating admin status to adapt the view
     * accordingly.
     *
     * @param model   the model to pass attributes to the view
     * @param session the current HTTP session, used to retrieve the logged-in user
     * @return the name of the "contacto" view
     */
    @GetMapping("/contact")
    public String contact(Model model, HttpSession session) {
        // Get user role from session (if using new session management)
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userRole != null && userRole == Usr.Role.ADMIN) {
            model.addAttribute("ADMIN", true); // User is admin
        } else {
            model.addAttribute("ADMIN", false); // No user in session or not admin
        }

        return "contacto"; // Show contact page regardless of user role
    }
}
