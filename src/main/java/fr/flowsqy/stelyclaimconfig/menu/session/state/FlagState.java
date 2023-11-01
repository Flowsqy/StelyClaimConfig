package fr.flowsqy.stelyclaimconfig.menu.session.state;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.jetbrains.annotations.NotNull;

public interface FlagState {

    boolean isActive();

    void apply(@NotNull ProtectedRegion region);

    void setDefault();

}
