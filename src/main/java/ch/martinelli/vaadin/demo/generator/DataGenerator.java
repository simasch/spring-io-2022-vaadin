package ch.martinelli.vaadin.demo.generator;

import ch.martinelli.vaadin.demo.db.tables.Employee;
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

            var hermioneCompton = ctx.newRecord(Employee.EMPLOYEE);
            hermioneCompton.setName("Hermione Compton");
            hermioneCompton.setDepartmentId(itDepartment.getId());
            hermioneCompton.store();

            var lysandraStevens = ctx.newRecord(Employee.EMPLOYEE);
            lysandraStevens.setName("Lysandra Stevens");
            lysandraStevens.setDepartmentId(itDepartment.getId());
            lysandraStevens.store();
        }
    }
}
