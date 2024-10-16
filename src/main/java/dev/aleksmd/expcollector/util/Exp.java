package dev.aleksmd.expcollector.util;

import org.jetbrains.annotations.NotNull;

public final class Exp {
    @NotNull
    public static final Exp INSTANCE = new Exp();

    private Exp() {
    }

    public int calculateExp(int level) {
        if (level <= 15) {
            return level * level + 6 * level;
        } else {
            return level <= 30 ? (int)(2.5D * (double)level * (double)level - 40.5D * (double)level + 360.0D) : (int)(4.5D * (double)level * (double)level - 162.5D * (double)level + 2220.0D);
        }
    }
}