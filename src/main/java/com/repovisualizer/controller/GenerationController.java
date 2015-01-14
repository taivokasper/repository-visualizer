package com.repovisualizer.controller;

import com.repovisualizer.model.GenerationInfo;
import com.repovisualizer.model.Repo;
import com.repovisualizer.service.GenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "rest/generation")
public class GenerationController {

    @Autowired
    private GenerationService generationService;

    @RequestMapping(method = RequestMethod.GET, value = "/generationInfo")
    @ResponseStatus(HttpStatus.OK)
    public GenerationInfo getGenerationInfo() {
        return Optional.ofNullable(generationService.getGenerationInfo()).orElse(new GenerationInfo());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/generationInfo/{repoName}")
    @ResponseStatus(HttpStatus.OK)
    public GenerationInfo getGenerationInfoForRepo(@PathVariable String repoName) {
        return Optional.ofNullable(generationService.getGenerationInfo(repoName)).orElse(new GenerationInfo());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/generate/{repoName}")
    @ResponseStatus(HttpStatus.OK)
    public Repo generateVideo(@PathVariable String repoName) {
        return generationService.generate(repoName);
    }
}