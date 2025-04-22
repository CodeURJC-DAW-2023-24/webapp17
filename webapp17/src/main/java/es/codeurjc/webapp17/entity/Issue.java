package es.codeurjc.webapp17.entity;

import java.time.LocalDateTime;


import org.springframework.format.annotation.DateTimeFormat;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="issue") 
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    private String name;
    private String email;

    @Column(columnDefinition = "TEXT", length = 3000)
    private String content;

    
    @DateTimeFormat(pattern = "dd 'de' MMMM 'de' yyyy HH:mm")
private LocalDateTime date;

    
}
