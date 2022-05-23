package ch.martinelli.vaadin.demo.views.employee;

import ch.martinelli.vaadin.demo.db.tables.records.VEmployeeRecord;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static ch.martinelli.vaadin.demo.db.tables.VEmployee.V_EMPLOYEE;
import static io.seventytwo.vaadinjooq.util.VaadinJooqUtil.orderFields;

public class EmployeeGrid extends Grid<VEmployeeRecord> {

    private final ConfigurableFilterDataProvider<VEmployeeRecord, Void, Condition> dataProvider;

    public EmployeeGrid(DSLContext ctx) {
        setId("employee-grid");
        setSizeFull();

        addColumn(VEmployeeRecord::getEmployeeId)
                .setHeader("ID")
                .setSortable(true)
                .setSortProperty(V_EMPLOYEE.EMPLOYEE_ID.getName());
        addColumn(VEmployeeRecord::getEmployeeLastName)
                .setHeader("Last Name")
                .setSortable(true)
                .setSortProperty(V_EMPLOYEE.EMPLOYEE_LAST_NAME.getName());
        addColumn(VEmployeeRecord::getEmployeeFirstName)
                .setHeader("First Name")
                .setSortable(true)
                .setSortProperty(V_EMPLOYEE.EMPLOYEE_FIRST_NAME.getName());
        addColumn(VEmployeeRecord::getDepartmentName)
                .setHeader("Department")
                .setSortable(true)
                .setSortProperty(V_EMPLOYEE.DEPARTMENT_NAME.getName());

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

        setItems(dataProvider);
    }

    public void refreshAll() {
        select(null);
        dataProvider.refreshAll();
    }

    public void filter(Condition condition) {
        dataProvider.setFilter(condition);
    }
}
