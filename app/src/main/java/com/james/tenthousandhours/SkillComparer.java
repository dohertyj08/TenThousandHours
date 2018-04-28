package com.james.tenthousandhours;

import java.util.Comparator;

public class SkillComparer implements Comparator<Skill> {
    @Override
    public int compare(Skill a, Skill b) {
        int c = Long.compare(a.getTime(), b.getTime());
        return c;
    }
}
