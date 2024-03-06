package com.kterising.Functions;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesConfig {
    private static File file;
    private static FileConfiguration customFile;


    public static void setup(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("KteRising").getDataFolder(), "messages.yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                System.out.println("messages.yml is not created");
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get(){
        return customFile;
    }

    public static void save(){
        try{
            customFile.save(file);
        }catch (IOException e){
            System.out.println("File not saved");
        }
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}
