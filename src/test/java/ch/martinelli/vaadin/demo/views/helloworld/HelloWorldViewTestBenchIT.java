package ch.martinelli.vaadin.demo.views.helloworld;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.html.testbench.LabelElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchTestCase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class HelloWorldViewTestBenchIT extends TestBenchTestCase {

    @Before
    public void setup() {
        WebDriverManager.chromedriver().setup();

        setDriver(new ChromeDriver());
        getDriver().get("http://localhost:8080/");
    }

    @Test
    public void say_hello() {
        $(TextFieldElement.class).id("name").setValue("World");
        $(ButtonElement.class).id("say-hello").click();

        LabelElement text = $(LabelElement.class).id("text");

        Assert.assertEquals("Hello World", text.getText());
    }

}
