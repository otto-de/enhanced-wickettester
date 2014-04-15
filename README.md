# Enhanced WicketTester

## Description

The WicketTester is a helper class to ease unit testing of wicket component and pages. There is no need for a servlet container, because it uses a mocked servlet context and a mocked wicket application. In the following it is possible to test the rendered page and the contained components. This is mostly done by selecting components with their wicket path as string. As you can image this is a common error on refactoring components on pages which results in changed wicket paths. 

The following example shows how the WicketTester renders a test page and asserts that a bookmarkable link is present to that page.

```java
//given
final WicketTester tester = new WicketTester();
tester.startPage(new TestPage());

//when
final Component link = tester.getComponentFromLastRenderedPage("some:wicket:path");

//then
assertThat(link, is(not(nullValue())));
```

Now this example can be rewritten selecting the component using the component matchers. The Enhanced WicketTester uses a
component matcher construct for several functions you are already used to call with a wicket-path.

```java
//given
final EnhancedWicketTester tester = new EnhancedWicketTester();
tester.startPage(new TestPage());

//when
final BookmarkablePageLink<?> link = tester.getChildMatching(
    ComponentMatchers.type(BookmarkablePageLink.class));

//then
assertThat(link, is(not(nullValue())));
```

## Features

- select components without the need of the wicket path
- base wickettester functionality using component matchers

## Usage

Selectors for components can be formed using the ComponentMatcherBuilder which uses a builder pattern:

```java
ComponentMatchers.type(BookmarkablePageLink.class).wicketId("link").visible();
```

Selectors can be combined together:

```java
ComponentMatchers.type(BookmarkablePageLink.class).wicketId("link").visible()
    .havingChild(ComponentMatchers.type(BookmarkablePageLink.class));
```

### Selectors

In the following you will find a list of all selectors possible:

```java
ComponentMatcherBuilder<T> type(final Class<T> componentClass)
```

```java
ComponentMatcherBuilder<T> wicketId(final String wicketId)
```

```java
ComponentMatcherBuilder<T> visible()
```

```java
ComponentMatcherBuilder<T> enabled()
```

```java
ComponentMatcherBuilder<T> type(final Class<T> componentClass)
```

```java
ComponentMatcherBuilder<T> not(final ComponentMatcherBuilder<T> builder)
```

```java
ComponentMatcherBuilder<T> modelObject(final Object modelObject)
```

```java
ComponentMatcherBuilder<T> havingChild(final ComponentMatcherBuilder<MT> builder)
```

```java
ComponentMatcherBuilder<T> havingDirectChild(final ComponentMatcherBuilder<MT> builder)
```

```java
ComponentMatcherBuilder<T> havingSibling(final ComponentMatcherBuilder<MT> builder)
```

```java
ComponentMatcherBuilder<T> havingDirectParent(final ComponentMatcherBuilder<MT> builder)
```

### WicketTester functions

#### Select a list of children

```java
List<T> getChildrenMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder)
```

```java
List<T> getChildrenMatching(final ComponentMatcherBuilder<T> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) 
```

```java
List<T> getChildrenMatching(final ComponentMatcherBuilder<T> builder) 
```

#### Select a single child

```java
T getChildMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders)
```

```java
T getChildMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder) 
```

```java
T getChildMatching(final ComponentMatcherBuilder<T> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders)
```

```java
T getChildMatching(final ComponentMatcherBuilder<T> builder)
```

#### Select first child of a list of children

```java
T getFirstChildMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders)
```

```java            
T getFirstChildMatching(final ComponentMatcherBuilder<T> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders)
```

```java
T getFirstChildMatching(final MarkupContainer root, final ComponentMatcherBuilder<T> builder) 
```

```java
T getFirstChildMatching(final ComponentMatcherBuilder<T> builder) 
```

#### Get the wicket-path

```java
String getPathRelativeToRoot(final Component root, final Component component) 
```

```java
String getPathRelativeToPage(final Component component) 
```

#### Create a TagTester for attribute testing

```java
TagTester getTagTesterByComponent(final Component component) 
```

```java
TagTester getTagTesterByComponentMatcher(final ComponentMatcherBuilder<? extends Component> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders) 
```

#### Create a FormTester

```java
FormTester newFormTester(final Form<?> form)
```

```java
FormTester newFormTester(final ComponentMatcherBuilder<? extends Form> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders)
```

#### Execution

```java
void clickLink(final ComponentMatcherBuilder<? extends Component> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders)
```

```java
void executeAjaxEvent(final String event, 
    final ComponentMatcherBuilder<? extends Component> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders)
```

#### Assertion helpers

```java
void assertModelValue(final Object expectedModelObject, 
    final ComponentMatcherBuilder<? extends Component> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders)
```

```java
void assertVisible(final Component component) 
```

```java
void assertVisible(final ComponentMatcherBuilder<? extends Component> builder,
    final ComponentMatcherBuilder<? extends MarkupContainer>... parentBuilders)
```

```java
void assertInvisible(final Label component) 
```

```java
void assertFeedback(final ComponentMatcherBuilder<? extends Component> builder, 
    final String... feedback) 
```

## Gradle, Maven

You can find all releases in Maven Central and in the public Sonatype repository:

https://oss.sonatype.org/content/repositories/releases

The current release is 0.1.0:

* de.otto:enhanced-wickettester:0.1.0

For development usage you should use the snapshot instead:

https://oss.sonatype.org/content/repositories/snapshots

The current snapshot-release is 0.1.1-SNAPSHOT:

* de.otto:enhanced-wickettester:0.1.1-SNAPSHOT

## License

Apache 2
