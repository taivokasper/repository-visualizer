package com.repovisualizer.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GenerationService {

    private Map<String, Boolean> currentlyGenerating = new HashMap<>();

    public void addRepo(String repoName) {
        if (!isGenerating(repoName)) {
            currentlyGenerating.put(repoName, true);
        }
    }

    public void removeRepo(String repoName) {
        currentlyGenerating.remove(repoName);
    }

    public Boolean isGenerating(String repoName) {
        return currentlyGenerating.getOrDefault(repoName, false);
    }

}