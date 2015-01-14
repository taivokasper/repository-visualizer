package com.repovisualizer.controller;

import com.repovisualizer.exception.RepoNotFound;
import com.repovisualizer.model.Repo;
import com.repovisualizer.service.RepoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "rest/repo")
public class RepoController {
    private static final Logger LOG = LoggerFactory.getLogger(RepoController.class);

    @Autowired
    private RepoService repoService;

    @RequestMapping(method = RequestMethod.GET, value = "/{repoName}")
    @ResponseStatus(HttpStatus.OK)
    public Repo getRepo(@PathVariable String repoName) {
        return repoService.getByRepoName(repoName).orElseThrow(RepoNotFound::new);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    @ResponseStatus(HttpStatus.OK)
    public List<Repo> getReposNames() {
        return repoService.getListOfRepositories();
    }
}