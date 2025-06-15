package es.codeurjc.webapp17.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usr {

    public Usr(String username, String email, String password, Boolean admin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = admin ? Role.ADMIN : Role.USER;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ADMIN', 'USER')")
    private Role role;

    private String username;
    private String password;
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "usr", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Post> posts;

    @OneToMany(mappedBy = "usr", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Comment> comments;

    public enum Role {
        USER, ADMIN
    }

    public Role getRole() {
        if (role == null) {
            return Role.USER;
        }
        return role;
    }
    public String getUsername() {
        return this.username;
    }
}
