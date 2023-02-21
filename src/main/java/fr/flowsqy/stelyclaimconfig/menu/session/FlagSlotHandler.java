package fr.flowsqy.stelyclaimconfig.menu.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlagSlotHandler {

    private final List<Integer> flagSlots;
    private final FlagManager flagManager;
    private final PageManager pageManager;
    private Iterator<String> pageFlagIdItr;

    public FlagSlotHandler(List<Integer> flagSlots, FlagManager flagManager, PageManager pageManager) {
        this.flagSlots = flagSlots;
        this.flagManager = flagManager;
        this.pageManager = pageManager;
    }

    public List<Integer> getFlagSlots() {
        return flagSlots;
    }

    /**
     * Get the flag identifier mapped to a slot on the current page
     *
     * @param slot The slot of the item
     * @return A flag {@link String} identifier
     */
    public String getAttachedFlagId(int slot) {
        return flagManager.getAvailableFlags().get(pageManager.getCurrentPage() * flagSlots.size() + flagSlots.indexOf(slot));
    }

    /**
     * Get the {@link Iterator} of the flag identifiers of this page
     *
     * @return A {@link String} {@link Iterator}
     */
    public Iterator<String> getPageFlagIdItr() {
        return pageFlagIdItr;
    }

    /**
     * Initialize the flag identifier iterator of this page
     */
    public void createPageFlagIdItr() {
        final List<String> flagIdentifiers = new ArrayList<>();
        final List<String> availableFlags = flagManager.getAvailableFlags();
        if (!availableFlags.isEmpty()) {
            final int numberOfFlagSlots = flagSlots.size();
            final int currentPage = pageManager.getCurrentPage();
            // Get the flag identifier mapped to this slot index on this page
            for (
                    int flagIndex = currentPage * numberOfFlagSlots, slotIndex = 0;
                    flagIndex < availableFlags.size() && slotIndex < numberOfFlagSlots;
                    flagIndex++, slotIndex++
            ) {
                flagIdentifiers.add(availableFlags.get(flagIndex));
            }
        }
        pageFlagIdItr = flagIdentifiers.iterator();
    }

    /**
     * Clear the flag identifier iterator of this page
     */
    public void clearPageFlagIdItr() {
        pageFlagIdItr = null;
    }

}
