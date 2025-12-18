package model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Episode implements Publishable {

    protected String id;
    protected String title;
    protected int durationMinutes;
    protected EpisodeStatus status;
    protected LocalDateTime scheduledDateTime;

    public Episode(String title, int durationMinutes) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.status = EpisodeStatus.DRAFT;
        this.scheduledDateTime = null;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public EpisodeStatus getStatus() {
        return status;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    @Override
    public void schedule(LocalDateTime dateTime) {
        this.scheduledDateTime = dateTime;
        this.status = EpisodeStatus.SCHEDULED;
    }

    @Override
    public boolean canPublish(LocalDateTime now) {
        return status == EpisodeStatus.SCHEDULED
                && scheduledDateTime != null
                && !now.isBefore(scheduledDateTime);
    }

    @Override
    public void publish(LocalDateTime now) {
        if (canPublish(now)) {
            status = EpisodeStatus.PUBLISHED;
        }
    }

    public abstract String getTypeLabel();

    @Override
    public String toString() {
        String timeInfo = (scheduledDateTime == null)
                ? "Not scheduled"
                : scheduledDateTime.toString();

        return "[" + getTypeLabel() + "] "
                + title + " (" + durationMinutes + " min) - "
                + status + " | " + timeInfo;
    }
}
