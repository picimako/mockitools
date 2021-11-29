import java.util.List;
import java.util.ArrayList;

import org.mockito.MockSettings;
import org.mockito.NotExtensible;

public interface <warning descr="Mockitools: This interface extends another interface annotated as @NotExtensible.">NotExtensibleValidation</warning> extends MockSettings {

    interface <warning descr="Mockitools: This interface extends another interface annotated as @NotExtensible.">NotExtensibleOneOfMultipleInterfaces</warning> extends MockSettings, List<String> {
    }

    interface <warning descr="Mockitools: This interface extends another interface annotated as @NotExtensible.">NotExtensibleOneOfMultipleInterfaces2</warning> extends List<String>, MockSettings {
    }
    
    interface ExtensibleInterface extends List<String> {
    }

    enum NotExtensibleEnum {
    }

    class SimpleClass {
    }
    
    class ExtensibleClass extends ArrayList<String> {
    }

    class <warning descr="Mockitools: This class extends a class annotated as @NotExtensible.">NotExtensibleClassCheck</warning> extends NotExtensibleClass {
    }

    abstract class <warning descr="Mockitools: This class extends a class annotated as @NotExtensible.">NotExtensibleAbstractClassCheck</warning> extends NotExtensibleClass {
        {
            new ForAnonymousInstantiation() {
            };
        }
    }

//    record Person(String name, String address) {
//    }

    @NotExtensible
    class NotExtensibleClass {
    }

    interface ForAnonymousInstantiation {
    }
}
