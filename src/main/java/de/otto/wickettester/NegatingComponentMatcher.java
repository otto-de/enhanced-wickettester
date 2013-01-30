package de.otto.wickettester;

import org.apache.wicket.Component;

public class NegatingComponentMatcher<T extends Component> implements ComponentMatcher<T, T> {

    private final ComponentMatcher<T, T> matcher;

    public NegatingComponentMatcher(final ComponentMatcher<T, T> matcher) {
        this.matcher = matcher;
    }

    @Override
    public T match(final T component) {
        return matcher.match(component) != null ? null : component;
    }

    @Override
    public String criterionAsString() {
        return "not " + matcher.criterionAsString();
    }
}
