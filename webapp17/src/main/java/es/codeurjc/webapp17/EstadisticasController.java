package es.codeurjc.webapp17;

import java.util.Arrays;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstadisticasController {

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model) {
        // Aquí iría la lógica para cargar las estadísticas desde la base de datos o cualquier otra fuente

        
        // Datos de ejemplo: temáticas y número de discusiones
        String[] topics = {"Java", "OpenSource", "JavaScript", "C++", "SSOO"};
        int[] discussions = {30, 25, 20, 15, 10};
        model.addAttribute("topics", topics);
        model.addAttribute("discussions", discussions);
        
        // Datos de ejemplo: títulos de los posts y número de comentarios
        String[] titles = {"Java vs C", "Python vs JavaScript", "Frameworks más populares", "El futuro de la inteligencia artificial", "Seguridad en el desarrollo de software"};
        int[] comments = {50, 45, 40, 35, 30};
        model.addAttribute("titles", titles);
        model.addAttribute("comments", comments);

        System.out.println("Temáticas: " + Arrays.toString(topics));
        System.out.println("Número de discusiones: " + Arrays.toString(discussions));
        System.out.println("Títulos de posts: " + Arrays.toString(titles));
        System.out.println("Número de comentarios: " + Arrays.toString(comments));


        
        return "estadisticas"; // Este es el nombre del archivo HTML sin la extensión
    }
}
