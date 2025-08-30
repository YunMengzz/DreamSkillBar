package com.github.yunmeng.utils;

import com.github.yunmeng.config.ILandConfig;
import com.github.yunmeng.custom.skills.SkillCombo;
import com.rit.sucy.player.TargetHelper;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.skills.TargetSkill;

import java.util.HashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class SkillCaster {
    public static HashMap<LivingEntity, HashMap<String, Integer>> cooldownMap = new HashMap<>();
    public static HashMap<String, HashMap<Integer, String>> playerSkillBinds = new HashMap<>();

    public static String getNumSkill(Player player, int i) {
        String name = player.getName();
        if (playerSkillBinds.containsKey(name)) {
            return (playerSkillBinds.get(name)).get(i);
        }
        return null;
    }

    public static boolean defCast(String skillName, int skillLevel, Player player) {
        int leftTime = cooldownCheck(player, skillName);
        if (leftTime > 0) {
            player.sendMessage(ILandConfig.langMap.get("SkillCooldown").replace("<time>", leftTime + "").replace("<skill>", skillName));
            return false;
        }
        boolean cast = false;
        if (SkillCombo.skillCombos.containsKey(skillName)) {
            if (checkCast(skillName, skillLevel, player)) {
                SkillCombo sc = SkillCombo.skillCombos.get(skillName);
                SkillCombo.playerSkillCombos.put(player, sc);
                cast = false;
            }
        } else {
            cast = castSkill(skillName, skillLevel, player);
        }
        if (cast) {
            putSkillCooldown(skillName, skillLevel, player);
        }
        return cast;
    }

    public static boolean checkCast(String skillName, int skillLevel, LivingEntity liv) {
        if ((liv instanceof Player)) {
            int leftTime = cooldownCheck(liv, skillName);
            if (leftTime > 0) {
                liv.sendMessage(ILandConfig.langMap.get("SkillCooldown").replace("<time>", leftTime + "").replace("<skill>", skillName));
                return false;
            }
            Player player = (Player) liv;
            PlayerData pd = SkillAPI.getPlayerData(player);
            int mana = SkillUtil.getSkillMana(skillName, skillLevel);
            if (pd.getMana() < mana) {
                player.sendMessage(ILandConfig.langMap.get("NoMana"));
                return false;
            }
            pd.setMana(pd.getMana() - mana);
        }

        return true;
    }

    public static boolean castSkill(String skillName, int skillLevel, LivingEntity liv) {
        if ((liv instanceof Player)) {
            int leftTime = cooldownCheck(liv, skillName);
            if (leftTime > 0) {
                liv.sendMessage(ILandConfig.langMap.get("SkillCooldown").replace("<time>", leftTime + "").replace("<skill>", skillName));
                return false;
            }
            Player player = (Player) liv;
            PlayerData pd = SkillAPI.getPlayerData(player);
            PlayerSkill psa = pd.getSkill(skillName);
            if (psa != null) {
                int now = psa.getLevel();
                if (now != skillLevel) {
                    if (now > skillLevel) {
                        while ((psa.getLevel() > skillLevel) &&
                                (psa.getLevel() > 0)) {
                            pd.forceDownSkill(psa);
                        }
                    }
                    while ((psa.getLevel() < skillLevel) &&
                            (!psa.isMaxed())) {
                        pd.forceUpSkill(psa);
                    }
                }
            }

            Skill skill = SkillAPI.getSkill(skillName);
            PlayerSkill ps = new PlayerSkill(pd, skill, pd.getClass("default"));
            if ((ps.getData() instanceof SkillShot)) {
                int mana = SkillUtil.getSkillMana(skillName, skillLevel);
                if (pd.getMana() < mana) {
                    player.sendMessage(ILandConfig.langMap.get("NoMana"));
                    return false;
                }
                pd.setMana(pd.getMana() - mana);
                SkillShot ss = (SkillShot) skill;
                ss.cast(liv, skillLevel);
            } else if ((ps.getData() instanceof TargetSkill)) {
                LivingEntity ent = TargetHelper.getLivingTarget(player, skill.getRange(skillLevel));
                if (ent != null) {
                    try {
                        int mana = SkillUtil.getSkillMana(skillName, skillLevel);
                        if (pd.getMana() < mana) {
                            player.sendMessage(ILandConfig.langMap.get("NoMana"));
                            return false;
                        }
                        boolean attack = !SkillAPI.getSettings().canAttack(player, ent);
                        pd.setMana(pd.getMana() - mana);
                        TargetSkill ts = (TargetSkill) skill;
                        ts.cast(player, ent, skillLevel, attack);
                    } catch (Exception localException) {
                    }
                }
            }
        }


        return true;
    }

    public static void foreCastSkill(String skillName, int skillLevel, LivingEntity liv) {
        if ((liv instanceof Player)) {
            Player player = (Player) liv;
            PlayerData pd = SkillAPI.getPlayerData(player);
            Skill skill = SkillAPI.getSkill(skillName);
            PlayerSkill ps = new PlayerSkill(pd, skill, pd.getClass("default"));
            PlayerSkill psa = pd.getSkill(skillName);
            if (psa != null) {
                int now = psa.getLevel();
                if (now != skillLevel) {
                    if (now > skillLevel) {
                        while ((psa.getLevel() > skillLevel) &&
                                (psa.getLevel() > 0)) {
                            pd.forceDownSkill(psa);
                        }
                    }
                    while ((psa.getLevel() < skillLevel) &&
                            (!psa.isMaxed())) {
                        pd.forceUpSkill(psa);
                    }
                }
            }

            if ((ps.getData() instanceof SkillShot)) {
                SkillShot ss = (SkillShot) skill;
                ss.cast(liv, skillLevel);
            } else if ((ps.getData() instanceof TargetSkill)) {
                LivingEntity ent = TargetHelper.getLivingTarget(player, skill.getRange(skillLevel));
                if (ent != null) {
                    try {
                        boolean attack = !SkillAPI.getSettings().canAttack(player, ent);
                        TargetSkill ts = (TargetSkill) skill;
                        ts.cast(player, ent, skillLevel, attack);
                    } catch (Exception localException) {
                    }
                }
            }
        } else {
            Skill skill = SkillAPI.getSkill(skillName);
            if ((skill instanceof SkillShot)) {
                SkillShot ss = (SkillShot) skill;
                ss.cast(liv, skillLevel);
            } else if ((skill instanceof TargetSkill)) {
                LivingEntity ent = TargetHelper.getLivingTarget(liv, skill.getRange(skillLevel));
                if (ent != null) {
                    try {
                        boolean attack = !SkillAPI.getSettings().canAttack(liv, ent);
                        TargetSkill ts = (TargetSkill) skill;
                        ts.cast(liv, ent, skillLevel, attack);
                    } catch (Exception localException1) {
                    }
                }
            }
        }
    }

    public static int cooldownCheck(LivingEntity liv, String skillName) {
        if ((liv instanceof Player)) {
            HashMap<String, Integer> map = cooldownMap.getOrDefault(liv, new HashMap<String, Integer>());
            if (map.containsKey(skillName)) {
                return map.get(skillName);
            }
        }
        return 0;
    }

    public static void putSkillCooldown(String skillName, int skillLevel, LivingEntity liv) {
        HashMap<String, Integer> map = cooldownMap.getOrDefault(liv, new HashMap<String, Integer>());
        map.put(skillName, SkillUtil.getSkillCooldown(skillName, skillLevel));
        cooldownMap.put(liv, map);
    }
}

