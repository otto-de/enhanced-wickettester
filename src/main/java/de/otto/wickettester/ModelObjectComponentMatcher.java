package de.otto.wickettester;

import org.apache.wicket.Component;

public class ModelObjectComponentMatcher<T extends Component> implements ComponentMatcher<T, T> {

    private final Object modelObject;

    public ModelObjectComponentMatcher(final Object modelObject) {
        this.modelObject = modelObject;
    }

    @Override
    public T match(final T component) {
        return modelObject.equals(component.getDefaultModelObject()) ? component : null;
    }

    @Override
    public String criterionAsString() {
        return String.format("having a model object '%s'", modelObject);
    }

}
