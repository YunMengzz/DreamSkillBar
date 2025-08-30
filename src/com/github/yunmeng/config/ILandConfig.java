package com.github.yunmeng.config;

import com.germ.germplugin.api.dynamic.gui.GuiManager;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiButton;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiLabel;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiScroll;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiTexture;
import com.github.yunmeng.ILandSkillBar;
import com.github.yunmeng.custom.profess.ILandProfess;
import com.github.yunmeng.custom.profess.ProfessUtil;
import com.github.yunmeng.custom.skills.ISkill;
import com.github.yunmeng.custom.skills.ISkillPart;
import com.github.yunmeng.custom.skills.SkillCombo;
import com.github.yunmeng.database.ILandDataBase;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.yunmeng.utils.SkillUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class ILandConfig {
    public static HashMap<String, String> langMap = new HashMap<>();

    public static void loadConfig() {
        File file = new File(ILandSkillBar.root, "config.yml");
        FileConfiguration config = load(file);
        ConfigurationSection sec = config.getConfigurationSection("Lang");
        for (String temp : sec.getKeys(false)) {
            langMap.put(temp, sec.getString(temp).replace('&', '§'));
        }

        ISkill.iSkills = new HashMap<>();
        File skills = new File(ILandSkillBar.root, "skills.yml");
        FileConfiguration skill = load(skills);
        for (String temp : skill.getKeys(false)) {
            ISkill is = new ISkill();
            is.setCooldown(skill.getInt(temp + ".cooldown"));
            is.setCooldownAdd(skill.getDouble(temp + ".cooldownadd"));
            is.setMaxLevel(skill.getInt(temp + ".maxlevel"));
            is.setPoint(skill.getInt(temp + ".point"));
            is.setPointAdd(skill.getDouble(temp + ".pointadd"));
            is.setMana(skill.getInt(temp + ".mana"));
            is.setManaAdd(skill.getDouble(temp + ".manaadd"));
            HashMap reqMap = new HashMap<>();
            ConfigurationSection seca = skill.getConfigurationSection(temp + ".req");
            if (seca != null) {
                for (String skillName : seca.getKeys(false)) {
                    reqMap.put(skillName, seca.getInt(skillName));
                }
            }
            Object needLevelMap = new HashMap();
            ConfigurationSection secb = skill.getConfigurationSection(temp + ".levelneed");
            if (secb != null) {
                for (String level : secb.getKeys(false)) {
                    ((HashMap) needLevelMap).put(Integer.parseInt(level), secb.getInt(level));
                }
            }
            is.setReq(reqMap);
            is.setNeedLevel((HashMap<Integer, Integer>) needLevelMap);
            is.setStack(temp, skill.getString(temp + ".id"), skill.getStringList(temp + ".lore"));
            ISkill.iSkills.put(temp, is);
            System.out.print(temp);
        }

        /*HashMap<String, Integer> reqMap;*/
        ISkillPart.skillParts = new HashMap<>();
        File files = new File(ILandSkillBar.root, "parts.yml");
        FileConfiguration parts = load(files);
        int index = 0;
        for (String temp : parts.getKeys(false)) {
            String x = parts.getString(temp + ".x");
            String y = parts.getString(temp + ".y");
            String texture = parts.getString(temp + ".texture");
            String lockTexture = parts.getString(temp + ".locktexture");
            String width = parts.getString(temp + ".width");
            String height = parts.getString(temp + ".height");
            ISkillPart isp = new ISkillPart(x, y, index, texture, lockTexture, width, height);
            ISkillPart.skillParts.put(temp, isp);
            index++;
        }
        /*int x;*/
        ProfessUtil.professMap = new HashMap<>();
        File prof = new File(ILandSkillBar.root, "profess.yml");
        FileConfiguration profess = load(prof);
        ProfessUtil.professMap.put(langMap.get("DefaultProfess"), new ILandProfess((String) langMap.get("DefaultProfess"), "1:0", new ArrayList<>(), new ArrayList<>()));
        for (String temp : profess.getKeys(false)) {
            String id = profess.getString(temp + ".id");
            List<String> lores = profess.getStringList(temp + ".lore");
            List<String> skillList = profess.getStringList(temp + ".skills");
            ILandProfess ilp = new ILandProfess(temp, id, lores, skillList);
            ProfessUtil.professMap.put(temp, ilp);
        }

        ILandSkillBar.ildb = new ILandDataBase();
        ILandSkillBar.ildb.host = config.getString("MySQLHost");
        ILandSkillBar.ildb.port = config.getString("MySQLPort");
        ILandSkillBar.ildb.username = config.getString("MySQLUsername");
        ILandSkillBar.ildb.password = config.getString("MySQLPassword");
        ILandSkillBar.ildb.databaseName = config.getString("MySQLDatabase");
        ILandSkillBar.ildb.useSSL = config.getBoolean("MySQLUseSSL");
        try {
            if (ILandSkillBar.ildb.getConnection() == null) Bukkit.getServer().shutdown();
            ILandSkillBar.ildb.createPlayerDataTable();
            ILandSkillBar.ildb.createBindTable();
            ILandSkillBar.ildb.createSkillTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SkillCombo.skillCombos = new HashMap<>();
        File com = new File(ILandSkillBar.root, "skillcombo.yml");
        FileConfiguration combo = load(com);
        for (String temp : combo.getKeys(false)) {
            List<String> processList = combo.getStringList(temp);
            SkillCombo sc = new SkillCombo(processList);
            SkillCombo.skillCombos.put(temp, sc);
        }

        ConfigurationSection section = config.getConfigurationSection("Gui.openb");

        GermGuiTexture ggt = new GermGuiTexture("iland_skill_beijing_texture");
        ggt.setPath(getByKey(section, "path", "local<->textures/test/技能界面.png"));
        ggt.setLocationX(getByKey(section, "x", "0.25*w"));
        ggt.setLocationY(getByKey(section, "y", "0.25*h"));
        ggt.setLocationZ(getByKey(section, "z", "0"));
        ggt.setWidth(getByKey(section, "width", "0.5*w"));
        ggt.setHeight(getByKey(section, "height", "0.8*h"));
        SkillUtil.ggt = ggt;

        section = config.getConfigurationSection("Gui.scroll");

        GermGuiScroll ggs = new GermGuiScroll("iland_skill_scroll");
        ggs.setWidth(getByKey(section, "width", "0.25*w"));
        ggs.setHeight(getByKey(section, "height", "0.8*h"));
        ggs.setLocationX(getByKey(section, "locationX", "0.25*w"));
        ggs.setLocationY(getByKey(section, "locationY", "0.25*h"));
        ggs.setLocationVX(getByKey(section, "locationVX", "0.25*w"));
        ggs.setLocationVY(getByKey(section, "locationVY", "0.25*h"));
        ggs.setWidthV(getByKey(section, "widthV", "0.2*w"));
        ggs.setHeightV(getByKey(section, "heightV", "0.8*h"));
        ggs.setInvalidV(Boolean.parseBoolean(section.get("invalidV") == null ? "true" : section.getString("invalidV")));
        ggs.setScrollableV("1500");
        ggs.setScrolledV("5");
        ggs.setWidthH("400");
        ggs.setHeightH("20")
                .setInvalidH(true)
                .setScrollableH("500")
                .setLocationHX("100")
                .setLocationHY("100");
        SkillUtil.ggs = ggs;

        GermGuiButton gButton = new GermGuiButton("sliderV");
        gButton.setDefaultPath(getByKey(section, "texture", "local<->textures/test/滚动.png"));
        gButton.setHoverPath(getByKey(section, "texture", "local<->textures/test/滚动.png"));
        gButton.setWidth("10");
        gButton.setHeight("35");
        SkillUtil.sliderV = gButton;

        GermGuiButton lub = new GermGuiButton("iland_skill_levelup_button");
        lub.setDefaultPath("local<->textures/test/button.png");
        lub.setHoverPath("local<->textures/test/button_.png");
        lub.setClickSound("minecraft:ui.button.click");
        String temp = "Gui.levelUpButton";
        String x = config.getString(temp + ".x");
        String y = config.getString(temp + ".y");
        String w = config.getString(temp + ".width");
        String h = config.getString(temp + ".height");
        lub.setLocationX(x);
        lub.setLocationY(y);
        lub.setLocationZ("7");
        lub.setWidth(w);
        lub.setHeight(h);
        lub.setAlign("center");
        lub.setTextScale("1");
        lub.setFont("default");
        lub.setText("§e升级技能");
        SkillUtil.levelUpButton = lub;

        GermGuiLabel ggl = new GermGuiLabel("iland_skill_levelup_label");
        temp = "Gui.skillPointLabel";
        x = config.getString(temp + ".x");
        y = config.getString(temp + ".y");
        ggl.setLocationX(x);
        ggl.setLocationY(y);
        ggl.setLocationZ("10");
        ggl.setScale("1");
        SkillUtil.skillPointLabel = ggl;

        ggl = new GermGuiLabel("iland_skill_scroll_label_skillinfo");
        temp = "Gui.skillLabel";
        x = config.getString(temp + ".x");
        y = config.getString(temp + ".y");
        ggl.setLocationX(x);
        ggl.setLocationY(y);
        ggl.setLocationZ("5");
        List<String> info = new ArrayList<>();
        info.add("§a请点击一个技能查看具体信息！");
        ggl.setTexts(info);
        SkillUtil.skillLabel = ggl;
    }

    public static FileConfiguration load(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    private static String getByKey(ConfigurationSection section, String key, String defaultValue) {
        return section.get(key) == null ? defaultValue : section.getString(key);
    }

}


