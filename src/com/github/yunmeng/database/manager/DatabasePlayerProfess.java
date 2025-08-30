package com.github.yunmeng.database.manager;

public class DatabasePlayerProfess {
    private String name;
    private String profess;
    private int skillPoint;

    public DatabasePlayerProfess(String name, String profess, int skillPoint) {
        this.name = name;
        this.profess = profess;
        this.skillPoint = skillPoint;
    }

    public void setProfess(String profess) {
        this.profess = profess;
    }

    public String getProfess() {
        return this.profess;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSkillPoint() {
        return this.skillPoint;
    }

    public void setSkillPoint(int skillPoint) {
        this.skillPoint = skillPoint;
    }
}
