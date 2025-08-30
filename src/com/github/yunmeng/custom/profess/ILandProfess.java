package com.github.yunmeng.custom.profess;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ILandProfess {
    private String name;
    private String id;
    private List<String> loreList;
    private List<String> skills;

    public ILandProfess(String name, String id, List<String> loreList, List<String> skills) {
        this.name = name;
        this.id = id;
        this.loreList = loreList;
        this.skills = skills;
    }

    public ItemStack getStack() {
        String[] ids = this.id.split(":");
        ItemStack stack = new ItemStack(org.bukkit.Material.STONE);
        stack.setTypeId(Integer.parseInt(ids[0]));
        stack.setDurability(Short.parseShort(ids[1]));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(this.name);
        meta.setLore(this.loreList);
        stack.setItemMeta(meta);
        return stack.clone();
    }

    public List<String> getSkills() {
        return this.skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}