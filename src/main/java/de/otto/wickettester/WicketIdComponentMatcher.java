package de.otto.wickettester;

import org.apache.wicket.Component;

public class WicketIdComponentMatcher<T extends Component> implements ComponentMatcher<T, T> {

    private final String wicketId;

    public WicketIdComponentMatcher(final String wicketId) {
        this.wicketId = wicketId;
    }

    @Override
    public T match(final T component) {
        if (component.getId().equals(wicketId)) {
            return component;
        }
        return null;
    }

    @Override
    public String criterionAsString() {
        return String.format("having the wicket id '%s'", wicketId);
    }

}