package com.github.yunmeng.custom.skills;

import com.github.yunmeng.config.ILandConfig;
import com.github.yunmeng.custom.profess.ILandProfess;
import com.github.yunmeng.custom.profess.ProfessUtil;
import com.github.yunmeng.manager.PlayerSkillData;
import com.github.yunmeng.utils.SkillCaster;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SkillGUI {
    private Inventory inv = Bukkit.createInventory(null, 36, ILandConfig.langMap.get("SkillTitle"));

    public static void resetSkillItem(Inventory inv, String skillName, ISkill skill, int level) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.getItem(i);
            if ((stack != null) && (stack.hasItemMeta())) {
                ItemMeta meta = stack.getItemMeta();
                if ((meta.hasDisplayName()) &&
                        (meta.getDisplayName().equals(skillName))) {
                    inv.setItem(i, skill.getStack(level));
                }
            }
        }
    }

    public void setItems(Player player) {
        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
        ILandProfess ilp = ProfessUtil.professMap.get(psd.getProfess());
        List<String> skills = ilp.getSkills();
        int i = 0;
        for (String name : skills) {
            ISkill is = ISkill.iSkills.get(name);
            int level = psd.getSkillLevel(name);
            this.inv.setItem(i, is.getStack(level));
            i++;
        }

        List<Integer> slots = new ArrayList<>();
        slots.add(31);
        slots.add(32);
        slots.add(33);
        slots.add(34);
        slots.add(35);
        for (int a = 1; a < 6; a++) {
            String skill = SkillCaster.getNumSkill(player, a);
            if (skill != null) {
                ISkill is = ISkill.iSkills.get(skill);
                int level = psd.getSkillLevel(skill);
                this.inv.setItem(slots.get(a - 1), is.getStack(level));
            }
        }
    }

    public void openInv(Player player) {
        player.openInventory(this.inv);
    }
}
