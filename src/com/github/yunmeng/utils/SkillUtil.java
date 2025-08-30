package com.github.yunmeng.utils;

import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiButton;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiLabel;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiScroll;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiTexture;
import com.github.yunmeng.ILandSkillBar;
import com.github.yunmeng.custom.profess.ILandProfess;
import com.github.yunmeng.custom.profess.ProfessUtil;
import com.github.yunmeng.custom.skills.ISkill;
import com.github.yunmeng.custom.skills.ISkillPart;
import com.github.yunmeng.manager.PlayerSkillData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class SkillUtil {

    public static GermGuiTexture ggt;
    public static GermGuiScroll ggs;
    public static GermGuiButton sliderV;
    public static GermGuiButton levelUpButton;
    public static GermGuiLabel skillPointLabel;
    public static GermGuiLabel skillLabel;


    public static void setGermGuiParts(Player player) {
        GermGuiScreen ggs = GermGuiScreen.getGermGuiScreen("iland_skill");
        ggs.setGuiName("skillaa");
        ggs.addGuiPart(ggt);

        GermGuiScroll gScroll = SkillUtil.ggs;
        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
        // 职业
        String profess = psd.getProfess();
        HashMap<String, Integer> skills = new HashMap<>();
        // 初始化技能们
        if (ProfessUtil.professMap.containsKey(profess)) {
            ILandProfess ilp = ProfessUtil.professMap.get(profess);
            for (String skill : ilp.getSkills()) {
                skills.put(skill, 0);
            }
        }
        HashMap<String, Integer> playerSkills = psd.getSkillData();
        for (String skill : playerSkills.keySet()) {
            skills.put(skill, playerSkills.get(skill));
        }
        for (String line : skills.keySet()) {
            if (ISkillPart.skillParts.containsKey(line) && ISkill.iSkills.containsKey(line)) {
                ISkill is = ISkill.iSkills.get(line);
                ISkillPart isp = ISkillPart.skillParts.get(line);
                boolean study = is.isStudy(player, line);
                GermGuiTexture texturePart = isp.getTexturePart(study);
                GermGuiButton ggb = isp.getButtonPart(line);
                GermGuiTexture selectPart = isp.getSelectPart(line);
                gScroll.addGuiPart(texturePart);
                gScroll.addGuiPart(ggb);
                gScroll.addGuiPart(selectPart);
            }
        }
        gScroll.setSliderV(sliderV);
        ggs.addGuiPart(gScroll);
        // 初始化按键
        for (int ai = 1; ai < 6; ai++) {
            Configuration config = ILandSkillBar.config;
            String temp = "Gui.keyView";
            /*double startX = config.getDouble(temp + ".startX");
            double msgStartX = config.getDouble(temp + ".msgStartX");
            double spacingX = config.getDouble(temp + ".spacingX");
            String _y = config.getString(temp + ".y");
            String msgY = config.getString(temp + ".msgY");

            String x = startX + (ai - 1) * spacingX + "*w";
            String ya = _y + "";
            String ym = msgY + "";
            String xm = msgStartX + (ai - 1) * spacingX + "*w";*/
            String x = config.getString(temp + ".x").replaceAll("<index>", ai + "");
            String msgX = config.getString(temp + ".msgX").replaceAll("<index>", ai + "");
            String y = config.getString(temp + ".y");
            String msgY = config.getString(temp + ".msgY");
            String text;
            switch (ai) {
                case 1:
                    text = "Q";
                    break;
                case 2:
                    text = "R";
                    break;
                case 3:
                    text = "F";
                    break;
                case 4:
                    text = "G";
                    break;
                case 5:
                    text = "Z";
                    break;
                default:
                    text = "Q";
            }
            ggs.addGuiPart(getCustomGermSkillTexture(ai, x, y, player.getName()));
            ggs.addGuiPart(getCustomGermSkillButton(ai, x, y));
            ggs.addGuiPart(getCustomGermSkillLabel(ai, msgX, msgY, text));
        }
        ggs.addGuiPart(levelUpButton);
        ggs.addGuiPart(getSkillPointLabel(psd.getSkillPoint()));
        ggs.addGuiPart(skillLabel);
        ggs.openGui(player);
    }

   /*public static void setGermGuiParts(Player player, int skillNum)

GuiScreen ggs = com.germ.germplugin.api.dynamic.gui.GuiManager.getOpenedGui(player, "skillmm");
ggs != null) {
rmGuiScroll gScroll = new GermGuiScroll("iland_skill_scroll");
gScroll.setWidth("200");
gScroll.setHeight("230");
gScroll.setLocationX("210");
       gScroll.setLocationY("150");
       gScroll.setLocationVX("405");
       gScroll.setLocationVY("150");
       gScroll.setWidthV("20");
       gScroll.setHeightV("180");
       gScroll.setInvalidV(false);
       if (skillNum > 5) {
         gScroll.setScrollableV((skillNum - 5) * 45 + "");
       } else {
         gScroll.setScrollableV("0");
       }
       gScroll.setScrolledV("0");
       PlayerSkillData psd = (PlayerSkillData)PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
       HashMap<String, Integer> skills = psd.getSkillData();
       int i = 0;
       for (String line : skills.keySet()) {
         if (ISkill.iSkills.containsKey(line)) {
           ISkill is = (ISkill)ISkill.iSkills.get(line);
           ItemStack stack = is.getStack(1);
           ItemMeta meta = stack.getItemMeta();
           List<String> loreList = meta.getLore();
           GermGuiSlot ggsb = getGermSkillSlot(i);
           ggsb.setItemStack(stack);
           gScroll.addGuiPart(ggsb);
           com.germ.germplugin.api.GermPacketAPI.sendSlotItemStack(player, "iland_skillslot_" + i, stack);
           gScroll.addGuiPart(getGermSkillTexture(i));

           i++;
         }
       }
       GermGuiButton gButton = new GermGuiButton("sliderV");
       gButton.setDefaultPath("local<->textures/test/滚动.png");
       gButton.setHoverPath("local<->textures/test/滚动.png");
       gButton.setWidth("10");
       gButton.setHeight("35");
       gScroll.setSliderV(gButton);

       ggs.addGuiPart(gScroll);
       ggs.addGuiPart(getGermSkillLabel(player));
     }
   }*/

    /*public static GermGuiSlot getGermSkillSlot(int skillIndex) {
        GermGuiSlot ggs = new GermGuiSlot("iland_skill_scroll_slot_" + skillIndex);
        Configuration config = ILandSkillBar.config;
        String temp = "Gui.skillSlot";

        ggs.setSize(config.getString(temp + ".size"));
        ggs.setInvalid(false);
        ggs.setInteract(false);
        ggs.setIdentity("iland_skillslot_" + skillIndex);
        ggs.setFillPath("local<->textures/gui/slot.png");
        ggs.setEmptyPath("local<->textures/gui/slot.png");
        ggs.setLocationX(temp + ".x");
        double y = (skillIndex - 1) * config.getDouble(temp + ".spacingY");
        ggs.setLocationY(config.getDouble(temp + ".startY") + y + "*h");
        ggs.setLocationZ("3");
        return ggs;
    }*/

   /* public static GermGuiTexture getGermSkillTexture(int skillIndex) {
        GermGuiTexture ggt = new GermGuiTexture("iland_skill_scroll_texture_" + skillIndex);
        Configuration config = ILandSkillBar.config;
        String temp = "Gui.skillTexture";
        ggt.setPath("local<->textures/test/技能列表.png");
        ggt.setLocationX(config.getString(temp + ".x"));
        double y = (skillIndex - 1) * config.getDouble(temp + ".spacingY");
        ggt.setLocationY(config.getDouble(temp + ".startY") + y + "*h");
        ggt.setLocationZ("1");
        ggt.setWidth(config.getString(temp + ".width"));
        ggt.setHeight(config.getString(temp + ".height"));
        return ggt;
    }

    public static GermGuiLabel getGermSkillInfoLabel(int skillIndex, List<String> skillInfo) {
        GermGuiLabel ggl = new GermGuiLabel("iland_skill_scroll_label_" + skillIndex);
        ggl.setLocationX("335");
        int y = (skillIndex - 1) * 45;
        ggl.setLocationY(160 + y + "");
        ggl.setLocationZ("5");
        skillInfo.remove(0);
        skillInfo.remove(0);
        ggl.setTexts(skillInfo);
        return ggl;
    }*/

    public static GermGuiTexture getCustomGermSkillTexture(int skillIndex, String x, String y, String pName) {
        GermGuiTexture ggt = new GermGuiTexture("iland_skill_custom_texture_" + skillIndex);
        HashMap<Integer, String> binds = SkillCaster.playerSkillBinds.getOrDefault(pName, new HashMap<>());
        if (binds.containsKey(skillIndex)) {
            String skill = binds.get(skillIndex);
            if (ISkillPart.skillParts.containsKey(skill)) {
                ISkillPart isp = ISkillPart.skillParts.get(skill);
                ggt.setPath(isp.getTexture());
            } else {
                ggt.setPath("local<->textures/test/nn2.png");
            }
        } else {
            ggt.setPath("local<->textures/test/nn2.png");
        }
        ggt.setLocationX(x);
        ggt.setLocationY(y);
        ggt.setLocationZ("3");
        ggt.setWidth("30");
        ggt.setHeight("30");
        return ggt;
    }

    public static GermGuiLabel getCustomGermSkillLabel(int skillIndex, String x, String y, String text) {
        GermGuiLabel ggl = new GermGuiLabel("iland_skill_custom_label_" + skillIndex);
        ggl.setLocationX(x);
        ggl.setLocationY(y);
        ggl.setLocationZ("20");
        ggl.setScale("2");
        List<String> info = new ArrayList<>();
        info.add(text);
        ggl.setTexts(info);
        return ggl;
    }

    public static GermGuiLabel getSkillPointLabel(int leftPoint) {
        GermGuiLabel ggl = skillPointLabel;
        ggl.setText("§e剩余技能点: §b" + leftPoint);
        return ggl;
    }

    public static GermGuiButton getCustomGermSkillButton(int skillIndex, String x, String y) {
        GermGuiButton ggt = new GermGuiButton("iland_skill_custom_button_" + skillIndex);
        ggt.setDefaultPath("local<->textures/misc/air.png");
        ggt.setHoverPath("local<->textures/misc/air.png");
        ggt.setClickSound("minecraft:ui.button.click");
        ggt.setLocationX(x);
        ggt.setLocationY(y);
        ggt.setLocationZ("7");
        ggt.setWidth("30");
        ggt.setHeight("30");
        return ggt;
    }

    public static int getSkillCooldown(String skillName, int skillLevel) {
        ISkill is = ISkill.iSkills.get(skillName);
        if (is != null) {
            return (int) (skillLevel * is.getCooldownAdd() + is.getCooldown());
        }
        return 0;
    }

    public static int getSkillMana(String skillName, int skillLevel) {
        ISkill is = ISkill.iSkills.get(skillName);
        if (is != null) {
            return (int) (is.getMana() + is.getManaAdd() * skillLevel);
        }
        return 0;
    }

    /*public static int getPlayerSkillPoint(Player player) {
        String name = player.getName();
        if (PlayerSkillData.playerDatas.containsKey(name)) {
            PlayerSkillData psd = PlayerSkillData.playerDatas.get(name);
            return psd.getSkillPoint();
        }
        return 0;
    }*/

    public static boolean takePlayerSkillPoint(Player player, int point) {
        String name = player.getName();
        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(name, new PlayerSkillData());
        if (psd.getSkillPoint() >= point) {
            psd.setSkillPoint(psd.getSkillPoint() - point);
            PlayerSkillData.playerDatas.put(name, psd);
            return true;
        }
        return false;
    }
}

