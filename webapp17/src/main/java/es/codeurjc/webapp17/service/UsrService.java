package es.codeurjc.webapp17.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.webapp17.dto.UsrDto;
import es.codeurjc.webapp17.dto.CreateUsrDto;
import es.codeurjc.webapp17.dto.UsrBasicDto;
import es.codeurjc.webapp17.dto.PostDto;
import es.codeurjc.webapp17.dto.CommentDto;
import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.UsrRepository;

@Service
public class UsrService {
    
    @Autowired
    private UsrRepository userRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user with email and password.
     *
     * @param email the user's email
     * @param password the plain text password
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticate(String email, String password) {
        Usr user = userRepository.findByEmail(email);
        if (user != null) {
            System.out.println("Usuario encontrado: " + user.getEmail());
            System.out.println("Contraseña codificada: " + user.getPassword());
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            System.out.println("Las contraseñas coinciden: " + matches);
            return matches;
        }
        System.out.println("Usuario no encontrado");
        return false;
    }

    /**
     * Retrieves a user by email (without sensitive information).
     *
     * @param email the user's email
     * @return UsrBasicDto if found, null otherwise
     */
    public UsrBasicDto getUsrByEmail(String email) {
        Usr user = userRepository.findByEmail(email);
        if (user != null) {
            return convertToBasicDto(user);
        }
        System.out.println("Usuario no encontrado");
        return null;
    }

    /**
     * Retrieves complete user information by email.
     *
     * @param email the user's email
     * @return UsrDto if found, null otherwise
     */
    public UsrDto getCompleteUsrByEmail(String email) {
        Usr user = userRepository.findByEmail(email);
        if (user != null) {
            return convertToDto(user);
        }
        System.out.println("Usuario no encontrado");
        return null;
    }

    /**
     * Deletes a user and all associated posts and comments.
     *
     * @param userId the ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    @Transactional
    public boolean deleteUsr(Long userId) {
        try {
            if (!userRepository.existsById(userId)) {
                return false;
            }
            
            // Delete user's comments
            userRepository.deleteCommentsByUsrId(userId);
            
            // Delete user's posts (which will also delete associated comments)
            List<PostDto> posts = postService.getPostsByUser(userId);
            for (PostDto post : posts) {
                postService.deletePost(post.id());
            }
            
            // Delete the user
            userRepository.deleteById(userId);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Retrieves all users.
     *
     * @return a list of UsrDto objects
     */
    public List<UsrDto> getAllUsrs() {
        List<Usr> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all users with basic information only.
     *
     * @return a list of UsrBasicDto objects
     */
    public List<UsrBasicDto> getAllUsrsBasic() {
        List<Usr> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new user with individual parameters.
     *
     * @param name the username
     * @param email the user's email
     * @param password the plain text password
     * @param admin whether the user should have admin privileges
     * @return the created UsrDto
     */
    public UsrDto createUsr(String name, String email, String password, Boolean admin) {
        String encodedPassword = passwordEncoder.encode(password);
        Usr usuario = new Usr(name, email, encodedPassword, admin);
        Usr savedUser = userRepository.save(usuario);
        return convertToDto(savedUser);
    }

    /**
     * Creates a new user from a CreateUsrDto.
     *
     * @param createUsrDto the DTO containing user creation data
     * @return the created UsrDto
     */
    public UsrDto createUsr(CreateUsrDto createUsrDto) {
        String encodedPassword = passwordEncoder.encode(createUsrDto.password());
        Usr newUser = new Usr(
            createUsrDto.username(), 
            createUsrDto.email(), 
            encodedPassword, 
            createUsrDto.admin()
        );
        Usr savedUser = userRepository.save(newUser);
        return convertToDto(savedUser);
    }

    /**
     * Finds a user by ID.
     *
     * @param id the user's ID
     * @return UsrDto if found, null otherwise
     */
    public UsrDto findUsrById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    /**
     * Finds basic user information by ID.
     *
     * @param id the user's ID
     * @return UsrBasicDto if found, null otherwise
     */
    public UsrBasicDto findUsrBasicById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToBasicDto)
                .orElse(null);
    }

    // Private mapping methods
    
    /**
     * Converts a Usr entity to UsrDto.
     *
     * @param usr the Usr entity to convert
     * @return the converted UsrDto
     */
    private UsrDto convertToDto(Usr usr) {
        if (usr == null) return null;
        
        // Convert posts to PostDto list (basic info to avoid circular references)
        List<PostDto> postDtos = usr.getPosts() != null ? 
            usr.getPosts().stream()
                .map(post -> new PostDto(
                    post.getId(),
                    post.getUsr() != null ? post.getUsr().getId() : null,
                    post.getUsr() != null ? post.getUsr().getUsername() : null,
                    post.getTitle(),
                    post.getImage(),
                    post.getContent(),
                    post.getDate(),
                    post.getTag(),
                    List.of() // Empty comments list to avoid deep nesting
                ))
                .collect(Collectors.toList()) : List.of();
        
        // Convert comments to CommentDto list
        List<CommentDto> commentDtos = usr.getComments() != null ? 
            usr.getComments().stream()
                .map(comment -> new CommentDto(
                    comment.getId(),
                    comment.getUsr() != null ? comment.getUsr().getId() : null,
                    comment.getUsr() != null ? comment.getUsr().getUsername() : null,
                    comment.getPost() != null ? comment.getPost().getId() : null,
                    comment.getDate(),
                    comment.getText()
                ))
                .collect(Collectors.toList()) : List.of();
        
        return new UsrDto(
            usr.getId(),
            usr.getUsername(),
            usr.getEmail(),
            usr.getRole(),
            postDtos,
            commentDtos
        );
    }

    /**
     * Converts a Usr entity to UsrBasicDto.
     *
     * @param usr the Usr entity to convert
     * @return the converted UsrBasicDto
     */
    private UsrBasicDto convertToBasicDto(Usr usr) {
        if (usr == null) return null;
        
        return new UsrBasicDto(
            usr.getId(),
            usr.getUsername(),
            usr.getRole()
        );
    }
}
