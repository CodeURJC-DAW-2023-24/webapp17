package es.codeurjc.webapp17.restcontroller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.IssueService;
import es.codeurjc.webapp17.service.UsrService;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private IssueService issuesService;

    @Autowired
    private UsrService userService;

    @GetMapping
    public ResponseEntity<?> getAdminInfo(@RequestHeader(value = "User-Role", required = false) String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<Issue> issues = issuesService.getAllIssues();
        List<Usr> users = userService.getAllUsrs();
        List<UserInfo> usersInfo = users.stream().map(usr -> new UserInfo(
            usr.getId(),
            usr.getUsername(),
            usr.getEmail(),
            usr.getPosts().size(),
            usr.getComments().size()
        )).collect(Collectors.toList());

        AdminInfo adminInfo = new AdminInfo(issues, usersInfo);
        return ResponseEntity.ok(adminInfo);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request,
                                        @RequestHeader(value = "User-Role", required = false) String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Usr newUser = new Usr(request.getUsername(), request.getEmail(), request.getPassword(), "ADMIN".equals(request.getRole()));
        userService.createUsr(newUser);
        sendCredentialsByEmail(request.getEmail(), request.getUsername(), request.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    private void sendCredentialsByEmail(String to, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Credenciales de acceso a la web de El Rincón Del Software");
        message.setText("Hola " + username + ",\n\n"
                + "Se ha creado una cuenta para ti en nuestra aplicación.\n\n"
                + "Nombre de usuario: " + username + "\n"
                + "Contraseña: " + password + "\n\n"
                + "¡Gracias por unirte a nuestra comunidad!");
        emailSender.send(message);
    }

    // Clases internas para manejar la respuesta y las solicitudes
    private static class AdminInfo {
        private List<Issue> issues;
        private List<UserInfo> users;

        public AdminInfo(List<Issue> issues, List<UserInfo> users) {
            this.issues = issues;
            this.users = users;
        }

        // Getters
        public List<Issue> getIssues() { return issues; }
        public List<UserInfo> getUsers() { return users; }
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

        // Getters
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public int getPostsCount() { return postsCount; }
        public int getCommentsCount() { return commentsCount; }
    }

    private static class CreateUserRequest {
        private String username;
        private String email;
        private String password;
        private String role;

        // Getters
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
    }
}