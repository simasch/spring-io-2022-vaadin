package ch.martinelli.vaadin.demo.views.helloworld;

import ch.martinelli.vaadin.demo.views.KaribuTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldViewKaribuIT extends KaribuTest {

    @Test
    void say_hello() {
        _get(TextField.class, spec -> spec.withId("name")).setValue("World");
        _get(Button.class, spec -> spec.withId("say-hello")).click();

        Label label = _get(Label.class, spec -> spec.withId("text"));

        assertThat(label.getText()).isEqualTo("Hello World");
    }

}
