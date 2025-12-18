package model;

public class RegularEpisode extends Episode {

    public RegularEpisode(String title, int durationMinutes) {
        super(title, durationMinutes);
    }

    @Override
    public String getTypeLabel() {
        return "Regular";
    }
}
