package de.otto.wickettester;

import org.apache.wicket.Component;

public class EnabledComponentMatcher<T extends Component> implements ComponentMatcher<T, T> {

    @Override
    public T match(final T component) {
        if (component.isEnabledInHierarchy()) {
            return component;
        }
        return null;
    }

    @Override
    public String criterionAsString() {
        return String.format("being enabled");
    }

}
