package com.codar5.gameinfo.config;

import com.codar5.gameinfo.util.HexUtil;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class GameInfoConfig {
    public boolean showFPS = true;
    public boolean showTimeAndDays = true;
    public boolean showSlimeChunk = true;
    public boolean showCoordinates = true;
    public boolean showNetherCoordinates = true;
    public boolean showBiome = true;
    public int color = -256;
    public Integer xPos = 3;
    public Integer yPos = 3;
    public boolean remark = true;
    public String version = "";
    public boolean showEquipment = true;
    public boolean showFurnaceInfo = false;
    public boolean showEntityInfo = false;
    public boolean showBlockInfo = false;
    public boolean enabled = true;
    private static final String CONFIG_FILE = "config" + File.separator + "gameinfo.properties";

    public GameInfoConfig() {
        this.loadConfig();
    }

    public void copyFrom(GameInfoConfig other) {
        this.showFPS = other.showFPS;
        this.showTimeAndDays = other.showTimeAndDays;
        this.showSlimeChunk = other.showSlimeChunk;
        this.showCoordinates = other.showCoordinates;
        this.showNetherCoordinates = other.showNetherCoordinates;
        this.showBiome = other.showBiome;
        this.color = other.color;
        this.xPos = other.xPos;
        this.yPos = other.yPos;
        this.remark = other.remark;
        this.version = other.version;
        this.showEquipment = other.showEquipment;
        this.showFurnaceInfo = other.showFurnaceInfo;
        this.showEntityInfo = other.showEntityInfo;
        this.showBlockInfo = other.showBlockInfo;
        this.enabled = other.enabled;
    }

    public void closeGameInfo(Boolean flag) {
        if (flag) {
            this.showFPS = false;
            this.showTimeAndDays = false;
            this.showSlimeChunk = false;
            this.showCoordinates = false;
            this.showNetherCoordinates = false;
            this.showBiome = false;
            this.remark = false;
            this.showEquipment = false;
            this.showFurnaceInfo = false;
            this.showEntityInfo = false;
            this.showBlockInfo = false;
        } else {
            this.loadConfig();
            System.out.println("[GameInfo] " + new Gson().toJson(this));
        }
    }

    public void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            this.saveConfig();
        }
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
            this.showFPS = Boolean.parseBoolean(properties.getProperty("showFPS", "true"));
            this.showTimeAndDays = Boolean.parseBoolean(properties.getProperty("showTimeAndDays", "true"));
            this.showSlimeChunk = Boolean.parseBoolean(properties.getProperty("showSlimeChunk", "true"));
            this.showCoordinates = Boolean.parseBoolean(properties.getProperty("showCoordinates", "true"));
            this.showNetherCoordinates = Boolean.parseBoolean(properties.getProperty("showNetherCoordinates", "true"));
            this.showBiome = Boolean.parseBoolean(properties.getProperty("showBiome", "true"));
            this.color = (int) Long.parseLong(properties.getProperty("color", "0xFFFFFF00").replace("0x", ""), 16);
            this.xPos = Integer.parseInt(properties.getProperty("xPos", "3"));
            this.yPos = Integer.parseInt(properties.getProperty("yPos", "3"));
            this.remark = Boolean.parseBoolean(properties.getProperty("remark", "true"));
            this.version = properties.getProperty("version", "");
            this.showEquipment = Boolean.parseBoolean(properties.getProperty("showEquipment", "true"));
            this.showFurnaceInfo = Boolean.parseBoolean(properties.getProperty("showFurnaceInfo", "false"));
            this.showEntityInfo = Boolean.parseBoolean(properties.getProperty("showEntityInfo", "false"));
            this.showBlockInfo = Boolean.parseBoolean(properties.getProperty("showBlockInfo", "false"));
            this.enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        Properties properties = new Properties();
        properties.setProperty("showFPS", Boolean.toString(this.showFPS));
        properties.setProperty("showTimeAndDays", Boolean.toString(this.showTimeAndDays));
        properties.setProperty("showSlimeChunk", Boolean.toString(this.showSlimeChunk));
        properties.setProperty("showCoordinates", Boolean.toString(this.showCoordinates));
        properties.setProperty("showNetherCoordinates", Boolean.toString(this.showNetherCoordinates));
        properties.setProperty("showBiome", Boolean.toString(this.showBiome));
        properties.setProperty("color", HexUtil.toHex(this.color));
        properties.setProperty("xPos", this.xPos.toString());
        properties.setProperty("yPos", this.yPos.toString());
        properties.setProperty("remark", Boolean.toString(this.remark));
        properties.setProperty("version", this.version);
        properties.setProperty("showEquipment", Boolean.toString(this.showEquipment));
        properties.setProperty("showFurnaceInfo", Boolean.toString(this.showFurnaceInfo));
        properties.setProperty("showEntityInfo", Boolean.toString(this.showEntityInfo));
        properties.setProperty("showBlockInfo", Boolean.toString(this.showBlockInfo));
        properties.setProperty("enabled", Boolean.toString(this.enabled));
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
