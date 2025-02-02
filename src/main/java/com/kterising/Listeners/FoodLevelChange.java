package com.kterising.Listeners;

import com.kterising.Functions.StartGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChange implements Listener {
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!StartGame.match) {
            event.getEntity().setFoodLevel(20);
        }
    }
}
