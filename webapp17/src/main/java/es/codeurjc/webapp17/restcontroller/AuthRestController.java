package es.codeurjc.webapp17.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import es.codeurjc.webapp17.dto.UsrBasicDto;
import es.codeurjc.webapp17.dto.UsrDto;
import es.codeurjc.webapp17.service.UsrService;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UsrService userService;

    /**
     * Logs in the user with email and password.
     * Stores the authenticated user information in the HttpSession.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {
        
        if (userService.authenticate(email, password)) {
            // Get basic user information (without sensitive data)
            UsrBasicDto userBasic = userService.getUsrByEmail(email);
            
            if (userBasic != null) {
                // Store user ID in session instead of full entity
                session.setAttribute("userId", userBasic.id());
                session.setAttribute("userRole", userBasic.role());
                
                return ResponseEntity.ok().body(userBasic);
            } else {
                return ResponseEntity.badRequest().body("User data not found.");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials.");
        }
    }

    /**
     * Logs out the currently authenticated user by clearing the HttpSession.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.removeAttribute("userId");
        session.removeAttribute("userRole");
        session.invalidate();
        
        return ResponseEntity.ok().body(new LogoutResponse("Logout successful."));
    }

    /**
     * Returns the current authenticated user's information.
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("Not authenticated."));
        } else {
            // Get basic user information
            UsrBasicDto userBasic = userService.findUsrBasicById(userId);
            
            if (userBasic != null) {
                return ResponseEntity.ok().body(userBasic);
            } else {
                return ResponseEntity.status(404).body(new ErrorResponse("User not found."));
            }
        }
    }

    /**
     * Returns complete user profile information (requires authentication).
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("Not authenticated."));
        } else {
            // Get complete user information
            UsrDto user = userService.findUsrById(userId);
            
            if (user != null) {
                return ResponseEntity.ok().body(user);
            } else {
                return ResponseEntity.status(404).body(new ErrorResponse("User not found."));
            }
        }
    }

    // Response DTOs for API responses
    
    /**
     * DTO for logout response.
     */
    public record LogoutResponse(String message) {}

    /**
     * DTO for error responses.
     */
    public record ErrorResponse(String error) {}
}
