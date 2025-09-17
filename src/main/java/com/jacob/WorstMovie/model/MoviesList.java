package com.jacob.WorstMovie.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"title", "studios", "producers"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MoviesList {
    //    year;title;studios;producers;winner
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int yearmovie;
    private String title;
    private String studios;
    private String producers;
    private int winner;
}
