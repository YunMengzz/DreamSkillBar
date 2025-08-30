package com.github.yunmeng.listener;

import com.github.yunmeng.config.ILandConfig;
import com.github.yunmeng.custom.profess.ProfessUtil;
import com.github.yunmeng.custom.skills.ISkill;
import com.github.yunmeng.custom.skills.SkillGUI;
import com.github.yunmeng.manager.PlayerSkillData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class InventoryClickListener
        implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        String title = inv.getTitle();
        if (title.equals(ILandConfig.langMap.get("SkillTitle"))) {
            HumanEntity he = event.getWhoClicked();
            if ((he instanceof Player)) {
                Player player = (Player) he;
                int slot = event.getRawSlot();
                event.setCancelled(true);
                ClickType ct = event.getClick();
                if (ct == ClickType.RIGHT) {
                    ArrayList<Integer> slots = new ArrayList<>();
                    slots.add(31);
                    slots.add(32);
                    slots.add(33);
                    slots.add(34);
                    slots.add(35);
                    if (slots.contains(slot)) {
                        inv.setItem(slot, null);
                        return;
                    }
                    ItemStack stack = event.getCurrentItem();
                    if ((stack != null) && (stack.hasItemMeta())) {
                        for (int i = 0; i < 5; i++) {
                            int s = (Integer) slots.get(i);
                            ItemStack old = inv.getItem(s);
                            if ((old == null) || (old.getType() == Material.AIR)) {
                                inv.setItem(s, stack);
                                break;
                            }
                        }
                    }
                } else if (ct == ClickType.LEFT) {
                    ItemStack stack = event.getCurrentItem();
                    if ((stack != null) && (stack.hasItemMeta())) {
                        ItemMeta meta = stack.getItemMeta();
                        String name = meta.getDisplayName();
                        if (ISkill.iSkills.containsKey(name)) {
                            ISkill is = ISkill.iSkills.get(name);
                            is.study(player, name);
                            PlayerSkillData psd = PlayerSkillData.playerDatas.get(player.getName());
                            int level = psd.getSkillLevel(name);
                            SkillGUI.resetSkillItem(inv, name, is, level);
                        }
                    }
                }
            }
        } else if (title.equals(ILandConfig.langMap.get("Profess"))) {
            // 转职
            HumanEntity he = event.getWhoClicked();
            if ((he instanceof Player)) {
                Player player = (Player) he;
                event.setCancelled(true);
                ItemStack stack = event.getCurrentItem();
                if ((stack != null) && (stack.hasItemMeta())) {
                    ItemMeta meta = stack.getItemMeta();
                    String name = meta.getDisplayName();
                    if (ProfessUtil.professMap.containsKey(name)) {
                        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
                        psd.changeProfess(name, player.getLevel());
                        PlayerSkillData.playerDatas.put(player.getName(), psd);
                        player.closeInventory();
                    }
                }
            }
        }
    }
}