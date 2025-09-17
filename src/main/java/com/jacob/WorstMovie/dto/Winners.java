package com.jacob.WorstMovie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Winners {
    private String producer;
    private int interval;
    private int previousWin;
    private int followingWin;
}
