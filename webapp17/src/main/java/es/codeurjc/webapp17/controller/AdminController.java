package es.codeurjc.webapp17.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import es.codeurjc.webapp17.dto.IssueDto;
import es.codeurjc.webapp17.dto.UsrDto;
import es.codeurjc.webapp17.dto.CreateUsrDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.IssueService;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private IssueService issuesService;

    @Autowired
    private UsrService userService;

    /**
     * Handles the GET request for the admin page.
     * 
     * This method checks if the current user is an admin.
     * If so, it retrieves all issues and user information,
     * and returns the admin view. Otherwise, it redirects to
     * an error page for unauthorized access.
     *
     * @param session the HTTP session containing the user information
     * @param model   the model to pass data to the view
     * @return the admin view if the user is an admin; otherwise, a redirect to an
     *         error page
     */
    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");

        if (userId != null && userRole != null) {
            if (userRole == Usr.Role.ADMIN) {
                model.addAttribute("ADMIN", true); // User is admin
            } else {
                model.addAttribute("ADMIN", false); // User is not admin
                return "/no_admin"; // Redirect non-admin users to index
            }
        }

        // Retrieve all issues from the service and add them to the model
        List<IssueDto> issues = issuesService.getAllIssues();
        model.addAttribute("issues", issues);

        // Retrieve all users and map them to UserInfo objects
        List<UsrDto> users = userService.getAllUsrs();
        List<UserInfo> usersInfo = users.stream().map(usr -> new UserInfo(
                usr.id(),
                usr.username(),
                usr.email(),
                usr.posts() != null ? usr.posts().size() : 0,
                usr.comments() != null ? usr.comments().size() : 0)).collect(Collectors.toList());

        // Add the list of users to the model
        model.addAttribute("users", usersInfo);

        // Show admin page if user is an admin, otherwise redirect
       return "admin";
    }

    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private int postsCount;
        private int commentsCount;

        public UserInfo(Long id, String username, String email, int postsCount, int commentsCount) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.postsCount = postsCount;
            this.commentsCount = commentsCount;
        }

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getPostsCount() {
            return postsCount;
        }

        public void setPostsCount(int postsCount) {
            this.postsCount = postsCount;
        }

        public int getCommentsCount() {
            return commentsCount;
        }

        public void setCommentsCount(int commentsCount) {
            this.commentsCount = commentsCount;
        }
    }

    /**
     * Handles the POST request to create a new user from the admin panel.
     *
     * This method receives the new user's details from the request parameters,
     * creates the user, saves it to the database, and sends the credentials via
     * email.
     *
     * @param username the new user's username
     * @param email    the new user's email address
     * @param password the new user's password
     * @param role     the role assigned to the new user ("ADMIN" or "USER")
     * @return a redirect to the admin page after successful creation
     */
    @PostMapping("/adminuser")
    public String createUser(@RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role) {
        
        // Create a CreateUsrDto with the provided data
        CreateUsrDto createUserDto = new CreateUsrDto(
            username, 
            email, 
            password, 
            role.equals("ADMIN")
        );

        // Save the new user to the database using the DTO
        userService.createUsr(createUserDto);

        // Send the login credentials via email
        sendCredentialsByEmail(email, username, password);

        // Redirect back to the admin page (or another page if needed)
        return "redirect:/admin";
    }

    /**
     * Sends an email with login credentials to the newly created user.
     *
     * This method constructs a simple email message containing the user's
     * username and password, and sends it using the configured email sender.
     *
     * @param to       the recipient's email address
     * @param username the user's username
     * @param password the user's password
     */
    private void sendCredentialsByEmail(String to, String username, String password) {
        // Create a simple email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Access credentials for El Rincón Del Software website");
        message.setText("Hello " + username + ",\n\n"
                + "An account has been created for you in our application.\n\n"
                + "Username: " + username + "\n"
                + "Password: " + password + "\n\n"
                + "Thank you for joining our community!");

        // Send the email using the configured email sender
        emailSender.send(message);
    }
}
