package com.github.yunmeng.custom.profess;

import com.github.yunmeng.config.ILandConfig;
import com.github.yunmeng.manager.PlayerSkillData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class ProfessGUI {
    private Inventory inv = Bukkit.createInventory(null, 27, (String) ILandConfig.langMap.get("Profess"));

    public ProfessGUI() {
        setItems();
    }

    public void setItems() {
        int i = 0;
        for (String prf : ProfessUtil.professMap.keySet())
            if (!prf.equals(ILandConfig.langMap.get("Profess"))) {
                ILandProfess ilp = ProfessUtil.professMap.get(prf);
                this.inv.setItem(i, ilp.getStack());
                i++;
            }
    }

    public boolean openGUI(Player player) {
        PlayerSkillData psd = PlayerSkillData.playerDatas.getOrDefault(player.getName(), new PlayerSkillData());
        if (psd.getProfess().equals(ILandConfig.langMap.get("DefaultProfess"))) {
            player.openInventory(this.inv);
            return true;
        }
        return false;
    }
}