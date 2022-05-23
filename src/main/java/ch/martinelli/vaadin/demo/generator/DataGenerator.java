package ch.martinelli.vaadin.demo.generator;

import ch.martinelli.vaadin.demo.db.tables.Employee;
import com.devskiller.jfairy.Fairy;
import com.devskiller.jfairy.producer.person.Person;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static ch.martinelli.vaadin.demo.db.tables.Department.DEPARTMENT;

@Component
public class DataGenerator {

    private final DSLContext ctx;

    public DataGenerator(DSLContext ctx) {
        this.ctx = ctx;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void insertData() {
        if (ctx.selectFrom(DEPARTMENT).where(DEPARTMENT.NAME.eq("IT")).fetchOne() == null) {
            var itDepartment = ctx.newRecord(DEPARTMENT);
            itDepartment.setName("IT");
            itDepartment.store();

            var hrDepartment = ctx.newRecord(DEPARTMENT);
            hrDepartment.setName("HR");
            hrDepartment.store();

            Fairy fairy = Fairy.create();

            for (int i = 0; i < 100; i++) {
                Person person = fairy.person();

                var employee = ctx.newRecord(Employee.EMPLOYEE);
                employee.setFirstName(person.getFirstName());
                employee.setLastName(person.getLastName());
                employee.setDateOfBirth(person.getDateOfBirth());
                employee.setDepartmentId(i % 5 == 0 ? hrDepartment.getId() : itDepartment.getId());
                employee.store();
            }
        }
    }
}
