package com.github.yunmeng.custom.skills;

import com.github.yunmeng.manager.PlayerSkillData;
import com.github.yunmeng.utils.SkillCaster;

import java.util.HashMap;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class SkillCombo
        implements Cloneable {
    public static HashMap<OfflinePlayer, SkillCombo> playerSkillCombos = new HashMap();
    public static HashMap<String, SkillCombo> skillCombos = new HashMap();

    private int time;
    private HashMap<Integer, String> process = new HashMap();

    public SkillCombo clone() {
        SkillCombo c = null;
        try {
            c = (SkillCombo) super.clone();
            c.setProcess((HashMap) this.process.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return c;
    }

    public SkillCombo(List<String> pcs) {
        int time = 0;
        for (String line : pcs) {
            if (line.startsWith("delay")) {
                time += Integer.parseInt(line.replace("delay:", ""));
            }
        }
        this.time = time;
        for (String line : pcs) {
            if (line.startsWith("delay")) {
                time -= Integer.parseInt(line.replace("delay:", ""));
            } else {
                this.process.put(Integer.valueOf(time), line);
            }
        }
    }

    public boolean read(Player player) {
        if (this.process.containsKey(Integer.valueOf(this.time))) {
            String[] s = ((String) this.process.get(Integer.valueOf(this.time))).split(":");
            if (s[0].equals("cast")) {
                String name = player.getName();
                PlayerSkillData psd = (PlayerSkillData) PlayerSkillData.playerDatas.getOrDefault(name, new PlayerSkillData());
                int level = psd.getSkillLevel(name);
                SkillCaster.castSkill(s[1], level, player);
            }
            if (s[0].equals("forecast")) {
                String name = player.getName();
                PlayerSkillData psd = (PlayerSkillData) PlayerSkillData.playerDatas.getOrDefault(name, new PlayerSkillData());
                int level = psd.getSkillLevel(name);
                SkillCaster.foreCastSkill(s[1], level, player);
            }
        }
        this.time -= 1;
        return this.time <= 0;
    }

    public void setProcess(HashMap<Integer, String> process) {
        this.process = process;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return this.time;
    }

    public HashMap<Integer, String> getProcess() {
        return this.process;
    }
}