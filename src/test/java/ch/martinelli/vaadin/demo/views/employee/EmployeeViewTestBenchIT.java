package ch.martinelli.vaadin.demo.views.employee;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.flow.component.textfield.testbench.PasswordFieldElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.TestBenchTestCase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.assertEquals;

public class EmployeeViewTestBenchIT extends TestBenchTestCase {

    @Before
    public void setup() {
        WebDriverManager.chromedriver().setup();

        setDriver(new ChromeDriver());
        getDriver().get("http://localhost:8080/employees/");

        $(TextFieldElement.class).id("vaadinLoginUsername").setValue("admin");
        $(PasswordFieldElement.class).id("vaadinLoginPassword").setValue("admin");
        $(ButtonElement.class).first().click();
    }

    @Test
    public void check_grid_content() {
        GridElement grid = $(GridElement.class).id("employee-grid");

        assertEquals(2, grid.getRowCount());

        GridTHTDElement cell = grid.getCell(0, 1);
        assertEquals("Hermione Compton", cell.getText());
    }

    @After
    public void tearDown() {
        getDriver().quit();
    }

}
