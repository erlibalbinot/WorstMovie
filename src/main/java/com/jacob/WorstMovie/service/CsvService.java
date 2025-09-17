package com.jacob.WorstMovie.service;

import com.jacob.WorstMovie.dto.MovieListDTO;
import com.jacob.WorstMovie.model.UtilMapper;
import com.jacob.WorstMovie.repository.MovieListRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvService {

    private static Logger log = LoggerFactory.getLogger(CsvService.class);
    @Autowired
    private MovieListRepository repository;

    public void addMovies(MultipartFile csv) throws IOException, CsvException, DataIntegrityViolationException {
        List<MovieListDTO> moviesDTO = getMoviesList(csv);

        repository.saveAll(UtilMapper.toEntityList(moviesDTO));
    }

    public void addUpdateMovies(MultipartFile csv) throws IOException, CsvException {
        List<MovieListDTO> moviesDTO = getMoviesList(csv);

        moviesDTO.stream().forEach(movie -> repository.findByTitleAndStudiosAndProducers(movie.getTitle(), movie.getStudios(), movie.getProducers())
                .map(ifPresent -> {
                    ifPresent.setYearmovie(movie.getYear());
                    ifPresent.setWinner(movie.getWinner());
                    return repository.save(ifPresent);
                }).orElseGet(() -> repository.save(UtilMapper.toEntity(movie))));
    }

    public void deleteMovies(MultipartFile csv) throws IOException, CsvException {
        Reader reader = new InputStreamReader(csv.getInputStream());
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .build();
        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(parser)
                .build();

        csvReader.skip(1);

        List<MovieListDTO> moviesDTO = csvReader.readAll()
                .stream().map(row -> {
                    if (row.length > 0) {
                        return UtilMapper.createDto(0, row[1], row[2], row[3], null);
                    }
                    return null;
                })
                .filter(movie -> movie != null)
                .collect(Collectors.toList());

        moviesDTO.stream().forEach(movie -> repository.findByTitleAndStudiosAndProducers(movie.getTitle(), movie.getStudios(), movie.getProducers())
                .map(ifPresent -> {
                            repository.delete(ifPresent);
                            return null;
                        }
                ));
        //repository.deleteAll(UtilMapper.toEntityList(moviesDTO));
    }

    private List<MovieListDTO> getMoviesList(MultipartFile csv) throws IOException, CsvException {
        Reader reader = new InputStreamReader(csv.getInputStream());
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .build();
        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(parser)
                .build();

        csvReader.skip(1);

        return csvReader.readAll()
                .stream().map(row -> {
                    if (row.length > 0) {
                        try {
                            if (Integer.parseInt(row[0]) > 9999 && Integer.parseInt(row[0]) < 0) {
                                log.error("Ano da linha : {};{};{};{};{} deve estar entre 0 e 9999.", new Object[] {row[0], row[1], row[2], row[3], row[4]});
                                return null;
                            }
                            return UtilMapper.createDto(Integer.parseInt(row[0]), row[1], row[2], row[3], row[4]);
                        } catch (NumberFormatException e) {
                            log.error("Erro ao converter ano da linha: {};{};{};{};{}", new Object[] {row[0], row[1], row[2], row[3], row[4]});
                            return null;
                        }
                    }
                    return null;
                })
                .filter(movie -> movie != null)
                .collect(Collectors.toList());
    }
}
