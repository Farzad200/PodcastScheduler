package app;

import model.*;

import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) throws Exception {

        EpisodeRepository repo = new EpisodeRepository();

        Episode ep1 = repo.createEpisode("Regular", "My First Episode", 20);
        Episode ep2 = repo.createEpisode("Bonus", "Behind the Scenes", 10);

        repo.scheduleEpisode(ep1, LocalDateTime.of(2025, 5, 1, 10, 0));
        repo.scheduleEpisode(ep2, LocalDateTime.of(2025, 5, 1, 11, 0));

        System.out.println("Episode 1 status: " + ep1.getStatus());
        System.out.println("Episode 2 status: " + ep2.getStatus());

        // ---- SAVE ----
        repo.saveToFile("episodes.txt");

        // ---- LOAD ----
        EpisodeRepository repo2 = new EpisodeRepository();
        repo2.loadFromFile("episodes.txt");

        System.out.println("Loaded episodes: " + repo2.getEpisodes().size());
    }
}
