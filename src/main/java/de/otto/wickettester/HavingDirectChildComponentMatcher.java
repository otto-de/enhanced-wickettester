package de.otto.wickettester;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;

public class HavingDirectChildComponentMatcher<T extends Component, CT extends Component> implements ComponentMatcher<T, T> {

    private final ComponentMatcher<CT, CT> matcher;

    public HavingDirectChildComponentMatcher(final ComponentMatcher<CT, CT> matcher) {
        this.matcher = matcher;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T match(final T component) {
        if (!(component instanceof MarkupContainer)) {
            return null;
        } else {
            for (final Component directChild : (MarkupContainer) component) {
                // this is just plain wrong, but works, since the type matcher is in the chain
                final CT result = matcher.match((CT) directChild);
                if (result != null) {
                    return component;
                }
            }
            return null;
        }
    }

    @Override
    public String criterionAsString() {
        return String.format("having a direct child (%s)", matcher.criterionAsString());
    }

}
