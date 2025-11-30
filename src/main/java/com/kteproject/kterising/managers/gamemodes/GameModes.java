package com.kteproject.kterising.managers.gamemodes;
import java.util.List;

public class GameModes {
    private final String name;
    private final boolean enabled;
    private final String label;
    private final String description;
    private final int guiSlot;
    private final List<ModeItem> items;
    private final int countdown;
    private final String placeholderlabel;

    public GameModes(String name, boolean enabled, String label, String description, int guiSlot, List<ModeItem> items, int countdown, String placeholderlabel) {
        this.name = name;
        this.enabled = enabled;
        this.label = label;
        this.description = description;
        this.guiSlot = guiSlot;
        this.items = items;
        this.countdown = countdown;
        this.placeholderlabel = placeholderlabel;
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public List<ModeItem> getItems() { return items; }
    public int getCountdown() { return countdown; }
    public String getPlaceholderlabel() { return placeholderlabel; }

}
