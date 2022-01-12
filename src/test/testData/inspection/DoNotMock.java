package pm.org.mockito;

public @interface DoNotMock {
    String cause() default "Default reason";
}
