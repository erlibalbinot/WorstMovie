package com.jacob.WorstMovie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacob.WorstMovie.dto.MovieListDTO;
import com.jacob.WorstMovie.repository.MovieListRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class MoviesControllerCsvTest {
    @Autowired
    MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieListRepository repository;

    @Test
    void createMovieTest() throws Exception {
        String csv = """
                year;title;studios;producers;winner
                1980;ttt;ttt;tttt;yes
                1999;aaaa;aaaa;aaaa;yes
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "movies.csv",
                "text/csv",
                csv.getBytes()
        );

        mock.perform(MockMvcRequestBuilders.multipart("/movies/csv")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(repository.findByTitleAndStudiosAndProducers("ttt", "ttt", "tttt").isPresent());
    }

    @Test
    void createMovieConflictTest() throws Exception {

        mock.perform(MockMvcRequestBuilders.multipart("/movies/csv")
                        .file(getFileCsv()))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void createMovieBadRequestTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.multipart("/movies/csv"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createMovieInvalidYearTest() throws Exception {
        String csv = """
                year;title;studios;producers;winner
                a;Can't Stop the Musics;Associated Film Distributions;Allan Carrs;yes
                1999;Matrixss;Paramountss;Irmãss;yes
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "movies.csv",
                "text/csv",
                csv.getBytes()
        );

        mock.perform(MockMvcRequestBuilders.multipart("/movies/csv")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void updateMovieTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.multipart("/movies/csv")
                        .file(getFileCsv())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MovieListDTO dto = getMovieDto();
        Assertions.assertTrue(repository.findByTitleAndStudiosAndProducers(dto.getTitle(), dto.getStudios(), dto.getProducers()).isPresent());
    }

    @Test
    void updateMovieBadTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.multipart("/movies/csv")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteMoviesTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.multipart("/movies/csv")
                        .file(getFileCsv())
                        .with(request -> {
                            request.setMethod("DELETE");
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Assertions.assertFalse(repository.findByTitleAndStudiosAndProducers("Can't Stop the Music", "Associated Film Distribution", "Allan Carr").isPresent());
    }

    @Test
    void deleteMoviesBadRequestTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.multipart("/movies/csv")
                        .with(request -> {
                            request.setMethod("DELETE");
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        MovieListDTO dto = getMovieDto();
        Assertions.assertTrue(repository.findByTitleAndStudiosAndProducers("Can't Stop the Music", "Associated Film Distribution", "Allan Carr").isPresent());
    }

    private MockMultipartFile getFileCsv() {
        String csv = """
                year;title;studios;producers;winner
                1980;Can't Stop the Music;Associated Film Distribution;Allan Carr;yes
                1999;Matrixs;Paramounts;Irmãs;yes
                """;

        return new MockMultipartFile(
                "file",
                "movies.csv",
                "text/csv",
                csv.getBytes()
        );
    }

    private MovieListDTO getMovieDto() {
        return new MovieListDTO(null, 1999, "Matrixs", "Paramounts", "Irmãs", 1);
    }
}
