package de.otto.wickettester;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.util.visit.Visits;

public class HavingChildComponentMatcher<T extends Component, CT extends Component> implements ComponentMatcher<T, T> {

    private final ComponentMatcher<CT, CT> matcher;

    public HavingChildComponentMatcher(final ComponentMatcher<CT, CT> matcher) {
        this.matcher = matcher;
    }

    @Override
    public T match(final T component) {
        if (!(component instanceof MarkupContainer)) {
            return null;
        } else {
            final Component res = Visits.visitChildren((MarkupContainer) component, new IVisitor<CT, CT>() {

                @Override
                public void component(final CT component, final IVisit<CT> visit) {
                    final CT result = matcher.match(component);
                    if (result != null) {
                        visit.stop(result);
                    }
                }
            });
            return res != null ? component : null;
        }
    }

    @Override
    public String criterionAsString() {
        return String.format("having a child (%s)", matcher.criterionAsString());
    }

}
