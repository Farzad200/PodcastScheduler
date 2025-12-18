package model;

public class BonusEpisode extends Episode {

    public BonusEpisode(String title, int durationMinutes) {
        super(title, durationMinutes);
    }

    @Override
    public String getTypeLabel() {
        return "Bonus";
    }
}
