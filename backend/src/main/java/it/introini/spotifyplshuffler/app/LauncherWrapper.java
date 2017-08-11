package it.introini.spotifyplshuffler.app;

import io.vertx.core.Launcher;

public class LauncherWrapper {
    public static void main(String[] args) {
        new Launcher().dispatch(args);
    }
}
