package es.codeurjc.webapp17.restcontroller;

import java.io.IOException;
import java.nio.file.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageRestController {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Upload an image to the server.
     * 
     * @param image The image file to upload.
     * @return The URL of the uploaded image.
     */
    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body("No image provided.");
        }

        try {
            // Generate a unique file name
            String filename = System.currentTimeMillis() + "-" + image.getOriginalFilename();

            Path directory = Paths.get(uploadPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Path filepath = directory.resolve(filename);
            Files.write(filepath, image.getBytes());

            // URL to retrieve the image
            String imageUrl = "/images/" +filename;

            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving the image.");
        }
    }

    /**
     * Retrieve an image by its file name.
     * 
     * @param fileName The name of the image to retrieve.
     * @return The image file.
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable("fileName") String fileName) {
        Path filepath = Paths.get(uploadPath, fileName);
        if (!Files.exists(filepath)) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] image = Files.readAllBytes(filepath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
