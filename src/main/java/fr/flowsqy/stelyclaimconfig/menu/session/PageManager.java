package fr.flowsqy.stelyclaimconfig.menu.session;

import java.util.function.Function;

public class PageManager {

    private int numberOfPages;
    private int currentPage;

    public PageManager() {
        this.numberOfPages = 0;
        this.currentPage = 0; // Start at first page
    }

    public void load(int numberOfFlagSlot, int numberOfAvailableFlags) {
        this.numberOfPages = calculateNumberOfPages(numberOfFlagSlot, numberOfAvailableFlags);
    }

    private int calculateNumberOfPages(int numberOfFlagSlot, int numberOfAvailableFlags) {
        final int remainder = numberOfAvailableFlags % numberOfFlagSlot;
        return (numberOfAvailableFlags - remainder) / numberOfFlagSlot + (remainder > 0 ? 1 : 0);
    }


    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Function<Integer, Integer> pageFunction) {
        setCurrentPage(pageFunction.apply(currentPage));
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean canGoTo(Function<Integer, Integer> pageFunction) {
        return canGoTo(pageFunction.apply(currentPage));
    }

    public boolean canGoTo(int page) {
        return page >= 0 && page < numberOfPages;
    }

    /**
     * The current page index starting from 1
     *
     * @return The current page starting from 1
     */
    public int getReadableCurrentPage() {
        return currentPage + 1;
    }

    /**
     * Get the number of page this session have
     *
     * @return The page number of the session
     */
    public int numberOfPage() {
        return numberOfPages;
    }

}
