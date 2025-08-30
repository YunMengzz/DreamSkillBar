package com.github.yunmeng.custom.skills;

import com.github.yunmeng.config.ILandConfig;
import com.github.yunmeng.manager.PlayerSkillData;
import com.github.yunmeng.utils.SkillUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class ISkill {
    public static HashMap<String, ISkill> iSkills = new HashMap();

    private int cooldown;
    private double cooldownAdd;
    private int maxLevel;
    private int point;
    private int mana;
    private double manaAdd;
    private double pointAdd;
    private HashMap<String, Integer> req;
    private HashMap<Integer, Integer> needLevel;
    // 描述!
    private ItemStack stack;

    /**
     * 玩家能否学习这个技能判断方法
     * @param player 玩家
     * @param skill 技能
     * @return 能否学习这个方法
     */
    public boolean isStudy(Player player, String skill) {
        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
        int i = psd.getSkillLevel(skill);
        if (this.needLevel.containsKey(i + 1)) {
            int playerLevel = player.getLevel();
            if (this.needLevel.get(i + 1) > playerLevel) {
                return false;
            }
        }
        return true;
    }

    public boolean containsSkill(Player player, String skill) {
        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
        return psd.getSkillData().containsKey(skill);
    }

    /**
     * 学习技能方法
     * @param player
     * @param skill
     */
    public void study(Player player, String skill) {
        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
        int i = psd.getSkillLevel(skill);
        if (this.needLevel.containsKey(i + 1)) {
            int playerLevel = player.getLevel();
            if (this.needLevel.get(i + 1) > playerLevel) {
                player.sendMessage(ILandConfig.langMap.get("NoLevel"));
                player.closeInventory();
                return;
            }
        }
        if (i + 1 <= this.maxLevel) {
            int playerSkillPoint = psd.getSkillPoint();
            // 计算加点
            int need = (int) (this.pointAdd * i + this.point);
            // 判断加点 是否够
            if (playerSkillPoint >= need) {
                if (SkillUtil.takePlayerSkillPoint(player, need)) {
                    psd.addSkillLevel(skill);
                    PlayerSkillData.playerDatas.put(player.getName(), psd);
                    player.sendMessage(ILandConfig.langMap.get("SkillLevelUp").replace("<level>", i + 1 + "").replace("<skill>", skill));
                }
            } else player.sendMessage(ILandConfig.langMap.get("NoPoint"));
        } else {
            player.sendMessage(ILandConfig.langMap.get("LevelMax"));
        }
    }

    /**
     * 获取描述信息方法
     * @param level
     * @return
     */
    public ItemStack getStack(int level) {
        level++;
        ItemStack stack1 = this.stack.clone();
        ItemMeta meta = stack1.getItemMeta();
        List<String> lores = meta.getLore();
        List<String> newLores = new ArrayList<>();
        int need = (int) ((level - 1) * this.pointAdd + this.point);
        for (String lore : lores) {
            newLores.add(lore.replace("<level>", level - 1 + "").replace("<max>", this.maxLevel + "")
                    .replace("<point>", need + ""));
        }
        if (this.needLevel.containsKey(level)) {
            newLores.add(ILandConfig.langMap.get("NeedLevel").replace("<needlevel>", this.needLevel.get(level) + ""));
        } else {
            newLores.add(ILandConfig.langMap.get("NoNeedLevel"));
        }
        for (String needSkill : this.req.keySet()) {
            newLores.add(needSkill + " >> " + this.req.get(needSkill));
        }
        meta.setLore(newLores);
        stack1.setItemMeta(meta);
        return stack1;
    }


    /**
     * 设置详细信息方法
     * @param name
     * @param id
     * @param loreList
     */
    public void setStack(String name, String id, List<String> loreList) {
        ItemStack stack = new ItemStack(Material.STONE);
        String[] s = id.split(":");
        stack.setTypeId(Integer.parseInt(s[0]));
        stack.setDurability(Short.parseShort(s[1]));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(loreList);
        stack.setItemMeta(meta);
        this.stack = stack.clone();
    }

    public HashMap<Integer, Integer> getNeedLevel() {
        return this.needLevel;
    }

    public void setNeedLevel(HashMap<Integer, Integer> needLevel) {
        this.needLevel = needLevel;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPoint() {
        return this.point;
    }

    public int getMana() {
        return this.mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public double getManaAdd() {
        return this.manaAdd;
    }

    public void setManaAdd(double manaAdd) {
        this.manaAdd = manaAdd;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public double getCooldownAdd() {
        return this.cooldownAdd;
    }

    public double getPointAdd() {
        return this.pointAdd;
    }

    public HashMap<String, Integer> getReq() {
        return this.req;
    }

    public void setCooldownAdd(double cooldownAdd) {
        this.cooldownAdd = cooldownAdd;
    }

    public void setPointAdd(double pointAdd) {
        this.pointAdd = pointAdd;
    }

    public void setReq(HashMap<String, Integer> req) {
        this.req = req;
    }
}