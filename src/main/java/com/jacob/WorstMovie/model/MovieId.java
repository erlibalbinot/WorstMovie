package com.jacob.WorstMovie.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieId implements Serializable {
    private String title;
    private String studios;
    private String producers;
}
