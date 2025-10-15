package com.jacob.WorstMovie.service;

import com.jacob.WorstMovie.dto.MovieListDTO;
import com.jacob.WorstMovie.dto.WinnersList;
import com.jacob.WorstMovie.dto.WinnersMaxMin;
import com.jacob.WorstMovie.model.UtilMapper;
import com.jacob.WorstMovie.repository.MovieListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MoviesService {
    @Autowired
    private MovieListRepository repository;

    public MovieListDTO addMovie(MovieListDTO movie) throws DataIntegrityViolationException {
        return UtilMapper.toDto(repository.save(UtilMapper.toEntity(movie)));
    }

    public MovieListDTO addUpdateMovie(MovieListDTO movie) throws DataIntegrityViolationException {
        if (movie.getId() == null) {
            return repository.findByTitleAndStudiosAndProducers(movie.getTitle(), movie.getStudios(), movie.getProducers())
                    .map(ifPresent -> {
                        ifPresent.setYearmovie(movie.getYear());
                        ifPresent.setWinner(movie.getWinner());
                        return UtilMapper.toDto(repository.save(ifPresent));
                    }).orElseGet(() -> {
                        MovieListDTO dto = UtilMapper.toDto(repository.save(UtilMapper.toEntity(movie)));
                        dto.setId(null);
                        return dto;
                    });

        }
        return repository.findById(movie.getId())
                .map(ifPresent -> {
                    ifPresent.setYearmovie(movie.getYear());
                    ifPresent.setTitle(movie.getTitle());
                    ifPresent.setStudios(movie.getStudios());
                    ifPresent.setProducers(movie.getProducers());
                    ifPresent.setWinner(movie.getWinner());
                    return UtilMapper.toDto(repository.save(ifPresent));
                }).orElseGet(() -> {
                    MovieListDTO dto = UtilMapper.toDto(repository.save(UtilMapper.toEntity(movie)));
                    dto.setId(null);
                    return dto;
                });
    }

    public Boolean deleteMovie(String title, String studios, String producers) {
        return repository.findByTitleAndStudiosAndProducers(title, studios, producers)
                .map(ifPresent -> {
                            repository.delete(ifPresent);
                            return true;
                        }
                ).orElse(false);
    }

    public Boolean deleteMovie(Long id) {
        return repository.findById(id)
                .map(ifPresent -> {
                            repository.delete(ifPresent);
                            return true;
                        }
                ).orElse(false);
    }

    public List<MovieListDTO> getMoviesList() {
        return UtilMapper.toListDto(repository.findAll());
    }

    public WinnersList getWinners() {
        List<MovieListDTO> list = UtilMapper.toListDto(repository.findByWinner(1));

        Map<String, List<Integer>> producersYearsWin = getProducersAndYearsWin(list);

        List<WinnersMaxMin> minMaxIntervalAndYears = getMinMaxIntervalAndYears(producersYearsWin);

        int[] intervals = getMinAndMaxWinners(minMaxIntervalAndYears);

        return UtilMapper.toWinnersList(minMaxIntervalAndYears, intervals[0], intervals[1]);
    }

    private Map<String, List<Integer>> getProducersAndYearsWin(List<MovieListDTO> movies) {
        return movies.stream()
                .flatMap(movie -> Stream.of(
                                        movie.getProducers()
                                                .replaceAll("(?i)\\sand\\s", ",")
                                                .split(",")
                                )
                                .map(String::trim)
                                .filter(p -> !p.isEmpty())
                                .map(producer -> Map.entry(producer, movie.getYear()))
                )
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    private List<WinnersMaxMin> getMinMaxIntervalAndYears(Map<String, List<Integer>> producersYearsWin) {
        return producersYearsWin.entrySet().stream()
                .filter(moreWins -> moreWins.getValue().size() > 1)
                .map(e -> {
                    var sortedYears = e.getValue().stream().sorted().toList();

                    int minDiff = Integer.MAX_VALUE;
                    int maxDiff = 0;
                    int yearMinFirst = -1, yearMinFinal = -1;
                    int yearMaxFirst = -1, yearMaxFinal = -1;
                    for (int i = 1; i < sortedYears.size(); i++) {
                        int diff = sortedYears.get(i) - sortedYears.get(i - 1);
                        if (diff < minDiff) {
                            minDiff = diff;
                            yearMinFirst = sortedYears.get(i - 1);
                            yearMinFinal = sortedYears.get(i);
                        }
                        if (diff > maxDiff) {
                            maxDiff = diff;
                            yearMaxFirst = sortedYears.get(i - 1);
                            yearMaxFinal = sortedYears.get(i);
                        }
                    }

                    return new WinnersMaxMin(e.getKey(), minDiff, yearMinFirst, yearMinFinal, maxDiff, yearMaxFirst, yearMaxFinal);
                })
                .toList();
    }

    private int[] getMinAndMaxWinners(List<WinnersMaxMin> winners) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (var w : winners) {
            if (w.getIntervalMin() < min) min = w.getIntervalMin();
            if (w.getIntervalMax() > max) max = w.getIntervalMax();
        }
        return new int[]{min, max};
    }
}
