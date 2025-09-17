package com.jacob.WorstMovie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WinnersList {
    private List<Winners> min;
    private List<Winners> max;
}
