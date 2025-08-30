package com.github.yunmeng.custom.skills;

import com.germ.germplugin.api.dynamic.gui.part.GermGuiButton;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiTexture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;


public class ISkillPart {
    public static HashMap<String, ISkillPart> skillParts = new HashMap();

    private String x;
    private String y;
    private String width;
    private String height;
    private int index;
    private String texture = "local<->textures/misc/air.png";
    private String lockTexture = "local<->textures/misc/air.png";

    public ISkillPart(String x, String y, int index, String texture, String lockTexture, String width, String height) {
        System.out.println(x);
        System.out.println(y);
        System.out.println(width);
        System.out.println(height);
        System.out.println(index);
        System.out.println(texture);
        System.out.println(lockTexture);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.index = index;
        this.texture = texture;
        this.lockTexture = lockTexture;
    }

    public ISkillPart(String x, String y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public String getTexture() {
        return this.texture;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getX() {
        return this.x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getY() {
        return this.y;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLockTexture() {
        return this.lockTexture;
    }

    /**
     * 获取texturePart方法
     * @param study
     * @return
     */
    public GermGuiTexture getTexturePart(boolean study) {
        GermGuiTexture ggt = new GermGuiTexture("iland_skill_scroll_texture_" + this.index);
        if (study) {
            ggt.setPath(this.texture);
        } else {
            ggt.setPath(this.lockTexture);
        }
        ggt.setLocationX(this.x);
        ggt.setLocationY(this.y);
        ggt.setLocationZ("1");
        ggt.setWidth(width);
        ggt.setHeight(height);
        return ggt;
    }

    /**
     * 选择part  Texture  图片
     * @param skillName
     * @return
     */
    public GermGuiTexture getSelectPart(String skillName) {
        GermGuiTexture ggt = new GermGuiTexture("iland_skill_scroll_button_" + skillName + "_select");
        ggt.setPath("local<->textures/misc/air.png");
        ggt.setLocationX(this.x);
        ggt.setLocationY(this.y);
        ggt.setLocationZ("6");
        ggt.setWidth(width);
        ggt.setHeight(height);
        return ggt;
    }

    /**
     * 获取buttonPart方法
     * @param skillName
     * @return
     */
    public GermGuiButton getButtonPart(String skillName) {
        GermGuiButton ggt = new GermGuiButton("iland_skill_scroll_button_" + skillName);
        ggt.setDefaultPath("local<->textures/misc/air.png");
        ggt.setHoverPath("local<->textures/misc/air.png");
        ggt.setClickSound("minecraft:ui.button.click");
        ggt.setLocationX(this.x);
        ggt.setLocationY(this.y);
        ggt.setLocationZ("2");
        ggt.setWidth(width);
        ggt.setHeight(height);
        return ggt;
    }

    public List<String> getLabelInfo(String skillName, int level) {
        if (ISkill.iSkills.containsKey(skillName)) {
            ISkill is = ISkill.iSkills.get(skillName);
            ItemStack stack = is.getStack(level);
            return stack.getItemMeta().getLore();
        }
        List<String> info = new ArrayList<>();
        info.add("§a请点击一个技能查看具体信息！");
        return info;
    }
}


