package com.github.yunmeng.manager;

import com.github.yunmeng.config.ILandConfig;
import com.github.yunmeng.database.manager.DatabasePlayerProfess;

import java.util.HashMap;


public class PlayerSkillData {
    public static HashMap<String, PlayerSkillData> playerDatas = new HashMap<>();

    private HashMap<String, Integer> skillData = new HashMap<>();
    private int skillPoint = 0;
    private String profess = ILandConfig.langMap.get("DefaultProfess");


    public PlayerSkillData() {
    }

    public PlayerSkillData(DatabasePlayerProfess dpp) {
        this.skillPoint = dpp.getSkillPoint();
        this.profess = dpp.getProfess();
    }

    public boolean isStudy(String skill) {
        return this.skillData.containsKey(skill);
    }

    public int getSkillLevel(String skill) {
        return this.skillData.getOrDefault(skill, 0);
    }


    public void addSkillLevel(String skill) {
        this.skillData.put(skill, getSkillLevel(skill) + 1);
    }

    public boolean delSkillLevel(String skill) {
        int i = getSkillLevel(skill) - 1;
        if (i < 0) {
            return false;
        }
        this.skillData.put(skill, i);
        return true;
    }

    public HashMap<String, Integer> getSkillData() {
        return this.skillData;
    }

    public void setSkillData(HashMap<String, Integer> skillData) {
        this.skillData = skillData;
    }

    public int getSkillPoint() {
        return this.skillPoint;
    }

    public void addSkillPoint(int i) {
        this.skillPoint += i;
    }

    public String getProfess() {
        return this.profess;
    }

    public boolean changeProfess(String profess, int level) {
        this.profess = profess;
        this.skillData = new HashMap<>();
        this.skillPoint = (level * 2);
        return true;
    }

    public void foreChangeProfess(String profess, int skillPoint) {
        this.profess = profess;
        this.skillData = new HashMap<>();
        this.skillPoint = skillPoint;
    }

    public void setProfess(String profess) {
        this.profess = profess;
    }

    public void setSkillPoint(int skillPoint) {
        this.skillPoint = skillPoint;
    }
}
