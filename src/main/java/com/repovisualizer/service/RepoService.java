package com.repovisualizer.service;

import com.repovisualizer.exception.RepoNotFound;
import com.repovisualizer.exception.VideoGenerationException;
import com.repovisualizer.model.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.stream.Collectors.toList;

@Service
public class RepoService {
    private static final Logger LOG = LoggerFactory.getLogger(RepoService.class);

    @Value(value = "${video.generation.timeout.in.seconds}")
    private Long videoGenerationTimeout;
    @Value(value = "${video.generator.script.path}")
    private String videoGeneratorScript;
    @Value(value = "${video.results.dir}")
    private String resultsDir;
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

    public Repo generate(String repoName) {
        if (generationService.isGenerating(repoName)) {
            throw new VideoGenerationException("Video generation is already in progress!");
        }
        generationService.addRepo(repoName);
        try {
            Repo repo = getByRepoName(repoName).orElseThrow(RepoNotFound::new);
            repo.update();
            generateVideo(repo);
            return repo;
        } finally {
            generationService.removeRepo(repoName);
        }
    }

    private void generateVideo(Repo repo) {
        String repoAbsPath = repo.getLocation().getAbsolutePath();
        LOG.info("Starting to generate video for repository " + repoAbsPath);
        try {
            int exit = new ProcessExecutor()
                    .directory(repo.getLocation())
                    .command("bash", videoGeneratorScript, resultsDir, repo.getLocation().getName() + ".mp4", repo.getName())
                    .redirectOutput(Slf4jStream.of(RepoService.class).asInfo())
                    .timeout(videoGenerationTimeout, TimeUnit.SECONDS)
                    .execute().getExitValue();
            if (exit != 0) {
                throw new VideoGenerationException("Invalid exit code " + exit);
            }
        } catch (IOException | InterruptedException | TimeoutException e) {
            throw new VideoGenerationException("Exception occurred when generating video for repo " + repo.toString(), e);
        }
    }
}