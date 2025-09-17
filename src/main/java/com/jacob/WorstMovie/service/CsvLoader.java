package com.jacob.WorstMovie.service;

import com.jacob.WorstMovie.dto.MovieListDTO;
import com.jacob.WorstMovie.model.UtilMapper;
import com.jacob.WorstMovie.repository.MovieListRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("!test")
public class CsvLoader implements ApplicationRunner {
    private static Logger log = LoggerFactory.getLogger(CsvLoader.class);
    private final MovieListRepository repository;


    public CsvLoader(MovieListRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var resource = new ClassPathResource("Movielist.csv");

        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(';')
                    .build();
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(parser)
                    .build();

            csvReader.skip(1);

            List<MovieListDTO> moviesDTO =
                    csvReader.readAll()
                    .stream().map(row -> {
                        if (row.length > 0) {
                            try {
                                if (Integer.parseInt(row[0]) > 9999 || Integer.parseInt(row[0]) < 0) {
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

            repository.saveAll(UtilMapper.toEntityList(moviesDTO));
        }
    }
}
