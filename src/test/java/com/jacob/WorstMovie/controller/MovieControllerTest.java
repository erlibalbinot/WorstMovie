package com.jacob.WorstMovie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacob.WorstMovie.dto.MovieListDTO;
import com.jacob.WorstMovie.repository.MovieListRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MovieControllerTest {
    @Autowired
    MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieListRepository repository;

    @Test
    void createMovieTest() throws Exception {
        MovieListDTO movie = getMovieDto();
        movie.setTitle("Scooby Doo 4");

        mock.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(repository.findByTitleAndStudiosAndProducers("Scooby Doo 4", movie.getStudios(), movie.getProducers()).isPresent());
        Assertions.assertFalse(repository.findByTitleAndStudiosAndProducers("Scooby Doo 2", "samba", "show").isPresent());
    }

    @Test
    void createMovieBadTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createMovieConflictTest() throws Exception {
        MovieListDTO movie = getMovieDto();
        movie.setTitle("Scooby Doo 4");

        mock.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void getMoviesTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.get("/movies/listmovies"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Scooby Doo"));
    }

    @Test
    void getMoviesWinnersTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.get("/movies"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.min[0].producer").value("Albert S. Ruddy"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min[0].interval").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min[0].previousWin").value(1982))
                .andExpect(MockMvcResultMatchers.jsonPath("$.min[0].followingWin").value(1984))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max[0].producer").value("Jerry Weintraub"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max[0].interval").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max[0].previousWin").value(1980))
                .andExpect(MockMvcResultMatchers.jsonPath("$.max[0].followingWin").value(1989));
    }

    @Test
    void updateMovieTest() throws Exception {
        MovieListDTO movie = getMovieDto();
        movie.setYear(2000);

        mock.perform(MockMvcRequestBuilders.put("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertTrue(repository.findByTitleAndStudiosAndProducers(movie.getTitle(), movie.getStudios(), movie.getProducers()).isPresent());
        Assertions.assertEquals(repository.findByTitleAndStudiosAndProducers(movie.getTitle(), movie.getStudios(), movie.getProducers()).get().getYearmovie(), 2000);
    }

    @Test
    void updateCreateMovieTest() throws Exception {
        MovieListDTO movie = getMovieDto();
        movie.setYear(2000);
        movie.setTitle("novo filme");

        mock.perform(MockMvcRequestBuilders.put("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(repository.findByTitleAndStudiosAndProducers(movie.getTitle(), movie.getStudios(), movie.getProducers()).isPresent());
        Assertions.assertEquals(repository.findByTitleAndStudiosAndProducers(movie.getTitle(), movie.getStudios(), movie.getProducers()).get().getYearmovie(), 2000);
    }

    @Test
    void updateMovieBadRequestTest() throws Exception {
        MovieListDTO movie = getMovieDto();
        movie.setTitle(null);

        mock.perform(MockMvcRequestBuilders.put("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteMovieIdTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.delete("/movies/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Assertions.assertFalse(repository.findById(1L).isPresent());
    }

    @Test
    void deleteMovieTitleStudioProducerTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.delete("/movies/Can't Stop the Music/Associated Film Distribution/Allan Carr")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Assertions.assertFalse(repository.findByTitleAndStudiosAndProducers(
                "Can't Stop the Music", "Associated Film Distribution", "Allan Carr").isPresent());
    }

    @Test
    void deleteMovieNotFoundTitleTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.delete("/movies/Can't Stop thes Music/Associated Film Distribution/Allans Carr")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Assertions.assertTrue(repository.findByTitleAndStudiosAndProducers(
                "Can't Stop the Music", "Associated Film Distribution", "Allan Carr").isPresent());
    }

    @Test
    void deleteMovieNotFoundIdTest() throws Exception {
        mock.perform(MockMvcRequestBuilders.delete("/movies/12312312")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private MovieListDTO getMovieDto() {
        return MovieListDTO.builder()
                .year(1980)
                .title("Can't Stop the Music")
                .studios("Associated Film Distribution")
                .producers("Allan Carr")
                .winner(0)
                .build();
    }

}
