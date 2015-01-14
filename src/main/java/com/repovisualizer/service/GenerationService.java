package com.repovisualizer.service;

import com.repovisualizer.exception.RepoNotFound;
import com.repovisualizer.exception.VideoGenerationException;
import com.repovisualizer.model.GenerationInfo;
import com.repovisualizer.model.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class GenerationService {
    private static final Logger LOG = LoggerFactory.getLogger(GenerationService.class);

    @Value(value = "${video.generator.script.path}")
    private String videoGeneratorScript;
    @Value(value = "${video.results.dir}")
    private String resultsDir;
    @Value(value = "${video.generation.timeout.in.seconds}")
    private Long videoGenerationTimeout;

    @Autowired
    private RepoService repoService;

    private GenerationInfo repoInProgres;

    public void markInProgress(String repoName) {
        if (!isGenerating(repoName)) {
            repoInProgres = new GenerationInfo(repoName);
        }
    }

    public void endInProgress() {
        repoInProgres = null;
    }

    public Boolean isGenerating(String repoName) {
        return isSomethingGenerating() && repoInProgres.getRepoInProgress().equals(repoName);
    }

    public Boolean isSomethingGenerating() {
        return repoInProgres != null;
    }

    public GenerationInfo getGenerationInfo() {
        return repoInProgres;
    }


    public GenerationInfo getGenerationInfo(String repo) {
        if (isGenerating(repo))
            return repoInProgres;
        return null;
    }

    public Repo generate(String repoName) {
        if (isSomethingGenerating()) {
            throw new VideoGenerationException("Some video generation is already in progress! One at a time please!");
        }
        markInProgress(repoName);
        try {
            Repo repo = repoService.getByRepoName(repoName).orElseThrow(RepoNotFound::new);
            repo.update();
            generateVideo(repo);
            return repo;
        } finally {
            endInProgress();
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