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

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.IssueService;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private JavaMailSender emailSender; // Inyecta el JavaMailSender

    @Autowired
    private IssueService issuesService;

    @Autowired
    private UsrService userService;

    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        Boolean isAdmin = false; // Valor predeterminado

        Usr user = (Usr) session.getAttribute("user");
        if (user != null) {
            if (user.getRole() == Usr.Role.ADMIN) {
                model.addAttribute("ADMIN", true);
            }
            }else {
                model.addAttribute("ADMIN", false);
                return "redirect:/error-no-admin";
            
        
            

        }

        List<Issue> issues = issuesService.getAllIssues();
        model.addAttribute("issues", issues);

        List<Usr> users = userService.getAllUsrs();
        List<UserInfo> usersInfo = users.stream().map(usr -> new UserInfo(
            usr.getId(),
            usr.getUsername(),
            usr.getEmail(),
            usr.getPosts().size(),
            usr.getComments().size()
        )).collect(Collectors.toList());

        model.addAttribute("users", usersInfo);
        

        if (isAdmin) {
            // Si el usuario es administrador, se muestra la página de administración
            return "admin";
        } else {
            // Si el usuario no es administrador, se redirige a la página de error
            return "redirect:/error-no-admin";
        }
    }

    @GetMapping("/error-no-admin")
    public String errorNoAdminPage() {
        // Página de error para usuarios que no son administradores
        return "error-no-admin";
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


    @PostMapping("/admin/create-user")
    public String createUser(@RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("password") String password,
                             @RequestParam("role") String role) {
        // Crea un nuevo usuario con los datos proporcionados
        Usr newUser = new Usr(username, email, password, role.equals("ADMIN"));

        // Guarda el nuevo usuario en la base de datos
        userService.createUsr(newUser);
        sendCredentialsByEmail(email, username, password);

        // Redirige de vuelta a la página de administración u otra página de tu elección
        return "redirect:/admin";
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


}
