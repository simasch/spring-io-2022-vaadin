package ch.martinelli.vaadin.demo.views.employee;

import ch.martinelli.vaadin.demo.db.tables.records.EmployeeRecord;
import ch.martinelli.vaadin.demo.db.tables.records.VEmployeeRecord;
import ch.martinelli.vaadin.demo.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.annotation.security.RolesAllowed;

import static ch.martinelli.vaadin.demo.db.tables.Employee.EMPLOYEE;
import static ch.martinelli.vaadin.demo.db.tables.VEmployee.V_EMPLOYEE;
import static io.seventytwo.vaadinjooq.util.VaadinJooqUtil.orderFields;
import static org.jooq.impl.DSL.lower;

@RolesAllowed({"ADMIN", "USER"})
@PageTitle("Employees")
@Route(value = "employees", layout = MainLayout.class)
public class EmployeeView extends VerticalLayout {

    private Grid<VEmployeeRecord> grid;
    private ConfigurableFilterDataProvider<VEmployeeRecord, Void, Condition> dataProvider;
    private final TextField filter = new TextField();

    public EmployeeView(EmployeeForm employeeForm, DSLContext ctx) {
        filter.setPlaceholder("Filter by name...");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> dataProvider.setFilter(
                lower(V_EMPLOYEE.EMPLOYEE_FIRST_NAME).like("%" + e.getValue().toLowerCase() + "%")
                        .or(lower(V_EMPLOYEE.EMPLOYEE_LAST_NAME).like("%" + e.getValue().toLowerCase() + "%"))));

        var clear = new Button("Clear filter");
        clear.addClickListener(e -> filter.clear());

        var add = new Button("Add new employee");
        add.addClickListener(e -> {
            grid.asSingleSelect().clear();
            employeeForm.setEmployee(new EmployeeRecord());
        });

        var toolbar = new HorizontalLayout(filter, clear, add);
        toolbar.setWidthFull();

        grid = new Grid<>();
        grid.setId("employee-grid");
        grid.addColumn(VEmployeeRecord::getEmployeeId)
                .setHeader("ID")
                .setSortable(true)
                .setSortProperty(V_EMPLOYEE.EMPLOYEE_ID.getName());
        grid.addColumn(VEmployeeRecord::getEmployeeFirstName)
                .setHeader("Last Name")
                .setSortable(true)
                .setSortProperty(V_EMPLOYEE.EMPLOYEE_LAST_NAME.getName());
        grid.addColumn(VEmployeeRecord::getEmployeeFirstName)
                .setHeader("First Name")
                .setSortable(true)
                .setSortProperty(V_EMPLOYEE.EMPLOYEE_FIRST_NAME.getName());
        grid.addColumn(VEmployeeRecord::getDepartmentName)
                .setHeader("Department")
                .setSortable(true)
                .setSortProperty(V_EMPLOYEE.DEPARTMENT_NAME.getName());

        grid.setSizeFull();

        dataProvider = new CallbackDataProvider<VEmployeeRecord, Condition>(
                query -> ctx
                        .selectFrom(V_EMPLOYEE)
                        .where(query.getFilter().orElse(DSL.noCondition()))
                        .orderBy(orderFields(V_EMPLOYEE, query))
                        .offset(query.getOffset())
                        .limit(query.getLimit())
                        .fetchStream(),
                query -> ctx
                        .selectCount()
                        .from(V_EMPLOYEE)
                        .where(query.getFilter().orElse(DSL.noCondition()))
                        .fetchOneInto(Integer.class),
                VEmployeeRecord::getEmployeeId)
                .withConfigurableFilter();

        grid.setItems(dataProvider);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                EmployeeRecord employee = ctx
                        .selectFrom(EMPLOYEE)
                        .where(EMPLOYEE.ID.eq(event.getValue().getEmployeeId()))
                        .fetchOne();
                employeeForm.setEmployee(employee);
            }
        });

        employeeForm.setChangeHandler(() -> {
            dataProvider.refreshAll();
            grid.select(null);
        });

        var main = new SplitLayout(grid, employeeForm);
        main.setSplitterPosition(70);
        main.setId("main-split-layout");
        main.setSizeFull();

        add(toolbar, main);

        setSizeFull();
    }

}
