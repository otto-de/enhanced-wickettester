package de.otto.wickettester;

import org.apache.wicket.Component;

public class TypeComponentMatcher<T extends Component> implements ComponentMatcher<T, T> {

    private final Class<T> componentClass;

    public TypeComponentMatcher(final Class<T> componentClass) {
        this.componentClass = componentClass;
    }

    @Override
    public T match(final T component) {
        if (componentClass.isAssignableFrom(component.getClass())) {
            return component;
        }
        return null;
    }

    @Override
    public String criterionAsString() {
        return String.format("being of type '%s'", componentClass.getSimpleName());
    }

}