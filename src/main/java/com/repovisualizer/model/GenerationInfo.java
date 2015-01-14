package com.repovisualizer.model;

import lombok.Data;

import java.util.Date;

@Data
public class GenerationInfo {
    private String repoInProgress;
    private Date dateStarted;

    public GenerationInfo() {
    }

    public GenerationInfo(String repoInProgress) {
        this.repoInProgress = repoInProgress;
        dateStarted = new Date();
    }
}