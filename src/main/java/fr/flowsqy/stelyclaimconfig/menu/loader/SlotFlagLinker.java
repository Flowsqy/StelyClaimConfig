package fr.flowsqy.stelyclaimconfig.menu.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class SlotFlagLinker {

    private List<Integer> flagSlots;

    public void setFlagSlots(List<Integer> flagSlots) {
        this.flagSlots = flagSlots;
    }

    public void link(List<Integer> flagSlots) {
        // Register sorted slots to map each slot to its flag
        final List<Integer> sanitizedSlots = new ArrayList<>(new HashSet<>(this.flagSlots));
        Collections.sort(sanitizedSlots);

        flagSlots.addAll(sanitizedSlots);
    }

}
