package com.jacob.WorstMovie.repository;

import com.jacob.WorstMovie.model.MoviesList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieListRepository extends JpaRepository<MoviesList, Long> {

    List<MoviesList> findByWinner(int winner);

    Optional<MoviesList> findByTitleAndStudiosAndProducers(String title, String Studios, String producers);

}
