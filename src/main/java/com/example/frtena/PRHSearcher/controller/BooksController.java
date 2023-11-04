package com.example.frtena.PRHSearcher.controller;

import com.example.frtena.PRHSearcher.entity.Book;
import com.example.frtena.PRHSearcher.service.BookItem;
import com.example.frtena.PRHSearcher.service.BookResponse;
import com.example.frtena.PRHSearcher.service.VolumeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class BooksController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${google.books.api.key}")
    private String apiKey; // Configura tu clave de API en el application.properties

    @GetMapping("/")
    public String buscarLibros() {
        return "buscar";
    }

    @PostMapping("/buscar")
    public String buscarLibrosPorNombre(@RequestParam("title") String title, Model model) {
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + title;
        ResponseEntity<BookResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(createHeadersWithApiKey()),
                BookResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            List<Book> libros = new ArrayList<>();

            List<BookItem> bookList = response.getBody().getItems();

            if (bookList != null && !bookList.isEmpty()) {
                for (BookItem bookItem : bookList) {
                    VolumeInfo volumeInfo = bookItem.getVolumeInfo();
                    String bookTitle = volumeInfo.getTitle();
                    List<String> authors = volumeInfo.getAuthors();
                    String description = volumeInfo.getDescription();

                    // Accede a la imagen a través de ImageLinks
                    String thumbnail = volumeInfo.getImageLinks() != null ? volumeInfo.getImageLinks().getThumbnail() : "";

                    Book libro = new Book(thumbnail, bookTitle, String.join(", ", authors), description);
                    libros.add(libro);
                }

                model.addAttribute("libros", libros);
            } else {
                model.addAttribute("error", "No se encontraron libros.");
            }
        } else {
            model.addAttribute("error", "No se encontraron libros.");
        }

        return "resultado";
    }

    private HttpHeaders createHeadersWithApiKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Api-Key", apiKey);
        return headers;
    }
}