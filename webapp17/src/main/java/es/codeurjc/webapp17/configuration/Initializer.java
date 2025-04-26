package es.codeurjc.webapp17.configuration;

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

    // Encrypted password using Caesar cipher with a shift of 3
    private static final String ENCRYPTED_PASSWORD = "vxshudgplq";
    private static final int SHIFT = 3; // Number of positions for Caesar cipher shift

    /**
     * This method is called after the bean's properties have been set.
     * It checks if the user "SUPERADMIN" exists. If not, it decrypts the password
     * and creates the user.
     */
    @PostConstruct
    public void init() {
        Usr existingUser = usrRepository.findByUsername("SUPERADMIN");

        if (existingUser == null) {
            // Decrypt the password
            String password = decryptPassword(ENCRYPTED_PASSWORD, SHIFT);

            Usr newUser = new Usr("SUPERADMIN", "superadmin@superadmin", password, true);

            // Save the new user to the database
            userService.createUsr(newUser);
        }
    }

    /**
     * Method to decrypt the password using reverse Caesar cipher.
     * It shifts the characters backward in the alphabet by the given shift amount.
     *
     * @param encryptedPassword the encrypted password string
     * @param shift             the number of positions to shift each character
     * @return the decrypted password
     */
    private String decryptPassword(String encryptedPassword, int shift) {
        StringBuilder decryptedPassword = new StringBuilder();

        for (char c : encryptedPassword.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                // Shift each character backward in the alphabet
                char decryptedChar = (char) ((c - base - shift + 26) % 26 + base);
                decryptedPassword.append(decryptedChar);
            } else {
                // If it's not a letter, don't decrypt it
                decryptedPassword.append(c);
            }
        }

        return decryptedPassword.toString();
    }

}
