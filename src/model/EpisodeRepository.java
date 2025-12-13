package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.*;
import java.nio.file.*;

public class EpisodeRepository {

    private List<Episode> episodes = new ArrayList<>();

    public List<Episode> getEpisodes() {
        return episodes;
    }

    // ---------------- CREATE ----------------
    public Episode createEpisode(String type, String title, int durationMinutes) {
        String id = UUID.randomUUID().toString();
        Episode episode;

        if (type.equalsIgnoreCase("Regular")) {
            episode = new RegularEpisode(id, title, durationMinutes);
        } else {
            episode = new BonusEpisode(id, title, durationMinutes);
        }

        episodes.add(episode);
        return episode;
    }

    // ---------------- SCHEDULE ----------------
    public void scheduleEpisode(Episode episode, LocalDateTime dateTime)
            throws ScheduleConflictException {

        for (Episode ep : episodes) {
            if (ep != episode &&
                ep.getScheduledDateTime() != null &&
                ep.getScheduledDateTime().equals(dateTime)) {

                throw new ScheduleConflictException(
                        "Another episode is already scheduled at this time."
                );
            }
        }

        episode.schedule(dateTime);
    }

    // ---------------- PUBLISH ----------------
    public void publishEpisode(Episode episode, LocalDateTime now) {
        episode.publish(now);
    }

    // ---------------- SAVE ----------------
    public void saveToFile(String filename) throws EpisodePersistenceException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {

            for (Episode e : episodes) {
                writer.write(
                        e.getId() + "|" +
                        e.getTypeLabel() + "|" +
                        e.getTitle() + "|" +
                        e.getDurationMinutes() + "|" +
                        e.getStatus() + "|" +
                        (e.getScheduledDateTime() == null ? "" : e.getScheduledDateTime())
                );
                writer.newLine();
            }

        } catch (IOException ex) {
            throw new EpisodePersistenceException("Failed to save episodes", ex);
        }
    }

    // ---------------- LOAD ----------------
    public void loadFromFile(String filename) throws EpisodePersistenceException {
        episodes.clear();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|");
                if (parts.length < 6) continue; // safety check

                String id = parts[0];
                String type = parts[1];
                String title = parts[2];
                int duration = Integer.parseInt(parts[3]);
                EpisodeStatus status = EpisodeStatus.valueOf(parts[4]);

                LocalDateTime dateTime = parts[5].isEmpty()
                        ? null
                        : LocalDateTime.parse(parts[5]);

                Episode episode = type.equalsIgnoreCase("Regular")
                        ? new RegularEpisode(id, title, duration)
                        : new BonusEpisode(id, title, duration);

                if (dateTime != null) {
    try {
        episode.schedule(dateTime);
    } catch (ScheduleConflictException e) {
    }
}


                if (status == EpisodeStatus.PUBLISHED) {
                    episode.publish(LocalDateTime.now());
                }

                episodes.add(episode);
            }

        } catch (IOException | RuntimeException ex) {
            throw new EpisodePersistenceException("Failed to load episodes", ex);
        }
    }
}
