package com.repovisualizer.service;

import com.repovisualizer.model.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class RepoService {
    private static final Logger LOG = LoggerFactory.getLogger(RepoService.class);

    @Value(value = "${repos.home}")
    private String reposHome;
    private File reposHomeFile;

    @Value(value = "${external.server.protocol}")
    private String externalServerProtocol;
    @Value(value = "${external.server.address}")
    private String externalServerAddress;
    @Value(value = "${external.server.port}")
    private int externalServerPort;
    @Value(value = "${video.download.url.pattern}")
    private String videoDownloadUrlPattern;
    @Autowired
    private GenerationService generationService;

    private File getReposHome() {
        if (reposHomeFile == null) {
            File file = new File(reposHome);
            if (!file.exists() || !file.isDirectory())
                throw new RuntimeException("Invalid repos home directory:" + reposHome);
            reposHomeFile = file;
        }
        return reposHomeFile;
    }

    public List<Repo> getListOfRepositories() {
        String videoPattern = externalServerProtocol + "://" + externalServerAddress + ":" + externalServerPort + "/" + videoDownloadUrlPattern;
        File[] files = getReposHome().listFiles(i -> i.exists() && i.isDirectory() && i.canRead() && new Repo(i, videoDownloadUrlPattern).isValidRepoRoot());
        return Arrays.stream(files).map(i -> new Repo(i, videoPattern, videoDownloadUrlPattern, generationService)).collect(toList());
    }

    public Optional<Repo> getByRepoName(String repoName) {
        return getListOfRepositories().stream()
                .filter(i -> repoName.equals(i.getName()))
                .findFirst();
    }
}