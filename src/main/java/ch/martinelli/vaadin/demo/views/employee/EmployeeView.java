package ch.martinelli.vaadin.demo.views.employee;

import ch.martinelli.vaadin.demo.db.tables.records.EmployeeRecord;
import ch.martinelli.vaadin.demo.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.security.RolesAllowed;

import static ch.martinelli.vaadin.demo.db.tables.Employee.EMPLOYEE;
import static ch.martinelli.vaadin.demo.db.tables.VEmployee.V_EMPLOYEE;
import static org.jooq.impl.DSL.lower;

@RolesAllowed({"ADMIN", "USER"})
@PageTitle("Employees")
@Route(value = "employees", layout = MainLayout.class)
public class EmployeeView extends VerticalLayout {

    private final TextField filter = new TextField();
    private final EmployeeGrid grid;

    public EmployeeView(DSLContext ctx, TransactionTemplate trx) {

        EmployeeForm employeeForm = new EmployeeForm(ctx, trx);

        HorizontalLayout toolbar = createToolbar(employeeForm);

        grid = new EmployeeGrid(ctx);
        grid.addSelectionListener(event ->
                event.getFirstSelectedItem().ifPresent(vEmployeeRecord -> {
                    EmployeeRecord employee = ctx
                            .selectFrom(EMPLOYEE)
                            .where(EMPLOYEE.ID.eq(vEmployeeRecord.getEmployeeId()))
                            .fetchOne();
                    employeeForm.setEmployee(employee);
                }));

        employeeForm.setChangeHandler(grid::refreshAll);

        var main = new SplitLayout(grid, employeeForm);
        main.setSplitterPosition(70);
        main.setId("main-split-layout");
        main.setSizeFull();

        add(toolbar, main);

        setSizeFull();
    }

    private HorizontalLayout createToolbar(EmployeeForm employeeForm) {
        filter.setPlaceholder("Filter by name...");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> grid.filter(
                lower(V_EMPLOYEE.EMPLOYEE_FIRST_NAME).like("%" + e.getValue().toLowerCase() + "%")
                        .or(lower(V_EMPLOYEE.EMPLOYEE_LAST_NAME).like("%" + e.getValue().toLowerCase() + "%"))));

        var clear = new Button("Clear filter");
        clear.addClickListener(e -> filter.clear());

        var add = new Button("Add new employee");
        add.addClickListener(e -> {
            grid.select(null);
            employeeForm.setEmployee(new EmployeeRecord());
        });

        var toolbar = new HorizontalLayout(filter, clear, add);
        toolbar.setWidthFull();
        return toolbar;
    }

}
