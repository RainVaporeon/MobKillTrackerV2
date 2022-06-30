package com.spiritlight.mobkilltracker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigSpirit {
    private static boolean antiLoop = false;
    public static void read() throws IOException {
        File config = new File("config/MKTv2.json");
        if (config.exists()) {
            try {
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = (JsonObject) parser.parse(new FileReader("config/MKTv2.json"));
                Main.def_duration = jsonObject.get("def_duration").getAsInt();
            } catch (NullPointerException exception) {
                System.out.println("New configuration files found?");
                if(antiLoop) return;
                antiLoop = true;
                write();
                read();
            }
        } else {
            write();
        }
    }

    public static void write() throws IOException {
        JsonWriter writer = new JsonWriter(new FileWriter("config/MKTv2.json"));
        writer.beginObject();
        writer.name("def_duration").value(Main.def_duration);
        writer.endObject();
        writer.close();
    }

    public static boolean save() {
        try {
            write();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
