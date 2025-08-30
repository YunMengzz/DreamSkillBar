package com.github.yunmeng.listener;

import com.github.yunmeng.utils.SkillCaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        String title = inv.getTitle();
        if (title.equals(com.github.yunmeng.config.ILandConfig.langMap.get("SkillTitle"))) {
            HumanEntity he = event.getPlayer();
            if ((he instanceof Player)) {
                Player player = (Player) he;
                List<Integer> slots = new ArrayList<>();
                slots.add(31);
                slots.add(32);
                slots.add(33);
                slots.add(34);
                slots.add(35);
                String name = player.getName();
                HashMap<Integer, String> map = SkillCaster.playerSkillBinds.getOrDefault(name, new HashMap<>());
                int a = 1;
                for (Iterator<Integer> localIterator = slots.iterator(); localIterator.hasNext(); ) {
                    int i = localIterator.next();
                    ItemStack stack = inv.getItem(i);
                    if ((stack != null) && (stack.hasItemMeta())) {
                        ItemMeta meta = stack.getItemMeta();
                        if (meta.hasDisplayName()) {
                            map.put(a, meta.getDisplayName());
                        }
                    } else {
                        map.remove(a);
                    }
                    a++;
                }
                SkillCaster.playerSkillBinds.put(name, map);
            }
        }
    }
}