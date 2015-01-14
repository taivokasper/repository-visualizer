package com.repovisualizer.model;

import com.repovisualizer.exception.RepoUpdateException;
import com.repovisualizer.service.GenerationService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

@Data
@Configurable
public class Repo {
    private static final Logger LOG = LoggerFactory.getLogger(Repo.class);

    private String name;
    private File location;
    private RepoType repoType;
    private String videoPath;

    private String downloadPathPattern;
    private GenerationService generationService;
    private Boolean isGenerating = false;

    public Repo(File location, String downloadRelativePathPattern) {
        this.location = location;
        this.name = location.getName();
        this.videoPath = downloadRelativePathPattern.replace("#REPO_NAME#", name);
        File hiddenDir = getRepoHiddenDir();
        if (hiddenDir != null)
            repoType = RepoType.fromDirname(hiddenDir.getName());
    }

    public Repo(File location, String downloadPathPattern, String downloadRelativePathPattern, GenerationService generationService) {
        this(location, downloadRelativePathPattern);
        this.downloadPathPattern = downloadPathPattern;
        this.generationService = generationService;
    }

    public boolean isValidRepoRoot() {
        return getRepoHiddenDir() != null;
    }

    private File getRepoHiddenDir() {
        Predicate<File> containsRepoHiddenDir = i -> i.isDirectory() && i .canRead() &&
                (".hg".equals(i.getName()) || ".git".equals(i.getName()));
        if (location.isDirectory() && location.canRead()) {
            File[] files = location.listFiles(containsRepoHiddenDir::test);
            return Arrays.stream(files).findFirst().orElse(null);
        }
        return null;
    }

    public String getVideoPath() {
        if (isExistingUrl(downloadPathPattern.replace("#REPO_NAME#", name))) {
            return videoPath;
        }
        return null;
    }

    public Boolean getIsGenerating() {
        return generationService.isGenerating(getName());
    }

    private boolean isExistingUrl(String url) {
        HttpURLConnection connection;
        try {
            URL videoUrl = new URL(url);
            connection = (HttpURLConnection) videoUrl.openConnection();
            //Set request to header to reduce load.
            connection.setRequestMethod("HEAD");
            int code = connection.getResponseCode();
            LOG.info("Url " + url + " returned status code " + code);
            return HttpStatus.NOT_FOUND.value() != code;
        } catch (IOException e) {
            return false;
        }
    }

    public void update() {
        switch (repoType) {
            case HG:
                update(new Command("hg", "pull", "--insecure", "-u"));
                break;
            case GIT:
                update(new Command("git", "pull"));
                break;
        }
    }

    private void update(Command command) {
        LOG.info("Executing command in directory " + location.getAbsolutePath());
        try {
            int exitValue = new ProcessExecutor()
                    .directory(location)
                    .command(command.getCommandParts())
                    .redirectOutput(Slf4jStream.of(Repo.class).asInfo())
                    .execute().getExitValue();
            if (exitValue != 0) {
                throw new RepoUpdateException("Invalid exit code " + exitValue + " when updating!");
            }
        } catch (IOException | InterruptedException | TimeoutException e) {
            throw new RepoUpdateException();
        }
    }
}