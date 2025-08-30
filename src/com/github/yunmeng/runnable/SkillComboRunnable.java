package com.github.yunmeng.runnable;

import com.github.yunmeng.custom.skills.SkillCombo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class SkillComboRunnable implements Runnable {
    public void run() {
        Iterator<Entry<OfflinePlayer, SkillCombo>> it = SkillCombo.playerSkillCombos.entrySet().iterator();
        while (it.hasNext()) {
            Entry<OfflinePlayer, SkillCombo> e = it.next();
            if (((OfflinePlayer) e.getKey()).isOnline()) {
                SkillCombo sc = (SkillCombo) e.getValue();
                if (sc.read((Player) e.getKey())) {
                    e.setValue(sc);
                } else {
                    it.remove();
                }
            }
        }
    }
}