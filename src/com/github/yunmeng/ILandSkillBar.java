package com.github.yunmeng;

import com.github.yunmeng.config.ILandConfig;
import com.github.yunmeng.custom.profess.ProfessGUI;
import com.github.yunmeng.custom.profess.ProfessUtil;
import com.github.yunmeng.custom.skills.SkillGUI;
import com.github.yunmeng.custom.skills.events.GermButtonClickEvent;
import com.github.yunmeng.database.ILandDataBase;
import com.github.yunmeng.database.manager.DatabasePlayerProfess;
import com.github.yunmeng.listener.ILandPlayerListener;
import com.github.yunmeng.listener.InventoryClickListener;
import com.github.yunmeng.listener.InventoryCloseListener;
import com.github.yunmeng.manager.PlayerSkillData;
import com.github.yunmeng.runnable.CooldownRunnable;
import com.github.yunmeng.runnable.SkillComboRunnable;
import com.github.yunmeng.utils.ILandPAPIHooker;
import com.github.yunmeng.utils.SkillCaster;
import com.github.yunmeng.utils.SkillUtil;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


public final class ILandSkillBar
        extends JavaPlugin {
    public static String root;
    public static Configuration config;
    public static Plugin plugin;
    public static HashSet<Player> safeLock = new HashSet<>();


    public static HashMap<OfflinePlayer, Integer> safeTime = new HashMap<>();


    public static ILandDataBase ildb;


    public void onEnable() {
        root = getDataFolder().getAbsolutePath();
        config = getConfig();
        plugin = Bukkit.getPluginManager().getPlugin("DreamSkillBar");
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            public void run() {
                Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
                if (papi != null) {
                    boolean isLoadPAPI = new ILandPAPIHooker().register();
                    if (isLoadPAPI) {
                        System.out.println("[DreamSkillBar]Placeholder API Loaded!");
                    } else
                        System.out.println("[DreamSkillBar]Placeholder API Unloaded!");
                }
            }
        }, 10L);


        saveDefaultConfig();
        ILandConfig.loadConfig();
        Bukkit.getScheduler().runTaskTimer(this, new CooldownRunnable(), 20, 20);
        Bukkit.getScheduler().runTaskTimer(this, new SkillComboRunnable(), 1, 1);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);
        Bukkit.getPluginManager().registerEvents(new ILandPlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new GermButtonClickEvent(), this);
        this.getLogger().info("加载成功！");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("iland")) {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                if (args.length >= 1) {
                    int num;
                    if (args[0].equals("skillcast")) {
                        num = Integer.parseInt(args[1]);
                        String skill = null;
                        switch (num) {
                            case 1:
                                skill = SkillCaster.getNumSkill(player, 1);
                                break;
                            case 2:
                                skill = SkillCaster.getNumSkill(player, 2);
                                break;
                            case 3:
                                skill = SkillCaster.getNumSkill(player, 3);
                                break;
                            case 4:
                                skill = SkillCaster.getNumSkill(player, 4);
                                break;
                            case 5:
                                skill = SkillCaster.getNumSkill(player, 5);
                        }

                        if (skill != null) {
                            PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
                            int level = psd.getSkillLevel(skill);
                            if (level > 0) {
                                SkillCaster.defCast(skill, level, player);
                            }
                        }
                    } else if (args[0].equals("cast")) {
                        SkillCaster.foreCastSkill(args[1], Integer.parseInt(args[2]), player);
                    } else if (args[0].equals("defcast")) {
                        SkillCaster.defCast(args[1], Integer.parseInt(args[2]), player);
                    } else if (args[0].equals("professreset")) {
                        PlayerSkillData.playerDatas.remove(player.getName());
                        SkillCaster.playerSkillBinds.remove(player.getName());
                    } else if (args[0].equals("openb")) {
                        SkillUtil.setGermGuiParts(player);
                    } else if (args[0].equals("areacast")) {
                        for (Entity ent : player.getNearbyEntities(5.0D, 5.0D, 5.0D)) {
                            if ((ent instanceof LivingEntity)) {
                                SkillCaster.foreCastSkill(args[1], Integer.parseInt(args[2]), (LivingEntity) ent);
                            }
                        }
                    } else if (args[0].equals("skill")) {
                        SkillGUI sg = new SkillGUI();
                        sg.setItems(player);
                        sg.openInv(player);
                    } else if (args[0].equals("addpoint")) {
                        if (player.isOp()) {
                            PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(args[1], null);
                            if (psd != null) {
                                psd.addSkillPoint(Integer.parseInt(args[2]));
                            }
                        }
                    } else if (args[0].equals("profess")) {
                        ProfessGUI pg = new ProfessGUI();
                        pg.setItems();
                        pg.openGUI(player);
                    } else if (args[0].equals("reload")) {
                        ILandConfig.loadConfig();
                        player.sendMessage("[DreamSkillBar]重载配置文件成功！");
                    } else if (args[0].equals("dataset")) {
                        DatabasePlayerProfess dpp = new DatabasePlayerProfess(player.getName(), "SB", 200);
                        ildb.updatePlayerProfessData(dpp);
                        player.sendMessage("update ok");
                    } else if (args[0].equals("print")) {
                        DatabasePlayerProfess dpp = ildb.getPlayerProfessData(player.getName());
                        player.sendMessage(dpp.getProfess());
                        player.sendMessage(dpp.getSkillPoint() + "");
                    } else if (args[0].equals("set")) {
                        DatabasePlayerProfess dpp = new DatabasePlayerProfess(player.getName(), "SB", 200);
                        ildb.insertData(dpp);
                        player.sendMessage("send ok!");
                    }
                    else if (args[0].equals("giveprofess")) {
                        try{
                            String name = ChatColor.translateAlternateColorCodes('&', args[1]);
                            if (ProfessUtil.professMap.containsKey(name)) {
                                PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(args[2], new PlayerSkillData());
                                psd.changeProfess(name, player.getLevel());
                                PlayerSkillData.playerDatas.put(args[2], psd);
                                sender.sendMessage("更改成功!");
                                return true;
                            }
                            sender.sendMessage("未找到名为" + name + "的职业");
                        } catch (ArrayIndexOutOfBoundsException e) {
                            sender.sendMessage("/iland giveprofess [职业] [玩家]");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (args[0].equalsIgnoreCase("deleteprofess")){
                        String name = ILandConfig.langMap.get("DefaultProfess");
                        PlayerSkillData psd = args.length >= 2 ? PlayerSkillData.playerDatas.getOrDefault(args[1], new PlayerSkillData()) : PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
                        psd.changeProfess(name, player.getLevel());
                        PlayerSkillData.playerDatas.put(player.getDisplayName(), psd);
                        sender.sendMessage("重置为初始职业成功！");
                    } else if (args[0].equals("regive")) {
                        String name = args[1];
                        if (ProfessUtil.professMap.containsKey(name)) {
                            PlayerSkillData.playerDatas.remove(player.getName());
                            SkillCaster.playerSkillBinds.remove(player.getName());
                            PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(args[2], new PlayerSkillData());
                            psd.changeProfess(name, player.getLevel());
                            PlayerSkillData.playerDatas.put(player.getDisplayName(), psd);
                        }
                    } else if (args[0].equalsIgnoreCase("getProfess")) {
                        PlayerSkillData psd = PlayerSkillData.playerDatas.get(player.getName());
                        if (psd == null) {
                            player.sendMessage(ILandConfig.langMap.get("DefaultProfess"));
                        } else {
                            player.sendMessage(psd.getProfess());
                        }
                    }
                }
            } else if (args.length >= 1) {
                if (args[0].equals("giveprofess")) {
                    String name = args[1];
                    if (ProfessUtil.professMap.containsKey(name)) {
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player != null) {
                            PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(args[2], new PlayerSkillData());
                            psd.changeProfess(name, player.getLevel());
                            PlayerSkillData.playerDatas.put(args[2], psd);
                            sender.sendMessage("更改成功!");
                        }
                        sender.sendMessage("请输入玩家  /iland giveprofess [职业] [玩家]");
                    }
                } else if (args[0].equalsIgnoreCase("deleteprofess")){
                    String name = ILandConfig.langMap.get("DefaultProfess");
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null) {
                        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(args[1], new PlayerSkillData());
                        psd.changeProfess(name, player.getLevel());
                        PlayerSkillData.playerDatas.put(args[1], psd);
                        sender.sendMessage("重置为初始职业成功！");
                    }
                    sender.sendMessage("请输入玩家  iland deleteprofess [玩家]");
                } else if (args[0].equals("regive")) {
                    String name = args[1];
                    if (ProfessUtil.professMap.containsKey(name)) {
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player != null) {
                            PlayerSkillData.playerDatas.remove(player.getName());
                            SkillCaster.playerSkillBinds.remove(player.getName());
                            PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(args[2], new PlayerSkillData());
                            psd.changeProfess(name, player.getLevel());
                            PlayerSkillData.playerDatas.put(args[2], psd);
                        }
                    }
                } else if ((args[0].equals("addpoint")) &&
                        (sender.isOp())) {
                    PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(args[1], null);
                    if (psd != null) {
                        psd.addSkillPoint(Integer.parseInt(args[2]));
                    }
                }
            }
        }


        return true;
    }

    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers())
            if (safeLock.contains(player)) {
                safeLock.remove(player);
            } else {
                String name = player.getName();
                PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(name, new PlayerSkillData());
                DatabasePlayerProfess dpp = new DatabasePlayerProfess(name, psd.getProfess(), psd.getSkillPoint());
                HashMap<String, Integer> skills = psd.getSkillData();
                HashMap<Integer, String> binds = SkillCaster.playerSkillBinds.getOrDefault(name, new HashMap<>());
                ildb.deleteAllBind(name);
                for (int i = 1; i <= 5; i++) {
                    if (binds.containsKey(i)) {
                        ildb.insertBind(name, i, binds.get(i));
                    }
                }
                ildb.deleteAllSkill(name);
                for (String skill : skills.keySet()) {
                    ildb.insertSkill(name, skill, skills.get(skill));
                }
                ildb.deleteData(name);
                ildb.insertData(dpp);
            }
        ildb.closeQuietly();
    }
}