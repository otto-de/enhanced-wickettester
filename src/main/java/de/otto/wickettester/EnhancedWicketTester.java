package de.otto.wickettester;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.util.visit.Visits;
import org.testng.Assert;

import de.otto.wickettester.ComponentMatchers.ComponentMatcherBuilder;
import de.otto.wickettester.ComponentMatchers.ComponentMatcherBuilder.CollectingComponentMatcher;

public class EnhancedWicketTester extends WicketTester {

    public EnhancedWicketTester() {
        super();
    }

    public EnhancedWicketTester(final WebApplication application) {
        super(application);
    }

    public static <T extends Component, R> R visitComponentTree(final MarkupContainer root, final ComponentMatcher<T, R> matcher) {
        final R res = Visits.visitChildren(root, new IVisitor<T, R>() {

            @Override
            public void component(final T component, final IVisit<R> visit) {
                final R result = matcher.match(component);
                if (result != null) {
                    visit.stop(result);
                }
            }
        });
        return res;
    }

    public <T extends Component> List<T> getChildrenMatching(final MarkupContainer root,
            final ComponentMatcherBuilder<T> builder, final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        final ComponentMatcherBuilder<? extends MarkupContainer>[] realParentBuilders = cleanup(parentBuilders);
        if (realParentBuilders.length == 0) {
            final CollectingComponentMatcher<T, T> matcher = builder.buildCollecting();
            visitComponentTree(root, matcher);
            return matcher.getBucket();
        } else {
            final List<? extends MarkupContainer> parentsMatching = getChildrenMatching(root, realParentBuilders[0],
                    tail(realParentBuilders));
            final List<T> childrenMatching = new LinkedList<T>();
            for (final Component parent : parentsMatching) {
                final CollectingComponentMatcher<T, T> matcher = builder.buildCollecting();
                visitComponentTree((MarkupContainer) parent, matcher);
                childrenMatching.addAll(matcher.getBucket());
            }
            return childrenMatching;
        }
    }

    @SuppressWarnings("unchecked")
    private ComponentMatcherBuilder<? extends MarkupContainer>[] cleanup(
            final ComponentMatcherBuilder<? extends MarkupContainer>[] builders) {
        if (builders == null || builders.length == 0) {
            return new ComponentMatcherBuilder[0];
        }
        // remove null values
        ComponentMatcherBuilder<? extends MarkupContainer>[] buildersWip = builders;
        while (ArrayUtils.contains(buildersWip, null)) {
            buildersWip = (ComponentMatcherBuilder<? extends MarkupContainer>[]) ArrayUtils
                    .removeElement(builders, (Object) null);
        }
        return buildersWip;
    }

    @SuppressWarnings("unchecked")
    private ComponentMatcherBuilder<? extends MarkupContainer>[] tail(
            final ComponentMatcherBuilder<? extends MarkupContainer>[] arr) {
        return (ComponentMatcherBuilder<? extends MarkupContainer>[]) ArrayUtils.remove(arr, 0);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> List<T> getChildrenMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder) {
        return getChildrenMatching(root, builder, (ComponentMatcherBuilder<? extends MarkupContainer>) null);
    }

    public <T extends Component> List<T> getChildrenMatching(final ComponentMatcherBuilder<T> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        return getChildrenMatching(getLastRenderedPage(), builder, parentBuilders);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> List<T> getChildrenMatching(final ComponentMatcherBuilder<T> builder) {
        return getChildrenMatching(builder, (ComponentMatcherBuilder<? extends MarkupContainer>) null);
    }

    public <T extends Component> T getChildMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        final List<T> children = getChildrenMatching(root, builder, parentBuilders);

        Assert.assertEquals(children.size(), 1, String.format("Did not find exactly one child %s", builder.criteriaAsString()));

        return children.get(0);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getChildMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder) {
        return getChildMatching(root, builder, (ComponentMatcherBuilder<? extends MarkupContainer>) null);
    }

    public <T extends Component> T getChildMatching(final ComponentMatcherBuilder<T> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        return getChildMatching(getLastRenderedPage(), builder, parentBuilders);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getChildMatching(final ComponentMatcherBuilder<T> builder) {
        return getChildMatching(builder, (ComponentMatcherBuilder<? extends MarkupContainer>) null);
    }

    public <T extends Component> T getFirstChildMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        final List<T> children = getChildrenMatching(root, builder, parentBuilders);

        Assert.assertTrue(children.size() > 0, String.format("Did not find at least one child %s", builder.criteriaAsString()));

        return children.get(0);
    }

    public <T extends Component> T getFirstChildMatching(final ComponentMatcherBuilder<T> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        return getFirstChildMatching(getLastRenderedPage(), builder, parentBuilders);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getFirstChildMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder) {
        return getFirstChildMatching(root, builder, (ComponentMatcherBuilder<? extends MarkupContainer>) null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getFirstChildMatching(final ComponentMatcherBuilder<T> builder) {
        return getFirstChildMatching(builder, (ComponentMatcherBuilder<? extends MarkupContainer>) null);
    }

    public String getPathRelativeToRoot(final Component root, final Component component) {
        final String rootPath = root.getPageRelativePath();
        final String componentPath = component.getPageRelativePath();

        Assert.assertTrue(componentPath.startsWith(rootPath), "Component is not a child of the root component");

        if (rootPath.length() == componentPath.length()) {
            return StringUtils.EMPTY;
        }

        /*
         * remove the root path prefix and any possible leftover path
         * separators.
         */
        return StringUtils.stripStart(componentPath.substring(rootPath.length()), ":");
    }

    public String getPathRelativeToPage(final Component component) {
        return getPathRelativeToRoot(getLastRenderedPage(), component);
    }

    public TagTester getTagTesterByComponent(final Component component) {
        final String[] pathSegments = component.getPath().split(String.valueOf(Component.PATH_SEPARATOR));

        //strip leading 0:
        final String[] tail = Arrays.copyOfRange(pathSegments, 1, pathSegments.length);

        return TagTester.createTagByAttribute(getLastResponseAsString(), "wicketpath", getWicketPath(tail));
    }

    public TagTester getTagTesterByComponentMatcher(final ComponentMatcherBuilder<? extends Component> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        return getTagTesterByComponent(getChildMatching(builder, parentBuilders));
    }

    private String getWicketPath(final String[] tail) {
        for (int i = 0; i < tail.length; i++) {
            tail[i] = tail[i].replace("_", "__");
        }
        return StringUtils.join(tail, "_");
    }

    public void assertModelValue(final Component component, final Object expectedModelObject) {
        assertThat(component.getDefaultModelObject(), equalTo(expectedModelObject));
    }

    public void assertModelValue(final Object expectedModelObject, final ComponentMatcherBuilder<? extends Component> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        assertThat(getChildMatching(builder, parentBuilders).getDefaultModelObject(), equalTo(expectedModelObject));
    }

    public void assertVisible(final Component component) {
        assertVisible(getPathRelativeToPage(component));
    }

    public void assertVisible(final ComponentMatcherBuilder<? extends Component> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        assertVisible(getChildMatching(builder, parentBuilders));
    }

    public void assertFeedback(final ComponentMatcherBuilder<? extends Component> builder, final String... feedback) {
        assertFeedback(getPathRelativeToPage(getChildMatching(builder)), feedback);
    }

    public FormTester newFormTester(final Form<?> form) {
        return newFormTester(getPathRelativeToPage(form));
    }

    public FormTester newFormTester(@SuppressWarnings("rawtypes") final ComponentMatcherBuilder<? extends Form> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        return newFormTester(getChildMatching(builder, parentBuilders));
    }

    public void assertInvisible(final Label component) {
        assertInvisible(getPathRelativeToPage(component));
    }

    public void clickLink(final ComponentMatcherBuilder<? extends Component> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        clickLink(getChildMatching(builder, parentBuilders));
    }

    public void executeAjaxEvent(final String event, final ComponentMatcherBuilder<? extends Component> builder,
            final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) {
        executeAjaxEvent(getChildMatching(builder, parentBuilders), event);
    }

}
