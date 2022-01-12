import org.mockito.DoNotMock;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Mockito;

public class NonMockableTypesDoNotMockTest {

    @Mock
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">NotMockable</error> notMockableMockAnn;
    @Mock
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">NotMockableInterface</error> notMockableMockAnn2;
    @Mock
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. No reason provided.">NotMockableWithCustomAnnotation</error> notMockableMockAnn3;
    @Mock
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: A specific reason">NotMockableInterfaceWithReason</error> notMockableMockAnn4;
    @Mock
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfNotMockableInterface</error> notMockableMockAnn5;
    @Mock
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">TwoLevelSubclassOfInterface</error> notMockableMockAnn6;
    @Mock
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfNotMockable</error> notMockableMockAnn7;
    @Mock
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubInterfaceOfNotMockableInterface</error> notMockableMockAnn8;
    @Mock
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfSubInterfaceOfNotMockableInterface</error> notMockableMockAnn9;

    @Spy
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">NotMockable</error> notMockableSpyAnn;
    @Spy
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">NotMockableInterface</error> notMockableSpyAnn2;
    @Spy
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. No reason provided.">NotMockableWithCustomAnnotation</error> notMockableSpyAnn3;
    @Spy
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: A specific reason">NotMockableInterfaceWithReason</error> notMockableSpyAnn4;
    @Spy
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfNotMockableInterface</error> notMockableSpyAnn5;
    @Spy
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">TwoLevelSubclassOfInterface</error> notMockableSpyAnn6;
    @Spy
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfNotMockable</error> notMockableSpyAnn7;
    @Spy
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubInterfaceOfNotMockableInterface</error> notMockableSpyAnn8;
    @Spy
    <error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfSubInterfaceOfNotMockableInterface</error> notMockableSpyAnn9;
    
    public void testMethod() {
        var notMockableMock = Mockito.mock(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">NotMockable.class</error>);
        var notMockableMock2 = Mockito.mock(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">NotMockableInterface.class</error>);
        var notMockableMock3 = Mockito.mock(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. No reason provided.">NotMockableWithCustomAnnotation.class</error>);
        var notMockableMock4 = Mockito.mock(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: A specific reason">NotMockableInterfaceWithReason.class</error>);
        var notMockableMock5 = Mockito.mock(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfNotMockableInterface.class</error>);
        var notMockableMock6 = Mockito.mock(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">TwoLevelSubclassOfInterface.class</error>);
        var notMockableMock7 = Mockito.mock(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfNotMockable.class</error>);
        var notMockableMock8 = Mockito.mock(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubInterfaceOfNotMockableInterface.class</error>);
        var notMockableMock9 = Mockito.mock(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfSubInterfaceOfNotMockableInterface.class</error>);

        var notMockableSpy = Mockito.spy(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">NotMockable.class</error>);
        var notMockableSpy2 = Mockito.spy(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">NotMockableInterface.class</error>);
        var notMockableSpy3 = Mockito.spy(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. No reason provided.">NotMockableWithCustomAnnotation.class</error>);
        var notMockableSpy4 = Mockito.spy(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: A specific reason">NotMockableInterfaceWithReason.class</error>);
        var notMockableSpy5 = Mockito.spy(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfNotMockableInterface.class</error>);
        var notMockableSpy6 = Mockito.spy(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">TwoLevelSubclassOfInterface.class</error>);
        var notMockableSpy7 = Mockito.spy(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfNotMockable.class</error>);
        var notMockableSpy8 = Mockito.spy(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubInterfaceOfNotMockableInterface.class</error>);
        var notMockableSpy9 = Mockito.spy(<error descr="Mockitools: This type is not mockable since a type in the class hierarchy is annotated as @DoNotMock. The reason: Create a real instance instead.">SubclassOfSubInterfaceOfNotMockableInterface.class</error>);
    }

    @DoNotMock
    private static class NotMockable {}
    
    @DoNotMock
    private interface NotMockableInterface {}

    @pm.org.mockito.DoNotMock
    private static class NotMockableWithCustomAnnotation {}
    
    @DoNotMock(reason = "A specific reason")
    private interface NotMockableInterfaceWithReason {}

    static class SubclassOfNotMockableInterface implements NotMockableInterface {}

    private static class TwoLevelSubclassOfInterface extends SubclassOfNotMockableInterface {}

    private static class SubclassOfNotMockable extends NotMockable {}

    private interface SubInterfaceOfNotMockableInterface extends NotMockableInterface {}

    private static class SubclassOfSubInterfaceOfNotMockableInterface implements SubInterfaceOfNotMockableInterface {}
}
