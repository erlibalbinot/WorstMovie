package com.jacob.WorstMovie.controller;

import com.jacob.WorstMovie.dto.MovieListDTO;
import com.jacob.WorstMovie.dto.WinnersList;
import com.jacob.WorstMovie.service.CsvService;
import com.jacob.WorstMovie.service.MoviesService;
import com.opencsv.exceptions.CsvException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MoviesController {
    private final CsvService csvService;
    private final MoviesService moviesService;

    public MoviesController(CsvService csvService, MoviesService moviesService) {
        this.csvService = csvService;
        this.moviesService = moviesService;
    }

    @PostMapping()
    public ResponseEntity<MovieListDTO> addMovie(@RequestBody MovieListDTO movie) {
        if (movie == null || !movie.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            movie = moviesService.addMovie(movie);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }

    @PostMapping("/csv")
    public ResponseEntity<Void> addMovies(@RequestParam(required = false) MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo é obrigatório e deve ser enviado como multipart/form-data");
        }
        try {
            csvService.addMovies(file);
        } catch (IOException | CsvException e) {
            return ResponseEntity.unprocessableEntity().build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sua lista contém filmes já cadastrados na base com o mesmo título, estúdio e produtor.\nPara alterar o ano ou status de vencedor de um filme, envie uma Requisição PUT com o mesmo .csv");
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/csv")
    public ResponseEntity<Void> updateMovies(@RequestParam(required = false) MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo é obrigatório e deve ser enviado como multipart/form-data");
        }
        try {
            csvService.addUpdateMovies(file);
        } catch (IOException | CsvException e) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("")
    public ResponseEntity<Void> updateMovies(@RequestBody MovieListDTO movie) {
        if (movie == null || !movie.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        try {
            movie = moviesService.addUpdateMovie(movie);
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }

        if (movie.getId() != null)
            return ResponseEntity.status(HttpStatus.OK).build();
        else
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{title}/{studios}/{producers}")
    public ResponseEntity<Void> deleteMovie(@PathVariable("title") String title, @PathVariable("studios") String studios, @PathVariable("producers") String producers) {
        try {
            if (!moviesService.deleteMovie(title, studios, producers)) {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable("id") Long id) {
        try {
            if (!moviesService.deleteMovie(id)) {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/csv")
    public ResponseEntity<Void> deleteMovies(@RequestParam(required = false) MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo é obrigatório e deve ser enviado como multipart/form-data");
        }
        try {
            csvService.deleteMovies(file);
        } catch (IOException | CsvException e) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/winners")
    public ResponseEntity<WinnersList> getWinners() {
        try {
            return ResponseEntity.ok(moviesService.getWinners());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/listmovies")
    public ResponseEntity<List<MovieListDTO>> getMoviesList() {
        try {
            return ResponseEntity.ok(moviesService.getMoviesList());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
