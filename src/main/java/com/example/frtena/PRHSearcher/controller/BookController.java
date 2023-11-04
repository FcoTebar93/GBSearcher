package com.example.frtena.PRHSearcher.controller;

import com.example.frtena.PRHSearcher.entity.Book;
import com.example.frtena.PRHSearcher.service.BookItem;
import com.example.frtena.PRHSearcher.service.BookResponse;
import com.example.frtena.PRHSearcher.service.ImageLinks;
import com.example.frtena.PRHSearcher.service.VolumeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class BookController {
    private static final String API_BASE_URL = "https://www.googleapis.com/books/v1/volumes/";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${google.books.api.key}")
    private String apiKey; // Configura tu clave de API en el application.properties

    @GetMapping("/libro/{titulo}")
    public String verLibro(@PathVariable String titulo, Model model) {
        String apiUrl = API_BASE_URL + "?q=" + titulo;

        ResponseEntity<BookResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(createHeadersWithApiKey()),
                BookResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            BookResponse bookResponse = response.getBody();
            if (bookResponse != null && bookResponse.getItems() != null && !bookResponse.getItems().isEmpty()) {
                // Suponiendo que solo se mostrará un libro (el primero de la lista)
                BookItem bookItem = bookResponse.getItems().get(0);
                VolumeInfo volumeInfo = bookItem.getVolumeInfo();

                // Ahora puedes acceder a los detalles del libro a través de VolumeInfo
                String bookTitle = volumeInfo.getTitle();
                List<String> authors = volumeInfo.getAuthors();
                String description = volumeInfo.getDescription();

                String thumbnail = volumeInfo.getImageLinks() != null ? volumeInfo.getImageLinks().getThumbnail() : "";

                Book libro = new Book(thumbnail, bookTitle, String.join(", ", authors), description);

                model.addAttribute("libro", libro);
            } else {
                model.addAttribute("error", "Libro no encontrado");
                return "resultado";  // Otra vista que muestre el error
            }
        } else {
            model.addAttribute("error", "Libro no encontrado");
            return "resultado";  // Otra vista que muestre el error
        }

        return "libro";
    }


    private HttpHeaders createHeadersWithApiKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Api-Key", apiKey);
        return headers;
    }
}