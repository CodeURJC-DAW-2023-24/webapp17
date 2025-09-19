package es.codeurjc.webapp17.restcontroller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.webapp17.dto.UsrDto;
import es.codeurjc.webapp17.dto.UsrBasicDto;
import es.codeurjc.webapp17.dto.CreateUsrDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.service.UsrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "API for user management (Admin operations)")
public class UsersRestController {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UsrService userService;

    /**
     * Retrieves all users (Admin only).
     *
     * @param session HTTP session
     * @return List of user information
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users with detailed information (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<List<AdminUserInfoDto>> getAllUsers(HttpSession session) {
        
        // Check authentication and authorization
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Get all users
        List<UsrDto> users = userService.getAllUsrs();
        
        // Convert to admin-specific DTO with additional statistics
        List<AdminUserInfoDto> userInfoDtos = users.stream()
            .map(user -> new AdminUserInfoDto(
                user.id(),
                user.username(),
                user.email(),
                user.role(),
                user.posts() != null ? user.posts().size() : 0,
                user.comments() != null ? user.comments().size() : 0
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(userInfoDtos);
    }

    /**
     * Retrieves basic information of all users.
     *
     * @param session HTTP session
     * @return List of basic user information
     */
    @GetMapping("/basic")
    @Operation(summary = "Get all users basic info", description = "Retrieves basic information of all users (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<List<UsrBasicDto>> getAllUsersBasic(HttpSession session) {
        
        // Check authentication and authorization
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UsrBasicDto> users = userService.getAllUsrsBasic();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a specific user by ID (Admin only).
     *
     * @param id      User ID
     * @param session HTTP session
     * @return User information
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves specific user information by ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UsrDto> getUserById(@PathVariable Long id, HttpSession session) {
        
        // Check authentication and authorization
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UsrDto user = userService.findUsrById(id);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(user);
    }

    /**
     * Creates a new user and sends credentials via email (Admin only).
     *
     * @param request User creation request
     * @param session HTTP session
     * @return Created user information
     */
    @PostMapping
    @Operation(summary = "Create new user", description = "Creates a new user and sends login credentials via email (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<UsrDto> createUser(@RequestBody AdminCreateUserRequest request, HttpSession session) {
        
        // Check authentication and authorization
        Long userId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Validate request data
        if (request.username() == null || request.username().trim().isEmpty() ||
            request.email() == null || request.email().trim().isEmpty() ||
            request.password() == null || request.password().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Create user using CreateUsrDto
        CreateUsrDto createUserDto = new CreateUsrDto(
            request.username().trim(),
            request.email().trim(),
            request.password(),
            "ADMIN".equalsIgnoreCase(request.role())
        );

        // Create user
        UsrDto createdUser = userService.createUsr(createUserDto);

        // Send credentials by email
        try {
            sendCredentialsByEmail(request.email(), request.username(), request.password());
        } catch (Exception e) {
            // Log email sending failure but don't fail the user creation
            System.err.println("Failed to send credentials email: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Deletes a user by ID (Admin only).
     *
     * @param id      User ID to delete
     * @param session HTTP session
     * @return Success or error response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by ID (Admin only, with restrictions)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete superadmin or yourself"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ErrorResponse> deleteUser(@PathVariable Long id, HttpSession session) {
        
        // Check authentication and authorization
        Long currentUserId = (Long) session.getAttribute("userId");
        Usr.Role userRole = (Usr.Role) session.getAttribute("userRole");
        
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        if (userRole != Usr.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("No admin permissions"));
        }

        // Check if user to delete exists
        UsrDto userToDelete = userService.findUsrById(id);
        if (userToDelete == null) {
            return ResponseEntity.notFound().build();
        }

        // Prevent deletion of superadmin and self-deletion
        if ("superadmin@superadmin".equals(userToDelete.email()) || 
            currentUserId.equals(userToDelete.id())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Cannot delete superadmin or yourself"));
        }

        // Delete user
        boolean deleted = userService.deleteUsr(id);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete user"));
        }
    }

    /**
     * Sends login credentials to the newly created user via email.
     *
     * @param to       Recipient email
     * @param username Username
     * @param password Password
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

    // DTOs for admin-specific operations

    /**
     * DTO for user information in admin panel with statistics.
     */
    public record AdminUserInfoDto(
        Long id,
        String username,
        String email,
        Usr.Role role,
        int postsCount,
        int commentsCount
    ) {}

    /**
     * Request DTO for creating a user from admin panel.
     */
    public record AdminCreateUserRequest(
        String username,
        String email,
        String password,
        String role
    ) {}

    /**
     * DTO for error responses.
     */
    public record ErrorResponse(String error) {}
}
