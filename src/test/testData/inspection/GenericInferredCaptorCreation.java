import org.mockito.ArgumentCaptor;

class GenericInferredCaptorCreation {

    void noHighlights() {
        ArgumentCaptor<String> aCaptor = ArgumentCaptor.captor();
    }

    void highlights() {
        ArgumentCaptor<String> aCaptor = ArgumentCaptor.captor(<error descr="This type of ArgumentCaptor creation must not have any value passed in.">"string"</error>);
        ArgumentCaptor<String> anotherCaptor = ArgumentCaptor.captor(<error descr="This type of ArgumentCaptor creation must not have any value passed in.">"some", "string"</error>);
    }
}
