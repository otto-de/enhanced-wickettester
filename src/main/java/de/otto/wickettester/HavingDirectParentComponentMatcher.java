package de.otto.wickettester;

import org.apache.wicket.Component;

public class HavingDirectParentComponentMatcher<T extends Component, CT extends Component> implements ComponentMatcher<T, T> {

    private final ComponentMatcher<CT, CT> matcher;

    public HavingDirectParentComponentMatcher(final ComponentMatcher<CT, CT> matcher) {
        this.matcher = matcher;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T match(final T component) {
        // this is just plain wrong, but works, since the type matcher is in the chain
        return matcher.match((CT) component.getParent()) != null ? component : null;

    }

    @Override
    public String criterionAsString() {
        return String.format("having a direct parent (%s)", matcher.criterionAsString());
    }

}
