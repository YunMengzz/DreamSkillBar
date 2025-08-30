package com.github.yunmeng.custom.skills.events;

import com.germ.germplugin.api.dynamic.DynamicBase;
import com.germ.germplugin.api.dynamic.gui.GermGuiScreen;
import com.germ.germplugin.api.dynamic.gui.GuiManager;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiButton;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiLabel;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiPart;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiScroll;
import com.germ.germplugin.api.dynamic.gui.part.GermGuiTexture;
import com.germ.germplugin.api.event.gui.GermGuiButtonEvent;
import com.github.yunmeng.custom.skills.ISkill;
import com.github.yunmeng.custom.skills.ISkillPart;
import com.github.yunmeng.manager.PlayerSkillData;
import com.github.yunmeng.utils.SkillCaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GermButtonClickEvent implements org.bukkit.event.Listener {
    @org.bukkit.event.EventHandler
    public void onClick(GermGuiButtonEvent event) {
        if (event.getEventType() == com.germ.germplugin.api.dynamic.gui.part.GermGuiButton.EventType.LEFT_CLICK) {
            GermGuiButton ggb = event.getGermGuiButton();
            String indexName = ggb.getIndexName();
            GermGuiScroll gScroll;
            if (indexName.contains("iland_skill_scroll_button_")) {
                Player player = event.getPlayer();
                String skill = indexName.replace("iland_skill_scroll_button_", "");
                PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
                int level = 0;
                HashMap<String, Integer> skillStudys = psd.getSkillData();
                if (skillStudys.containsKey(skill)) {
                    level = skillStudys.get(skill);
                }
                List<String> info = new ArrayList<>();
                if (ISkill.iSkills.containsKey(skill)) {
                    ISkill is = ISkill.iSkills.get(skill);
                    ItemStack stack = is.getStack(level);
                    info = stack.getItemMeta().getLore();
                } else {
                    info.add("§a请点击一个技能查看具体信息！");
                }
                GermGuiScreen ggs = event.getGermGuiScreen();
                GermGuiPart ggp = ggs.getGuiPart("iland_skill_scroll_label_skillinfo");
                if ((ggp instanceof GermGuiLabel)) {
                    GermGuiLabel ggl = (GermGuiLabel) ggp;
                    ggl.setTexts(info);
                }
                GermGuiPart gScrollb = ggs.getGuiPart("iland_skill_scroll");
                if ((gScrollb instanceof GermGuiScroll)) {
                    gScroll = (GermGuiScroll) gScrollb;
                    List<GermGuiPart<? extends DynamicBase>> parts = gScroll.getGuiParts();
                    for (GermGuiPart ggpm : parts) {
                        String index = ggpm.getIndexName();
                        if (index.contains("select")) {
                            GermGuiPart ggma = gScroll.getGuiPart(index);
                            if ((ggma instanceof GermGuiTexture)) {
                                GermGuiTexture selectTex = (GermGuiTexture) ggma;
                                if (index.equals(indexName + "_select")) {
                                    selectTex.setPath("local<->textures/test/slot_in.png");
                                } else
                                    selectTex.setPath("local<->textures/misc/air.png");
                            }
                        }
                    }
                }
            } else {
                ISkillPart isp;
                if (indexName.contains("iland_skill_custom_button_")) {
                    Player player = event.getPlayer();
                    GermGuiScreen ggs = event.getGermGuiScreen();
                    int ai = Integer.parseInt(indexName.replace("iland_skill_custom_button_", ""));
                    HashMap binds = SkillCaster.playerSkillBinds.getOrDefault(player.getName(), new HashMap());
                    GermGuiTexture skillTexture;
                    if (binds.containsKey(ai)) {
                        binds.remove(ai);
                        GermGuiPart ggp = ggs.getGuiPart("iland_skill_custom_texture_" + ai);
                        if ((ggp instanceof GermGuiTexture)) {
                            GermGuiTexture ggt = (GermGuiTexture) ggp;
                            ggt.setPath("local<->textures/test/nn2.png");
                            GermGuiScreen skillHud = GuiManager.getOpenedGui(player, "skillbar");
                            GermGuiPart skillPart = skillHud.getGuiPart("iland_skillbar_texture_" + ai);
                            if ((skillPart instanceof GermGuiTexture)) {
                                skillTexture = (GermGuiTexture) skillPart;
                                skillTexture.setPath("local<->textures/test/nn2.png");
                            }
                        }
                        SkillCaster.playerSkillBinds.put(player.getName(), binds);
                    } else {
                        String skill = null;
                        GermGuiPart gScrollb = ggs.getGuiPart("iland_skill_scroll");

                        if ((gScrollb instanceof GermGuiScroll)) {
                            gScroll = (GermGuiScroll) gScrollb;
                            List<GermGuiPart<? extends DynamicBase>> parts = gScroll.getGuiParts();
                            for (GermGuiPart ggpm : parts) {
                                String index = ggpm.getIndexName();
                                if (index.contains("select")) {
                                    GermGuiPart ggma = gScroll.getGuiPart(index);
                                    if ((ggma instanceof GermGuiTexture)) {
                                        GermGuiTexture selectTex = (GermGuiTexture) ggma;
                                        if (selectTex.getPath().equals("local<->textures/test/slot_in.png")) {
                                            skill = index.replace("iland_skill_scroll_button_", "").replace("_select", "");
                                        }
                                    }
                                }
                            }
                        }
                        if (skill == null) return;
                        if (ISkill.iSkills.containsKey(skill)) {
                            ISkill is = ISkill.iSkills.get(skill);
                            if (!is.containsSkill(player, skill)) return;
                            binds.put(ai, skill);
                        }
                        if (ISkillPart.skillParts.containsKey(skill)) {
                            isp = ISkillPart.skillParts.get(skill);
                            GermGuiPart ggp = ggs.getGuiPart("iland_skill_custom_texture_" + ai);
                            if ((ggp instanceof GermGuiTexture)) {
                                GermGuiTexture ggt = (GermGuiTexture) ggp;
                                ggt.setPath(isp.getTexture());
                            }
                            GermGuiScreen skillHud = GuiManager.getOpenedGui(player, "skillbar");
                            GermGuiPart skillPart = skillHud.getGuiPart("iland_skillbar_texture_" + ai);
                            if ((skillPart instanceof GermGuiTexture)) {
                                skillTexture = (GermGuiTexture) skillPart;
                                skillTexture.setPath(isp.getTexture());
                            }
                        }
                        SkillCaster.playerSkillBinds.put(player.getName(), binds);
                    }
                } else if (indexName.equals("iland_skill_levelup_button")) {
                    Player player = event.getPlayer();
                    GermGuiScreen ggs = event.getGermGuiScreen();
                    String skill = null;
                    GermGuiPart gScrollb = ggs.getGuiPart("iland_skill_scroll");
                    if ((gScrollb instanceof GermGuiScroll)) {
                        gScroll = (GermGuiScroll) gScrollb;
                        List<GermGuiPart<? extends DynamicBase>> parts = gScroll.getGuiParts();
                        for (GermGuiPart ggpm : parts) {
                            String index = ggpm.getIndexName();
                            if (index.contains("select")) {
                                GermGuiPart ggma = gScroll.getGuiPart(index);
                                if ((ggma instanceof GermGuiTexture)) {
                                    GermGuiTexture selectTex = (GermGuiTexture) ggma;
                                    if (selectTex.getPath().equals("local<->textures/test/slot_in.png")) {
                                        skill = index.replace("iland_skill_scroll_button_", "").replace("_select", "");
                                    }
                                }
                            }
                        }
                    }
                    if (skill == null) return;
                    if (ISkill.iSkills.containsKey(skill)) {
                        ISkill is = ISkill.iSkills.get(skill);
                        is.study(player, skill);
                        boolean study = is.isStudy(player, skill);
                        if ((!study) && (ISkillPart.skillParts.containsKey(skill))) {
                            isp = ISkillPart.skillParts.get(skill);
                            GermGuiPart ggp = ggs.getGuiPart("iland_skill_scroll$iland_skill_scroll_texture_" + isp.getIndex());
                            if ((ggp instanceof GermGuiTexture)) {
                                GermGuiTexture ggt = (GermGuiTexture) ggp;
                                ggt.setPath(isp.getLockTexture());
                            }
                        }
                        PlayerSkillData psd = PlayerSkillData.playerDatas.get(player.getName());
                        int level = psd.getSkillLevel(skill);

                        ItemStack stack = is.getStack(level);
                        List<String> info = stack.getItemMeta().getLore();
                        GermGuiPart ggp = ggs.getGuiPart("iland_skill_scroll_label_skillinfo");
                        if ((ggp instanceof GermGuiLabel)) {
                            GermGuiLabel ggl = (GermGuiLabel) ggp;
                            ggl.setTexts(info);
                        }
                        GermGuiPart ggpm = ggs.getGuiPart("iland_skill_levelup_label");
                        if ((ggpm instanceof GermGuiLabel)) {
                            GermGuiLabel ggl = (GermGuiLabel) ggpm;
                            ggl.setText("§e剩余技能点: §b" + psd.getSkillPoint());
                        }
                    }
                }
            }
        }
    }
}


