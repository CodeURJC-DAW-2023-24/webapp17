package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UsrController {

    @Autowired
    private UsrService UsrService;

    /**
     * Creates a new user with the provided name, email, password, and admin status.
     * 
     * @param name     the name of the user
     * @param email    the email of the user
     * @param password the password for the user
     * @param admin    indicates if the user is an admin
     * @param model    the model to pass attributes to the view
     * @return a redirect to the contact page after the user is created
     */
    @PostMapping("/usr")
    public String createUsr(@RequestParam String name, @RequestParam String email, @RequestParam String password,
            @RequestParam Boolean admin, Model model) {
        UsrService.createUsr(name, email, password, admin);
        return "/contacto"; // Redirect to the contact page after creating the user
    }

    /**
     * Deletes a user by their ID. If the user is the superadmin, they cannot be
     * deleted.
     * 
     * @param id the ID of the user to delete
     * @return a redirect to the admin page after attempting to delete the user
     */
    @PostMapping("user/{id}/delete")
    public String deletePost(@PathVariable Long id,HttpSession session) {
        // Get the user to delete
        Usr user = UsrService.findUsrById(id);

        Usr userC = (Usr) session.getAttribute("user");
       
        if (userC.getRole() != Usr.Role.ADMIN) {
               return "redirect:/no_admin"; // Redirect if not admin
        }

        // Check if the user's email is 'superadmin@superadmin'
        if ("superadmin@superadmin".equals(user.getEmail())) {
            // Redirect with an error message or handle the case differently
            return "redirect:/admin"; // Prevent deletion of the superadmin
        }
        // Proceed with deletion if not the superadmin
        UsrService.deleteUsr(id);
        return "redirect:/admin"; // Redirect to the admin page after deleting the user
    }

}
