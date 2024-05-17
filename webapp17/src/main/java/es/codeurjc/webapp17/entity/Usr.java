package es.codeurjc.webapp17.entity;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="usr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Enum<Role> role;
    private String username;
    private String password;
    private String email;

    @JsonManagedReference
    @OneToMany(mappedBy="usr")
    private ArrayList<Post> posts;

    @JsonManagedReference
    @OneToMany(mappedBy="usr")
    private ArrayList<Comment> comments;

    public enum Role {
        USER, ADMIN
    }
    
}
