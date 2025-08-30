package com.github.yunmeng.listener;

import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import com.germ.germplugin.api.dynamic.gui.GuiManager;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiPart;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiTexture;
import com.github.yunmeng.ILandSkillBar;
import com.github.yunmeng.custom.skills.ISkillPart;
import com.github.yunmeng.custom.skills.SkillGUI;
import com.github.yunmeng.database.manager.DatabasePlayerProfess;
import com.github.yunmeng.manager.PlayerSkillData;
import com.github.yunmeng.utils.SkillCaster;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ILandPlayerListener implements org.bukkit.event.Listener {
    private static java.util.List<String> list;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ILandSkillBar.safeTime.containsKey(player)) {
            return;
        }
        ILandSkillBar.safeTime.put(player, 2);
        final String name = player.getName();
        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(name, new PlayerSkillData());
        final DatabasePlayerProfess dpp = new DatabasePlayerProfess(name, psd.getProfess(), psd.getSkillPoint());
        final HashMap<String, Integer> skills = psd.getSkillData();
        final HashMap<Integer, String> binds = SkillCaster.playerSkillBinds.getOrDefault(name, new HashMap<>());
        Bukkit.getScheduler().runTaskAsynchronously(ILandSkillBar.plugin, (Runnable) () -> {
            ILandSkillBar.ildb.deleteAllBind(name);
            for (int i = 1; i <= 5; i++) {
                if (binds.containsKey(i)) {
                    ILandSkillBar.ildb.insertBind(name, i, binds.get(i));
                }
            }
            ILandSkillBar.ildb.deleteAllSkill(name);
            for (String skill : skills.keySet()) {
                ILandSkillBar.ildb.insertSkill(name, skill, skills.get(skill));
            }
            ILandSkillBar.ildb.deleteData(name);
            ILandSkillBar.ildb.insertData(dpp);
        });
        PlayerSkillData.playerDatas.remove(name);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        ILandSkillBar.safeLock.add(player);
        final String name = player.getName();
        Bukkit.getScheduler().runTaskLaterAsynchronously(ILandSkillBar.plugin, new Runnable() {
            public void run() {
                DatabasePlayerProfess dpp = ILandSkillBar.ildb.getPlayerProfessData(name);
                if (dpp != null) {
                    PlayerSkillData psd = new PlayerSkillData(dpp);
                    HashMap<String, Integer> skills = ILandSkillBar.ildb.getSkills(name);
                    HashMap<Integer, String> binds = ILandSkillBar.ildb.getBinds(name);
                    psd.setSkillData(skills);
                    SkillCaster.playerSkillBinds.put(name, binds);
                    PlayerSkillData.playerDatas.put(name, psd);
                    GermGuiScreen ggs = GuiManager.getOpenedGui(player, "skillbar");
                    for (int i = 1; i < 6; i++) {
                        String skill = (String) binds.get(i);
                        if (ggs != null) {
                            GermGuiPart ggp = ggs.getGuiPart("iland_skillbar_texture_" + i);
                            if (ggp instanceof GermGuiTexture) {
                                GermGuiTexture ggt = (GermGuiTexture) ggp;
                                if (skill != null) {
                                    if (ISkillPart.skillParts.containsKey(skill)) {
                                        ISkillPart isp = (ISkillPart) ISkillPart.skillParts.get(skill);
                                        ggt.setPath(isp.getTexture());
                                    }
                                } else {
                                    ggt.setPath("local<->textures/test/nn2.png");
                                }
                            }
                        }
                    }
                } else {
                    PlayerSkillData.playerDatas.put(name, new PlayerSkillData());
                }
                ILandSkillBar.safeLock.remove(player);
            }
        }, 40L);
    }


    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        Entity ent = event.getRightClicked();
        if ((ent instanceof LivingEntity)) {
            String name = ent.getCustomName();
            final Player player = event.getPlayer();
            PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
            if ((name != null) && (name.equals(psd.getProfess()))) {
                final SkillGUI sg = new SkillGUI();
                sg.setItems(player);
                if (name.equals("龙骑兵")) {
                    Bukkit.getScheduler().runTaskLater(ILandSkillBar.plugin, () -> sg.openInv(player), 2L);
                } else {
                    sg.openInv(player);
                }
            }
        }
    }

    @EventHandler
    public void onLevelUp(PlayerLevelChangeEvent event) {
        String name = event.getPlayer().getName();
        int i = event.getNewLevel();
        int a = event.getOldLevel();
        if (i > a) {
            PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(name, new PlayerSkillData());
            psd.addSkillPoint((i - a) * 2);
            PlayerSkillData.playerDatas.put(name, psd);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack stack = event.getItemDrop().getItemStack();
        if ((stack != null) && (stack.hasItemMeta())) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasLore()) {
                for (String lore : meta.getLore()) {
                    if (lore.contains("§6已绑定")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§c已绑定物品无法丢弃！");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        ItemStack stack = event.getItem().getItemStack();
        if ((stack != null) && (stack.hasItemMeta())) {
            ItemMeta meta = stack.getItemMeta();
            if (meta.hasLore()) {
                for (String lore : meta.getLore()) {
                    if (lore.contains("§6已绑定")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§c已绑定物品无法拾取！");
                    }
                }
            }
        }
    }
}