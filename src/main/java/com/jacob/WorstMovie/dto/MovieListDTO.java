package com.jacob.WorstMovie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieListDTO {
    private Long id;
    private Integer year;
    private String title;
    private String studios;
    private String producers;
    private Integer winner;

    public Boolean isValid() {
        return this.year != null && this.year >= 0 && this.year <= 9999 &&
                this.title != null && !this.title.isBlank() &&
                this.studios != null && !this.studios.isBlank() &&
                this.producers != null && !this.producers.isBlank() &&
                this.winner != null;
    }
}
