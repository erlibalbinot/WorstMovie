package com.jacob.WorstMovie.service;

import com.jacob.WorstMovie.dto.WinnersList;
import com.jacob.WorstMovie.model.MoviesList;
import com.jacob.WorstMovie.repository.MovieListRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class WorstMovieServiceTest {

    @Mock
    private MovieListRepository repository;

    @InjectMocks
    private MoviesService moviesService;

    //@Test
    void shouldReturnMovieWhenExists() {
        List<MoviesList> moviesList = List.of(new MoviesList(1L, 2018, "A", "A", "prod1", 1),
                new MoviesList(2L, 2019, "AB", "AB", "prod1, prod2", 1),
                new MoviesList(3L, 2020, "ABC", "ABC", "prod2, prod3", 1),
                new MoviesList(4L, 2021, "ABCD", "ABCD", "prod3", 1),
                new MoviesList(5L, 2022, "ABCDE", "ABCDE", "prod1, prod3", 1),
                new MoviesList(6L, 2023, "ABCDEF", "ABCDEF", "prod2, prod3", 1));

        Mockito.when(repository.findByWinner(1)).thenReturn(moviesList);

        WinnersList result = moviesService.getWinners();

        Assertions.assertEquals(2, result.getMax().size());
    }
}
