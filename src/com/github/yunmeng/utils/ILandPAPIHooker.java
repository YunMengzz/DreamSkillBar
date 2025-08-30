package com.github.yunmeng.utils;

import com.github.yunmeng.manager.PlayerSkillData;

import java.util.HashMap;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class ILandPAPIHooker
        extends PlaceholderExpansion {
    public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin(getPlugin()) != null;
    }

    public String getAuthor() {
        return "Jimy_Spirits";
    }

    public String getIdentifier() {
        return "cil";
    }

    public String getPlugin() {
        return "ILandSkillBar";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String onPlaceholderRequest(Player player, String s) {
        if (s.contains("profess")) {
            String pName = player.getName();
            if (PlayerSkillData.playerDatas.containsKey(pName))
                return PlayerSkillData.playerDatas.get(player.getName()).getProfess();
            return "§a读取中..";
        }
        if (s.contains("skill")) {
            String pName = player.getName();
            if (PlayerSkillData.playerDatas.containsKey(pName))
                return PlayerSkillData.playerDatas.get(player.getName()).getSkillPoint() + "";
            return "0";
        }
        if (s.contains("cooldown")) {
            String pName = player.getName();
            HashMap<Integer, String> binds = SkillCaster.playerSkillBinds.getOrDefault(pName, new HashMap<>());
            if (s.contains("1")) {
                String skName = binds.get(1);
                if (skName == null) return "0";
                if (s.contains("max")) {
                    PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
                    int level = psd.getSkillLevel(skName);
                    int cooldown = SkillUtil.getSkillCooldown(skName, level);
                    return cooldown + "";
                }
                if (s.contains("now")) {
                    HashMap<String, Integer> cdMap = SkillCaster.cooldownMap.getOrDefault(player, new HashMap<>());
                    return cdMap.getOrDefault(skName, 0) + "";
                }
            } else if (s.contains("2")) {
                String skName = (String) binds.get(2);
                if (skName == null) return "0";
                if (s.contains("max")) {
                    PlayerSkillData psd = (PlayerSkillData) PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
                    int level = psd.getSkillLevel(skName);
                    int cooldown = SkillUtil.getSkillCooldown(skName, level);
                    return cooldown + "";
                }
                if (s.contains("now")) {
                    HashMap<String, Integer> cdMap = SkillCaster.cooldownMap.getOrDefault(player, new HashMap<>());
                    return cdMap.getOrDefault(skName, 0) + "";
                }
            } else if (s.contains("3")) {
                String skName = (String) binds.get(3);
                if (skName == null) return "0";
                if (s.contains("max")) {
                    PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
                    int level = psd.getSkillLevel(skName);
                    int cooldown = SkillUtil.getSkillCooldown(skName, level);
                    return cooldown + "";
                }
                if (s.contains("now")) {
                    HashMap<String, Integer> cdMap = SkillCaster.cooldownMap.getOrDefault(player, new HashMap<>());
                    return cdMap.getOrDefault(skName, 0) + "";
                }
            } else if (s.contains("4")) {
                String skName = (String) binds.get(4);
                if (skName == null) return "0";
                if (s.contains("max")) {
                    PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
                    int level = psd.getSkillLevel(skName);
                    int cooldown = SkillUtil.getSkillCooldown(skName, level);
                    return cooldown + "";
                }
                if (s.contains("now")) {
                    HashMap<String, Integer> cdMap = SkillCaster.cooldownMap.getOrDefault(player, new HashMap<>());
                    return cdMap.getOrDefault(skName, 0) + "";
                }
            } else if (s.contains("5")) {
                String skName = (String) binds.get(5);
                if (skName == null) return "0";
                if (s.contains("max")) {
                    PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
                    int level = psd.getSkillLevel(skName);
                    int cooldown = SkillUtil.getSkillCooldown(skName, level);
                    return cooldown + "";
                }
                if (s.contains("now")) {
                    HashMap<String, Integer> cdMap = SkillCaster.cooldownMap.getOrDefault(player, new HashMap<>());
                    return cdMap.getOrDefault(skName, 0) + "";
                }
            }
        }
        return null;
    }
}
