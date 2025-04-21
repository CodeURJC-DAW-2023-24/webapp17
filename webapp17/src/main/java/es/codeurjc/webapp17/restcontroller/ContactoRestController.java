package es.codeurjc.webapp17.restcontroller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

import es.codeurjc.webapp17.entity.Usr;

@RestController
@RequestMapping("/api")
public class ContactoRestController {

    @GetMapping("/contacto")
    public ResponseEntity<Map<String, Object>> getContactoInfo(
            @RequestHeader(value = "User-Role", required = false) String userRole,
            @RequestHeader(value = "User-Id", required = false) Long userId
    ) {
        Map<String, Object> response = new HashMap<>();

        if (userRole != null && userId != null) {
            boolean isAdmin = Usr.Role.ADMIN.name().equals(userRole);
            response.put("isAdmin", isAdmin);
            
            if (isAdmin) {
                // Si es admin, podríamos incluir información adicional si fuera necesario
                response.put("message", "Admin access granted");
            } else {
                response.put("message", "Regular user access");
            }
        } else {
            response.put("message", "Public access");
        }

        return ResponseEntity.ok(response);
    }
}
