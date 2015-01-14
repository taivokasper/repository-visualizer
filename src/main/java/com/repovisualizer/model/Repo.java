package com.repovisualizer.model;

import com.repovisualizer.exception.RepoUpdateException;
import com.repovisualizer.service.GenerationService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.File;
import java.io.IOException;
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
    private Boolean isGenerating = false;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String videoResultsDirOnHdd;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private GenerationService generationService;

    public Repo(File location, String downloadRelativePathPattern) {
        this.location = location;
        this.name = location.getName();
        this.videoPath = downloadRelativePathPattern.replace("#REPO_NAME#", name);
        File hiddenDir = getRepoHiddenDir();
        if (hiddenDir != null)
            repoType = RepoType.fromDirname(hiddenDir.getName());
    }

    public Repo(File location, String videoResultsDirOnHdd, String downloadRelativePathPattern, GenerationService generationService) {
        this(location, downloadRelativePathPattern);
        this.videoResultsDirOnHdd = videoResultsDirOnHdd;
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
        if (new File(videoResultsDirOnHdd + "/" + name + ".mp4").exists()) {
            return videoPath;
        }
        return null;
    }

    public Boolean getIsGenerating() {
        return generationService.isGenerating(getName());
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