package com.example.frtena.PRHSearcher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LibroController {
    // Inyecta el servicio necesario para obtener los detalles del libro
    private final BookService bookService;

    public LibroController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/libro/{id}")
    public String verLibro(@PathVariable Long id, Model model) {
        // Usa el servicio para buscar el libro por ID o hacer una solicitud a la API externa
        Optional<Book> libro = bookService.buscarLibroPorId(id);

        if (libro.isPresent()) {
            model.addAttribute("libro", libro.get());
            return "libro";
        } else {
            model.addAttribute("error", "Libro no encontrado");
            return "resultado";  // Otra vista que muestre el error
        }
    }
}
