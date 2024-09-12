package es.codeurjc.webapp17;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.webapp17.entity.Usr;
import es.codeurjc.webapp17.repository.UsrRepository;
import es.codeurjc.webapp17.service.UsrService;
import jakarta.annotation.PostConstruct;

@Component
public class Initializer {

    @Autowired
    private UsrRepository usrRepository;
    
    @Autowired
    private UsrService userService;

    // Contraseña cifrada usando el cifrado César con un desplazamiento de 3
    private static final String ENCRYPTED_PASSWORD = "vxshudgplq"; 
    private static final int SHIFT = 3; // Número de posiciones para el cifrado César

    @PostConstruct
    public void init() {
        Usr existingUser = usrRepository.findByUsername("SUPERADMIN");

        if (existingUser == null) {
            // Descifrar la contraseña
            String password = decryptPassword(ENCRYPTED_PASSWORD, SHIFT);
            //System.out.println("___________________________________________________________________________________________Contraseña descifrada: " + password);

            Usr newUser = new Usr("SUPERADMIN", "superadmin@superadmin", password, true);

            // Guardar el nuevo usuario en la base de datos
            userService.createUsr(newUser);
        }
    }

    // Método para descifrar la contraseña utilizando el cifrado César inverso
    private String decryptPassword(String encryptedPassword, int shift) {
        StringBuilder decryptedPassword = new StringBuilder();

        for (char c : encryptedPassword.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                // Desplazar cada carácter hacia atrás en el alfabeto
                char decryptedChar = (char) ((c - base - shift + 26) % 26 + base);
                decryptedPassword.append(decryptedChar);
            } else {
                // Si no es letra, no lo descifres
                decryptedPassword.append(c);
            }
        }

        return decryptedPassword.toString();
    }
}
