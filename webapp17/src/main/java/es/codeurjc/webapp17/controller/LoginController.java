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
        Usr user = (Usr) session.getAttribute("user");
        if (user != null) {
            if (user.getRole() == Usr.Role.ADMIN) {
                model.addAttribute("ADMIN", true);
            }
        } else {
            model.addAttribute("ADMIN", false);
        }
        model.addAttribute("loginError", "");
        model.addAttribute("logged", session.getAttribute("user") != null);
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
            currentUsr = userService.getUsr(email);
            session.setAttribute("user", currentUsr);
            return "redirect:/"; // Redirect to the homepage or dashboard after successful login
        } else {
            model.addAttribute("loginError", "Incorrect credentials"); // Display an error message if authentication
                                                                       // fails
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
    public String processLogin(Model model, HttpSession session) {
        session.setAttribute("user", null); // Clear the user session
        return "redirect:/"; // Redirect to the homepage or dashboard after logout
    }

}
