package ch.martinelli.vaadin.demo.views.employee;

import ch.martinelli.vaadin.demo.db.tables.records.DepartmentRecord;
import ch.martinelli.vaadin.demo.db.tables.records.EmployeeRecord;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import static ch.martinelli.vaadin.demo.db.tables.Department.DEPARTMENT;


@UIScope
@SpringComponent
public class EmployeeForm extends FormLayout {

    private final DSLContext ctx;
    private final TransactionTemplate transactionTemplate;

    private final Binder<EmployeeRecord> binder = new Binder<>(EmployeeRecord.class);
    private final TextField lastName;
    private final Button save;
    private final Button delete;
    private EmployeeRecord employee;

    private ChangeHandler changeHandler;

    public EmployeeForm(DSLContext ctx, TransactionTemplate transactionTemplate) {
        this.ctx = ctx;

        setClassName("editor");
        setResponsiveSteps(new ResponsiveStep("0", 1));

        this.transactionTemplate = transactionTemplate;

        var id = new TextField("Id");
        binder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter(0, "Integers only"))
                .bind(EmployeeRecord::getId, null);

        lastName = new TextField("Last Name");
        binder.forField(lastName)
                .asRequired()
                .bind(EmployeeRecord::getLastName, EmployeeRecord::setLastName);

        var firstName = new TextField("First Name");
        binder.forField(firstName)
                .asRequired()
                .bind(EmployeeRecord::getFirstName, EmployeeRecord::setFirstName);

        var dateOfBirth = new DatePicker("Date of Birth");
        binder.forField(dateOfBirth)
                .asRequired()
                .bind(EmployeeRecord::getDateOfBirth, EmployeeRecord::setDateOfBirth);

        var salary = new TextField("Salary");
        binder.forField(salary)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter(0, "Must be a number"))
                .withValidator((value, context) -> value == null || value > 0
                        ? ValidationResult.ok()
                        : ValidationResult.error("Number must be greater than 0"))
                .bind(EmployeeRecord::getSalary, EmployeeRecord::setSalary);

        var department = new ComboBox<DepartmentRecord>("Department");
        department.setItemLabelGenerator(DepartmentRecord::getName);
        binder.forField(department)
                .asRequired()
                .withConverter(new Converter<DepartmentRecord, Integer>() {
                    @Override
                    public Result<Integer> convertToModel(DepartmentRecord departmentRecord, ValueContext valueContext) {
                        if (departmentRecord == null) {
                            return Result.ok(null);
                        } else {
                            return Result.ok(departmentRecord.getId());
                        }
                    }

                    @Override
                    public DepartmentRecord convertToPresentation(Integer id, ValueContext valueContext) {
                        return ctx.selectFrom(DEPARTMENT).where(DEPARTMENT.ID.eq(id)).fetchOptional().orElse(new DepartmentRecord(null, ""));
                    }
                })
                .bind(EmployeeRecord::getDepartmentId, EmployeeRecord::setDepartmentId);

        department.setItems(ctx.selectFrom(DEPARTMENT).orderBy(DEPARTMENT.NAME).fetch());

        save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setEnabled(false);
        save.addClickListener(this::save);

        delete = new Button("Delete");
        delete.setEnabled(false);
        delete.addClickListener(this::delete);

        add(id, lastName, firstName, dateOfBirth, salary, department, new HorizontalLayout(save, delete));
    }

    public void setEmployee(EmployeeRecord employee) {
        this.employee = employee;
        binder.setBean(employee);

        lastName.focus();

        save.setEnabled(true);
        delete.setEnabled(true);
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    private void save(ClickEvent<Button> event) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            if (binder.validate().isOk()) {
                ctx.attach(employee);
                employee.store();

                save.setEnabled(false);
                delete.setEnabled(false);

                binder.setBean(null);

                changeHandler.onChange();
            }
        });
    }

    private void delete(ClickEvent<Button> event) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            ctx.attach(employee);
            employee.delete();

            save.setEnabled(false);
            delete.setEnabled(false);

            binder.setBean(null);

            changeHandler.onChange();
        });
    }

    public interface ChangeHandler {
        void onChange();
    }
}
