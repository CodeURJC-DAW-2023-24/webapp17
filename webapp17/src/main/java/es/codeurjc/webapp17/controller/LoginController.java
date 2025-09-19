package es.codeurjc.webapp17.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.webapp17.dto.UsrBasicDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private UsrService userService;

    /**
     * Displays the login form. If the user is already logged in, redirects them
     * based on their role.
     *
     * @param model   the model to pass data to the view
     * @param session the HTTP session to check the current user's session data
     * @return the name of the view to display (login form)
     */
    @GetMapping("/log_in")
    public String showLoginForm(Model model, HttpSession session) {
        // Check if user is already logged in using new session management
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId != null && userRole != null) {
            if (userRole == Usr.Role.ADMIN) {
                model.addAttribute("ADMIN", true);
            }
        } else {
            model.addAttribute("ADMIN", false);
        }
        
        model.addAttribute("loginError", "");
        model.addAttribute("logged", userId != null);
        return "log_in"; // Return the login form view
    }

    /**
     * Processes the login form. If the credentials are correct, the user is logged
     * in and redirected to the home page.
     *
     * @param email    the user's email address
     * @param password the user's password
     * @param model    the model to pass data to the view (e.g., error messages)
     * @param session  the HTTP session to store the user data upon successful login
     * @return a redirect to the home page if the login is successful, or back to
     *         the login page if it fails
     */
    @PostMapping("/logg_in")
    public String processLogin(@RequestParam("email") String email, @RequestParam("password") String password,
            Model model, HttpSession session) {
        
        if (userService.authenticate(email, password)) {
            // Get user information using DTO
            UsrBasicDto userBasic = userService.getUsrByEmail(email);
            
            if (userBasic != null) {
                // Store only necessary information in session
                session.setAttribute("userId", userBasic.id());
                session.setAttribute("userRole", userBasic.role());
                
                return "redirect:/"; // Redirect to the homepage after successful login
            } else {
                model.addAttribute("loginError", "User data not found");
                return "log_in";
            }
        } else {
            model.addAttribute("loginError", "Incorrect credentials"); // Display error message if authentication fails
            return "log_in"; // Return to the login form view
        }
    }

    /**
     * Logs out the user by clearing the session and redirects to the home page.
     *
     * @param model   the model to pass data to the view (not used in this case)
     * @param session the HTTP session to clear the user data
     * @return a redirect to the home page after logging out
     */
    @PostMapping("/log_out")
    public String processLogout(Model model, HttpSession session) {
        // Clear the user session data
        session.removeAttribute("userId");
        session.removeAttribute("userRole");
        session.invalidate(); // Invalidate the entire session for security
        
        return "redirect:/"; // Redirect to the homepage after logout
    }
}
