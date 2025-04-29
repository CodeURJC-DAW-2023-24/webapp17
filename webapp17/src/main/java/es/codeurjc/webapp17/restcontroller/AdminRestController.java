package es.codeurjc.webapp17.restcontroller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.IssueService;
import es.codeurjc.webapp17.service.UsrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Controller", description = "Admin operations for managing users and issues")
public class AdminRestController {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private IssueService issueService;

    @Autowired
    private UsrService userService;

    /**
     * Retrieves the admin dashboard data if the user has the ADMIN role.
     *
     * @param session the current HTTP session
     * @return a list of issues and user information, or 403 if access is denied
     */
    @Operation(summary = "Get admin panel information")
    @ApiResponse(responseCode = "200", description = "Information successfully loaded")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping
    public ResponseEntity<?> getAdminDashboard(HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");
        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body("Access denied");
        }

        List<Issue> issues = issueService.getAllIssues();
        List<Usr> users = userService.getAllUsrs();
        List<UserInfo> userInfos = users.stream().map(u -> new UserInfo(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getPosts().size(),
                u.getComments().size())).collect(Collectors.toList());

        return ResponseEntity.ok(new AdminResponse(issues, userInfos));
    }

    /**
     * Creates a new user and sends login credentials via email.
     *
     * @param request contains username, email, password and role
     * @return success message
     */
    @Operation(summary = "Create a new user from the admin panel")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "403", description = "No right permissions")
    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request, HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");
        if (user == null || user.getRole() != Usr.Role.ADMIN) {
            return ResponseEntity.status(403).body("No right permissions");
        }
        Usr newUser = new Usr(request.getUsername(), request.getEmail(), request.getPassword(),
                request.getRole().equals("ADMIN"));
        userService.createUsr(newUser);
        sendCredentialsByEmail(request.getEmail(), request.getUsername(), request.getPassword());
        return ResponseEntity.status(201).body("User successfully created.");
    }

    /**
     * Sends an email containing login credentials to the new user.
     *
     * @param to       recipient email
     * @param username user's name
     * @param password user's password
     */
    private void sendCredentialsByEmail(String to, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your account credentials");
        message.setText("Hello " + username + ",\n\n" +
                "Your account has been created.\n\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n\n" +
                "Please log in and change your password.");
        emailSender.send(message);
    }

    /**
     * DTO containing user information for admin view.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private int postsCount;
        private int commentsCount;
    }

    /**
     * DTO for creating a user via admin panel.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        private String username;
        private String email;
        private String password;
        private String role;

    }

    /**
     * Wrapper class to return both issues and user info in the admin response.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminResponse {
        private List<Issue> issues;
        private List<UserInfo> users;

    }
}
