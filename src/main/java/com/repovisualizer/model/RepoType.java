package com.repovisualizer.model;

public enum RepoType {
    GIT, HG;

    public static RepoType fromDirname(String dir) {
        if (".git".equals(dir))
            return GIT;
        else if (".hg".equals(dir))
            return HG;
        return null;
    }
}