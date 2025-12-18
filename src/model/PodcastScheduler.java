package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PodcastScheduler {

    private final List<Episode> episodes = new ArrayList<>();

    // -------------------------
    // Create episode
    // -------------------------
    public Episode createEpisode(String type, String title, int durationMinutes) {
        String id = UUID.randomUUID().toString();
        Episode episode;

      if (type.equalsIgnoreCase("Bonus")) {
    episode = new BonusEpisode(title, durationMinutes);
}
 else {
    episode = new RegularEpisode(title, durationMinutes);
}


        episodes.add(episode);
        return episode;
    }

    // -------------------------
    // Get all episodes
    // -------------------------
    public List<Episode> getEpisodes() {
        return episodes;
    }

    // -------------------------
    // Schedule episode
    // -------------------------
    public void scheduleEpisode(Episode episode, LocalDateTime dateTime)
            throws ScheduleConflictException {

        for (Episode e : episodes) {
            if (e != episode && e.getScheduledDateTime() != null) {
                if (e.getScheduledDateTime().equals(dateTime)) {
                    throw new ScheduleConflictException(
                        "Another episode is already scheduled at this time."
                    );
                }
            }
        }

        episode.schedule(dateTime);
    }

    // -------------------------
    // Publish episode
    // -------------------------
    public void publishEpisode(Episode episode, LocalDateTime now) {
        episode.publish(now);
    }

    // -------------------------
    // Save episodes to file
    // -------------------------
    public void saveToFile(String filename) throws EpisodePersistenceException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {

            for (Episode e : episodes) {
                writer.write(
                        e.getId() + "|" +
                        e.getTypeLabel() + "|" +
                        e.getTitle() + "|" +
                        e.getDurationMinutes() + "|" +
                        e.getStatus() + "|" +
                        (e.getScheduledDateTime() == null
                                ? ""
                                : e.getScheduledDateTime())
                );
                writer.newLine();
            }

        } catch (IOException ex) {
            throw new EpisodePersistenceException("Failed to save episodes.", ex);
        }
    }

    // -------------------------
    // Load episodes from file
    // -------------------------
    public void loadFromFile(String filename) throws EpisodePersistenceException {
        episodes.clear();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                String id = parts[0];
                String type = parts[1];
                String title = parts[2];
                int duration = Integer.parseInt(parts[3]);
                EpisodeStatus status = EpisodeStatus.valueOf(parts[4]);
                LocalDateTime dateTime =
                        parts[5].isEmpty() ? null : LocalDateTime.parse(parts[5]);



                        Episode episode = type.equals("Bonus")
        ? new BonusEpisode(title, duration)
        : new RegularEpisode(title, duration);


               if (dateTime != null) {
    episode.schedule(dateTime);

}


                if (status == EpisodeStatus.PUBLISHED) {
                    episode.publish(LocalDateTime.now());
                }

                episodes.add(episode);
            }

        } catch (IOException ex) {
            throw new EpisodePersistenceException("Failed to load episodes.", ex);
        }
    }
}
