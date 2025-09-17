package es.codeurjc.webapp17.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.UsrService;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UsrService userService;

    @Autowired(required = false)
    private Usr currentUsr;

    /**
     * Logs in the user with email and password.
     * Stores the authenticated user in the HttpSession.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {
        if (userService.authenticate(email, password)) {
            currentUsr = userService.getUsr(email);
            session.setAttribute("user", currentUsr);
            return ResponseEntity.ok().body(new UsrDto(currentUsr)); // uses  DTO
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials.");
        }
    }

    /**
     * Logs out the currently authenticated user by clearing the HttpSession.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.setAttribute("user", null);
        return ResponseEntity.ok().body("Logout successful.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Usr user = (Usr) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(401).body("Not authenticated.");
        } else {
            return ResponseEntity.ok().body(new UsrDto(user)); // uses  DTO
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class UsrDto {
        private Long id;
        private String email;
        private String nombre;
        private Usr.Role role;

        UsrDto(Usr user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nombre = user.getUsername();
            this.role = user.getRole();
        }

    }

}
