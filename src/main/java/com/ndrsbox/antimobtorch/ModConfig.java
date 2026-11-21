package com.ndrsbox.antimobtorch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "antimobtorch.json");

    // Independent configuration values for Soul Torches
    public double torchRadius = 16.0;
    public double torchPushStrength = 0.25;

    // Independent configuration values for Soul Lanterns
    public double lanternRadius = 24.0;
    public double lanternPushStrength = 0.35;

    public boolean preventNaturalSpawns = true;

    private static ModConfig INSTANCE = new ModConfig();

    public static ModConfig get() {
        return INSTANCE;
    }

    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
                if (INSTANCE == null) {
                    INSTANCE = new ModConfig();
                }
            } catch (IOException e) {
                Antimobtorch.LOGGER.error("Failed to load antimobtorch config, using defaults", e);
            }
        } else {
            save(); // Create the file with default values if it doesn't exist
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            Antimobtorch.LOGGER.error("Failed to save antimobtorch config", e);
        }
    }
}
