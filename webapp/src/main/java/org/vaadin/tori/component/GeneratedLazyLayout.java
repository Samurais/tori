package org.vaadin.tori.component;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class GeneratedLazyLayout extends AbstractLazyLayout {
    /**
     * The ComponentGenerator is an interface that allows the client code to
     * generate the used components lazily.
     * <p/>
     * <strong>Note: Using this does not support the modification of existing
     * components.</strong> So this interface works best with an immutable
     * amount of lazily generated components.
     * <p/>
     * {@link LazyLayout#replaceComponent(Component, Component)
     * replaceComponent()} and {@link LazyLayout#removeComponent(Component)
     * removeComponent()} might work, but you need to handle the bookkeeping
     * yourself.
     */
    public interface ComponentGenerator {
        /**
         * Get the Components found at places between (and including) two index
         * points.
         * 
         * @param from
         *            The zero-based index of the first component to return
         * @param to
         *            The zero-based index of the last component to return
         * @return The components between (and including) <code>from</code> and
         *         <code>to</code> such that they are in displayed order.
         */
        List<Component> getComponentsAtIndexes(int from, int to);

        /**
         * This method is called only once, and it needs to contain the amount
         * of components shown throughout the lifetime of the LazyLayout.
         */
        int getAmountOfComponents();
    }

    private final ComponentGenerator generator;

    public GeneratedLazyLayout(final ComponentGenerator generator) {
        this.generator = generator;
        getState()
                .setTotalAmountOfComponents(generator.getAmountOfComponents());
    }

    /**
     * @deprecated
     * @throws UnsupportedOperationException
     *             always
     */
    @Deprecated
    @Override
    public void addComponent(final Component c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     * @throws UnsupportedOperationException
     *             always
     */
    @Deprecated
    @Override
    public void addComponentEagerly(final Component c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     * @throws UnsupportedOperationException
     *             always
     */
    @Deprecated
    @Override
    public void removeAllComponents() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     * @throws UnsupportedOperationException
     *             always
     */
    @Deprecated
    @Override
    public void removeComponent(final Component c) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void loadingHook(final List<Integer> indicesToFetch) {
        System.out.println("GeneratedLazyLayout.loadingHook()");
        for (final int[] range : groupToRanges(indicesToFetch)) {
            final int from = range[0];
            final int to = range[1];

            for (final Component component : generator.getComponentsAtIndexes(
                    from, to)) {
                System.out.println(component);
            }
        }
    }

    /* default protected due to tests */
    static List<int[]> groupToRanges(final List<Integer> orderedNumbers) {
        final List<int[]> ranges = new ArrayList<int[]>();

        if (!orderedNumbers.isEmpty()) {
            int start = orderedNumbers.get(0);
            int previous = start;

            for (final int num : orderedNumbers) {
                if (num > previous + 1) {
                    ranges.add(new int[] { start, previous });
                    start = num;
                }
                previous = num;
            }
            ranges.add(new int[] { start, previous });
        }

        return ranges;
    }

}