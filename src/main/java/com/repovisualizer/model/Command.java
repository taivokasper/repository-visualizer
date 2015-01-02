package com.repovisualizer.model;

import lombok.Getter;

public class Command {
    @Getter
    private final String[] commandParts;

    public Command(String... commandParts) {
        this.commandParts = commandParts;
    }
}