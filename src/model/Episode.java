package model;

import java.time.LocalDateTime;

public abstract class Episode {

    private String id;
    private String title;
    private int durationMinutes;
    private LocalDateTime scheduledDateTime;

    public Episode(String id, String title, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.durationMinutes = durationMinutes;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getDurationMinutes() { return durationMinutes; }
    public LocalDateTime getScheduledDateTime() { return scheduledDateTime; }

    public void setScheduledDateTime(LocalDateTime dateTime) {
        this.scheduledDateTime = dateTime;
    }

    // Subclasses will provide a label (Regular or Bonus)
    public abstract String getTypeLabel();
}
