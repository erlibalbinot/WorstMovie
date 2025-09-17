package com.jacob.WorstMovie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WinnersMaxMin {

    private String producer;
    private int intervalMin;
    private int previousMinWin;
    private int followingMinWin;
    private int intervalMax;
    private int previousMaxWin;
    private int followingMaxWin;
}
