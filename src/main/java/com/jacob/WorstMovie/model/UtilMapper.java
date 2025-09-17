package com.jacob.WorstMovie.model;

import com.jacob.WorstMovie.dto.MovieListDTO;
import com.jacob.WorstMovie.dto.Winners;
import com.jacob.WorstMovie.dto.WinnersList;
import com.jacob.WorstMovie.dto.WinnersMaxMin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UtilMapper {

    public static MovieListDTO createDto(int year, String title, String studios, String producers, String winner) {
        return new MovieListDTO(
                null, year, title, studios, producers, yesToOne(winner));
    }

    public static MovieListDTO toDto(MoviesList movie) {
        return new MovieListDTO(
                movie.getId(), movie.getYearmovie(), movie.getTitle(), movie.getStudios(), movie.getProducers(), movie.getWinner());
    }

    public static MoviesList toEntity(MovieListDTO dto) {
        return MoviesList.builder()
                .yearmovie(dto.getYear())
                .title(dto.getTitle())
                .studios(dto.getStudios())
                .producers(dto.getProducers())
                .winner(dto.getWinner())
                .build();
    }

    public static List<MoviesList> toEntityList(List<MovieListDTO> listMovies) {
        return listMovies.stream().map(movie -> MoviesList.builder()
                        .yearmovie(movie.getYear())
                        .title(movie.getTitle())
                        .studios(movie.getStudios())
                        .producers(movie.getProducers())
                        .winner(movie.getWinner())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public static List<MovieListDTO> toListDto(List<MoviesList> listMovies) {
        return listMovies.stream().map(movie -> new MovieListDTO(
                        movie.getId(), movie.getYearmovie(), movie.getTitle(), movie.getStudios(), movie.getProducers(), movie.getWinner()))
                .collect(Collectors.toList());
    }

    public static WinnersList toWinnersList(List<WinnersMaxMin> winners, int minInterval, int maxInterval) {
        return new WinnersList(
                winners.stream()
                        .map(win -> new Winners(win.getProducer(), win.getIntervalMin(), win.getPreviousMinWin(), win.getFollowingMinWin()))
                        .filter(win -> win.getInterval() == minInterval)
                        .collect(Collectors.toList()),
                winners.stream()
                        .map(win -> new Winners(win.getProducer(), win.getIntervalMax(), win.getPreviousMaxWin(), win.getFollowingMaxWin()))
                        .filter(win -> win.getInterval() == maxInterval)
                        .collect(Collectors.toList()));
    }

    private static int yesToOne(String value) {
        return Optional.ofNullable(value)
                .filter(v -> v.equalsIgnoreCase("yes"))
                .map(v -> 1)
                .orElse(0);
    }
}
