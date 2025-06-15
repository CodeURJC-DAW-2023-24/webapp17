package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UsrController {

    @Autowired
    private UsrService UsrService;

    /**
     * Deletes a user by their ID. If the user is the superadmin, they cannot be
     * deleted.
     * 
     * @param id the ID of the user to delete
     * @return a redirect to the admin page after attempting to delete the user
     */
    @PostMapping("user/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
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
        if (user.getId() == userC.getId()) {
            // Redirect with an error message or handle the case differently
            return "redirect:/admin"; // Prevent deletion of the superadmin
        }
        // Proceed with deletion if not the superadmin
        UsrService.deleteUsr(id);
        return "redirect:/admin"; // Redirect to the admin page after deleting the user
    }

}
