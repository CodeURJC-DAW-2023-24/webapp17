package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.webapp17.dto.UsrDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UsrController {

    @Autowired
    private UsrService usrService;

    /**
     * Deletes a user by their ID. If the user is the superadmin, they cannot be
     * deleted.
     * 
     * @param id the ID of the user to delete
     * @return a redirect to the admin page after attempting to delete the user
     */
    @PostMapping("user/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        
        // Check if current user is logged in and is admin
        Long currentUserId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");

        if (currentUserId == null || userRole != Usr.Role.ADMIN) {
            return "redirect:/no_admin"; // Redirect if not admin
        }

        // Get the user to delete
        UsrDto userToDelete = usrService.findUsrById(id);
        
        if (userToDelete == null) {
            return "redirect:/admin"; // User not found
        }

        // Check if the user's email is 'superadmin@superadmin'
        if ("superadmin@superadmin".equals(userToDelete.email())) {
            // Prevent deletion of the superadmin
            return "redirect:/admin";
        }

        // Prevent user from deleting themselves
        if (userToDelete.id().equals(currentUserId)) {
            // Prevent self-deletion
            return "redirect:/admin";
        }

        // Proceed with deletion
        boolean deleted = usrService.deleteUsr(id);
        
        if (!deleted) {
            // Handle deletion failure if needed
            System.err.println("Failed to delete user with ID: " + id);
        }

        return "redirect:/admin"; // Redirect to the admin page after attempting deletion
    }
}
