package com.github.yunmeng.custom.profess;

import java.util.HashMap;

public class ProfessUtil {
    public static HashMap<String, ILandProfess> professMap = new HashMap();

    public static boolean isStudyThis(String profess, String skill) {
        if (professMap.containsKey(profess)) {
            ILandProfess ilp = (ILandProfess) professMap.get(profess);
            return ilp.getSkills().contains(skill);
        }
        return false;
    }
}


