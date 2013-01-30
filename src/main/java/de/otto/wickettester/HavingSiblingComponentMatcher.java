package de.otto.wickettester;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;

/**
 * Returns true if one of the siblings matches the criteria
 * 
 * @author Oliver Langer (oliver.langer@ottogroup.com)
 */
public class HavingSiblingComponentMatcher<T extends Component, CT extends Component> implements ComponentMatcher<T, T> {

    private final ComponentMatcher<CT, CT> matcher;

    public HavingSiblingComponentMatcher(final ComponentMatcher<CT, CT> matcher) {
        this.matcher = matcher;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T match(final T component) {
        if (component == null) {
            return null;
        }

        final MarkupContainer parent = component.getParent();

        if (parent == null) {
            return null;
        }

        T toReturn = null;
        final Iterator<Component> children = parent.iterator();
        while (children.hasNext()) {
            final CT next = (CT) children.next();
            if (matcher.match(next) != null) {
                toReturn = (T) next;
            }
        }

        return toReturn;
    }

    @Override
    public String criterionAsString() {
        return String.format("having a direct parent (%s)", matcher.criterionAsString());
    }

}
