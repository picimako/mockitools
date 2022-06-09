package com.picimako.mockitools.inspection;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.Mockito;

public class ExtraInterfacesTest {

    @Mock(extraInterfaces = List.class)
    public Object mockInterface;

    @Mock(extraInterfaces = <error descr="Mockitools: The referenced type is not an interface. @Mock#extraInterfaces() must be provided only interface types.">Object.class</error>)
    public Object mockNotInterface;

    @Mock(extraInterfaces = {<error descr="Mockitools: The referenced type is not an interface. @Mock#extraInterfaces() must be provided only interface types.">Object.class</error>})
    public Object mockNotInterfaceArrayInit;

    @Mock(extraInterfaces = {List.class, Set.class, Map.class})
    public Object mockInterfaceMultiple;

    @Mock(extraInterfaces = {List.class, Set.class, <error descr="Mockitools: The referenced type is not an interface. @Mock#extraInterfaces() must be provided only interface types.">Object.class</error>})
    public Object mockNotInterfaceOfMultiple;

    @Mock(extraInterfaces = {<error descr="Mockitools: The referenced type is not an interface. @Mock#extraInterfaces() must be provided only interface types.">Object.class</error>, <error descr="Mockitools: The referenced type is not an interface. @Mock#extraInterfaces() must be provided only interface types.">ArrayList.class</error>, <error descr="Mockitools: The referenced type is not an interface. @Mock#extraInterfaces() must be provided only interface types.">AbstractMap.class</error>})
    public Object mockNotInterfaceAtAll;

    public void testMethod() {
        Object mockInterface = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class));
        Object mockNotInterface = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(<error descr="Mockitools: The referenced type is not an interface. MockSettings#extraInterfaces() must be provided only interface types.">Object.class</error>));
        Object mockInterfaceMultiple = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class, Set.class, Map.class));
        Object mockNotInterfaceOfMultiple = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(List.class, Set.class, <error descr="Mockitools: The referenced type is not an interface. MockSettings#extraInterfaces() must be provided only interface types.">Object.class</error>));
        Object mockNotInterfaceAtAll = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces(<error descr="Mockitools: The referenced type is not an interface. MockSettings#extraInterfaces() must be provided only interface types.">Object.class</error>, <error descr="Mockitools: The referenced type is not an interface. MockSettings#extraInterfaces() must be provided only interface types.">ArrayList.class</error>, <error descr="Mockitools: The referenced type is not an interface. MockSettings#extraInterfaces() must be provided only interface types.">AbstractMap.class</error>));
        Object mockInterfaceNoArg = Mockito.mock(Object.class, Mockito.withSettings().extraInterfaces());
    }
}
