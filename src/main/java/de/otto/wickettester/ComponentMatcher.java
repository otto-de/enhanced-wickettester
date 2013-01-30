package de.otto.wickettester;

import org.apache.wicket.Component;

public interface ComponentMatcher<T extends Component, R> {

    R match(T component);

    String criterionAsString();
}