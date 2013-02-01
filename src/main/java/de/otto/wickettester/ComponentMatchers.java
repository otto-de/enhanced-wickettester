package de.otto.wickettester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;

public class ComponentMatchers {

    public static class ComponentMatcherBuilder<T extends Component> {

        static class CollectingComponentMatcher<T extends Component, R> implements ComponentMatcher<T, R> {

            private final List<R> bucket;
            private final ComponentMatcher<T, R> componentMatcher;

            public CollectingComponentMatcher(final ComponentMatcher<T, R> matcher) {
                componentMatcher = matcher;
                bucket = new ArrayList<R>();
            }

            public void addToBucket(final R element) {
                bucket.add(element);
            }

            public List<R> getBucket() {
                return bucket;
            }

            @Override
            public R match(final T component) {
                final R visited = componentMatcher.match(component);
                if (visited != null) {
                    addToBucket(visited);
                }
                return null;
            }

            @Override
            public String criterionAsString() {
                return "a list of components";
            }
        }

        private static class JoiningComponentMatcher<T extends Component> implements ComponentMatcher<T, T> {

            private final List<ComponentMatcher<T, T>> componentMatchers = new LinkedList<ComponentMatcher<T, T>>(
                    Collections.singleton(new AllComponentMatcher<T>()));

            public JoiningComponentMatcher(final ComponentMatcher<T, T>... componentMatchers) {
                this.componentMatchers.addAll(Arrays.asList(componentMatchers));
            }

            @Override
            public T match(final T component) {
                for (final ComponentMatcher<T, T> visitor : componentMatchers) {
                    final T visited = visitor.match(component);
                    if (visited == null) {
                        return null;
                    }
                }
                return component;
            }

            @Override
            public String criterionAsString() {
                final List<String> criteria = new LinkedList<String>();
                for (final ComponentMatcher<T, T> visitor : componentMatchers) {
                    criteria.add(visitor.criterionAsString());
                }
                return StringUtils.join(criteria, " and ");
            }

        }

        private static class AllComponentMatcher<T extends Component> implements ComponentMatcher<T, T> {

            @Override
            public T match(final T component) {
                return component;
            }

            @Override
            public String criterionAsString() {
                return "being a component";
            }
        }

        private final List<ComponentMatcher<T, T>> visitors = new LinkedList<ComponentMatcher<T, T>>();

        public ComponentMatcherBuilder<T> wicketId(final String wicketId) {
            this.visitors.add(new WicketIdComponentMatcher<T>(wicketId));
            return this;
        }

        public ComponentMatcherBuilder<T> visible() {
            this.visitors.add(new VisibleComponentMatcher<T>());
            return this;
        }

        public ComponentMatcherBuilder<T> enabled() {
            this.visitors.add(new EnabledComponentMatcher<T>());
            return this;
        }

        public ComponentMatcherBuilder<T> type(final Class<T> componentClass) {
            this.visitors.add(new TypeComponentMatcher<T>(componentClass));
            return this;
        }

        public ComponentMatcherBuilder<T> visitedBy(final ComponentMatcher<T, T> visitor) {
            this.visitors.add(visitor);
            return this;
        }

        public <MT extends Component> ComponentMatcherBuilder<T> havingChild(final ComponentMatcherBuilder<MT> builder) {
            this.visitors.add(new HavingChildComponentMatcher<T, MT>(builder.build()));
            return this;
        }

        public <MT extends Component> ComponentMatcherBuilder<T> havingDirectChild(final ComponentMatcherBuilder<MT> builder) {
            this.visitors.add(new HavingDirectChildComponentMatcher<T, MT>(builder.build()));
            return this;
        }

        public <MT extends Component> ComponentMatcherBuilder<T> havingSibling(final ComponentMatcherBuilder<MT> builder) {
            this.visitors.add(new HavingSiblingComponentMatcher<T, MT>(builder.build()));
            return this;
        }

        public <MT extends MarkupContainer> ComponentMatcherBuilder<T> havingDirectParent(
                final ComponentMatcherBuilder<MT> builder) {
            this.visitors.add(new HavingDirectParentComponentMatcher<T, MT>(builder.build()));
            return this;
        }

        public ComponentMatcherBuilder<T> modelObject(final Object modelObject) {
            this.visitors.add(new ModelObjectComponentMatcher<T>(modelObject));
            return this;
        }

        public ComponentMatcherBuilder<T> not(final ComponentMatcherBuilder<T> builder) {
            this.visitors.add(new NegatingComponentMatcher<T>(builder.build()));
            return this;
        }

        @SuppressWarnings("unchecked")
        public ComponentMatcher<T, T> build() {
            return new JoiningComponentMatcher<T>(visitors.toArray(new ComponentMatcher[visitors.size()]));
        }

        public CollectingComponentMatcher<T, T> buildCollecting() {
            return new CollectingComponentMatcher<T, T>(build());
        }

        public Object criteriaAsString() {
            final List<String> criteria = new LinkedList<String>();
            for (final ComponentMatcher<T, T> visitor : visitors) {
                criteria.add(visitor.criterionAsString());
            }
            return StringUtils.join(criteria, " and ");
        }
    }

    public static <T extends Component> ComponentMatcherBuilder<T> builder() {
        return new ComponentMatcherBuilder<T>();
    }

    public static <T extends Component> ComponentMatcherBuilder<T> type(final Class<T> componentClass) {
        return ComponentMatchers.<T> builder().type(componentClass);
    }

}
