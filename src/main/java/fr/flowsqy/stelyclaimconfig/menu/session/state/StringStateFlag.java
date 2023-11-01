package fr.flowsqy.stelyclaimconfig.menu.session.state;

import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.jetbrains.annotations.NotNull;

public class StringStateFlag implements FlagState {

    private final StringFlag flag;
    private final String defaultValue = null;
    private String value;

    public StringStateFlag(@NotNull StringFlag flag) {
        this.flag = flag;
    }

    @Override
    public boolean isActive() {
        //final String defaultMessage = messages.getFormattedMessage("default-string-flags." + flagName.getName(), "%region%", playerName);
        if (defaultValue == null)
            return false;
        return !defaultValue.equals(value);
    }

    @Override
    public void apply(@NotNull ProtectedRegion region) {
        if (defaultValue == null) {
            region.setFlag(flag, null);
        } else {
            // Otherwise, set the flag
            region.setFlag(flag, value);
        }
    }

    @Override
    public void setDefault() {
        if (defaultValue == null) return;
        value = defaultValue;
    }
}
