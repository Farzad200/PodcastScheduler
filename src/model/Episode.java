package model;

import java.time.LocalDateTime;

public abstract class Episode implements Publishable {

    private String id;
    private String title;
    private int durationMinutes;
    private LocalDateTime scheduledDateTime;
    private EpisodeStatus status = EpisodeStatus.DRAFT;

    public Episode(String id, String title, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.durationMinutes = durationMinutes;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getDurationMinutes() { return durationMinutes; }
    public LocalDateTime getScheduledDateTime() { return scheduledDateTime; }

    public EpisodeStatus getStatus() {
        return status;
    }

    public void setScheduledDateTime(LocalDateTime dateTime) {
        this.scheduledDateTime = dateTime;
    }

    public abstract String getTypeLabel();

    @Override
    public void schedule(LocalDateTime dateTime) throws ScheduleConflictException {
        this.scheduledDateTime = dateTime;
        this.status = EpisodeStatus.SCHEDULED;
    }

    @Override
    public void publish(LocalDateTime now) {
        if (canPublish(now)) {
            this.status = EpisodeStatus.PUBLISHED;
        }
    }

    @Override
    public boolean canPublish(LocalDateTime now) {
        return scheduledDateTime != null && !scheduledDateTime.isAfter(now);
    }
}
