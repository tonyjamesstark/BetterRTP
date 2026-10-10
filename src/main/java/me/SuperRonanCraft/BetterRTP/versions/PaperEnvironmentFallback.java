package me.SuperRonanCraft.BetterRTP.versions;

import io.papermc.lib.environments.PaperEnvironment;

public class PaperEnvironmentFallback extends PaperEnvironment {

    // PaperLib 1.0.8's version regex needs a one-digit major ("(MC: \d\.\d+...)"), so it can't
    // parse "(MC: 26.2)" and falls back to the pre-Paper sync handlers. isVersion(int,int) is
    // called virtually from the Environment/PaperEnvironment constructors to pick those
    // handlers, so always answering true here (we're always newer than anything PaperLib 1.0.8
    // checks for) makes it pick the real Paper async chunk/teleport handlers instead.
    @Override
    public boolean isVersion(int major, int minor) {
        return true;
    }
}
