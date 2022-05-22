package ch.martinelli.vaadin.demo.views.employee;

import ch.martinelli.vaadin.demo.db.tables.records.VEmployeeRecord;
import ch.martinelli.vaadin.demo.views.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class EmployeeViewKaribuIT extends KaribuTest {

    @BeforeEach
    void login() {
        login("admin", "", List.of("ADMIN"));

        UI.getCurrent().navigate(EmployeeView.class);
    }

    @Test
    void check_grid_content() {
        Grid<VEmployeeRecord> athletesGrid = _get(Grid.class, spec -> spec.withId("employee-grid"));

        assertThat(GridKt._size(athletesGrid)).isEqualTo(2);
        assertThat(GridKt._get(athletesGrid, 0).getEmployeeName()).isEqualTo("Hermione Compton");
    }

}
