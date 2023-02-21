package fr.flowsqy.stelyclaimconfig.menu.session;

import com.sk89q.worldguard.protection.flags.Flag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlagSlotHandler {

    private final List<Integer> flagSlots;
    private final FlagManager flagManager;
    private final PageManager pageManager;
    private Iterator<Flag<?>> pageFlagsItr;

    public FlagSlotHandler(List<Integer> flagSlots, FlagManager flagManager, PageManager pageManager) {
        this.flagSlots = flagSlots;
        this.flagManager = flagManager;
        this.pageManager = pageManager;
    }

    public List<Integer> getFlagSlots() {
        return flagSlots;
    }

    /**
     * Get the flag mapped to a slot on the current page
     *
     * @param slot The slot of the item
     * @return The mapped {@link Flag}
     */
    public Flag<?> getAttachedFlag(int slot) {
        return flagManager.getAvailableFlags().get(pageManager.getCurrentPage() * flagSlots.size() + flagSlots.indexOf(slot));
    }

    /**
     * Get the flag {@link Iterator} of this page
     *
     * @return A {@link Flag} {@link Iterator}
     */
    public Iterator<Flag<?>> getPageFlagsItr() {
        return pageFlagsItr;
    }

    /**
     * Initialize the flag iterator of this page
     */
    public void createPageFlagsItr() {
        final List<Flag<?>> flagsOnThePage = new ArrayList<>();
        final List<Flag<?>> availableFlags = flagManager.getAvailableFlags();
        if (!availableFlags.isEmpty()) {
            final int numberOfFlagSlots = flagSlots.size();
            final int currentPage = pageManager.getCurrentPage();
            // Get the flag identifier mapped to this slot index on this page
            for (
                    int flagIndex = currentPage * numberOfFlagSlots, slotIndex = 0;
                    flagIndex < availableFlags.size() && slotIndex < numberOfFlagSlots;
                    flagIndex++, slotIndex++
            ) {
                flagsOnThePage.add(availableFlags.get(flagIndex));
            }
        }
        pageFlagsItr = flagsOnThePage.iterator();
    }

    /**
     * Clear the flag iterator of this page
     */
    public void clearPageFlagsItr() {
        pageFlagsItr = null;
    }

}
