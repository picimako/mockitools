package pm.org.mockito;

public @interface DoNotMock {
    String reason() default "Default reason";
}
