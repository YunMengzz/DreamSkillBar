package com.github.yunmeng.runnable;

import com.github.yunmeng.ILandSkillBar;
import com.github.yunmeng.utils.SkillCaster;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;


public class CooldownRunnable
        implements Runnable {
    public void run() {
        Iterator<Entry<OfflinePlayer, Integer>> ma = ILandSkillBar.safeTime.entrySet().iterator();
        while (ma.hasNext()) {
            Entry<OfflinePlayer, Integer> e = ma.next();
            int i = e.getValue() - 1;
            if (i > 0) {
                e.setValue(i);
            } else {
                ma.remove();
            }
        }


        Iterator<Entry<LivingEntity, HashMap<String, Integer>>> it = SkillCaster.cooldownMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<LivingEntity, HashMap<String, Integer>> e = it.next();
            HashMap<String, Integer> map = e.getValue();
            Iterator<Entry<String, Integer>> it2 = map.entrySet().iterator();
            while (it2.hasNext()) {
                Entry<String, Integer> e2 = it2.next();
                int i = e2.getValue() - 1;
                if (i > 0) {
                    e2.setValue(i);
                } else {
                    it2.remove();
                }
            }
            if (!map.isEmpty()) {
                e.setValue(map);
            } else {
                it.remove();
            }
        }
    }
}