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

        return getFastWinners(list);
    }

    private WinnersList getFastWinners(List<MovieListDTO> movies) {
        Map<String, List<Integer>> mapProducers = movies.stream().collect(
                Collectors.groupingBy(
                        MovieListDTO::getProducers,
                        Collectors.mapping(MovieListDTO::getYear, Collectors.toList())
                )
        );

        List<WinnersMaxMin> winners = getIntervalsByProducers(mapProducers);

        return UtilMapper.toWinnersList(winners, getMinInterval(winners), getMaxInterval(winners));
    }

    private List<WinnersMaxMin> getIntervalsByProducers(Map<String, List<Integer>> mapProducers) {
        return mapProducers.entrySet().stream()
                .filter(moreWins -> moreWins.getValue().size() > 1)
                .map(movie -> {
                            List<Integer> years = movie.getValue().stream().sorted().collect(Collectors.toList());

                            return calculateDiffs(movie.getKey(), years);
                        }
                ).collect(Collectors.toList());
    }

    private int getMinInterval(List<WinnersMaxMin> winners) {
        return winners.stream()
                .map(WinnersMaxMin::getIntervalMin)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
    }

    private int getMaxInterval(List<WinnersMaxMin> winners) {
        return winners.stream()
                .map(WinnersMaxMin::getIntervalMax)
                .max(Integer::compareTo)
                .orElse(0);
    }

    private WinnersMaxMin calculateDiffs(String producer, List<Integer> years) {
        int minDiff = Integer.MAX_VALUE;
        int maxDiff = 0;
        int yearMinFirst = -1, yearMinFinal = -1;
        int yearMaxFirst = -1, yearMaxFinal = -1;

        for (int i = 1; i < years.size(); i++) {
            int diff = years.get(i) - years.get(i - 1);
            if (diff < minDiff) {
                minDiff = diff;
                yearMinFirst = years.get(i - 1);
                yearMinFinal = years.get(i);
            }
            if (diff > maxDiff) {
                maxDiff = diff;
                yearMaxFirst = years.get(i - 1);
                yearMaxFinal = years.get(i);
            }
        }

        return new WinnersMaxMin(producer, minDiff, yearMinFirst, yearMinFinal, maxDiff, yearMaxFirst, yearMaxFinal);
    }
}
