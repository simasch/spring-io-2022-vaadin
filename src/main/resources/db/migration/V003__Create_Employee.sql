create sequence department_seq;

create table department
(
    id   integer      not null default nextval('security_group_seq') primary key,
    name varchar(255) not null
);

create sequence employee_seq;

create table employee
(
    id            integer      not null default nextval('security_group_seq') primary key,
    name          varchar(255) not null,
    salary        integer,
    department_id integer      not null,

    foreign key (department_id) references department (id)
);

create view v_employee as
select e.id as employee_id, e.name as employee_name, d.name as department_name
from employee e
         join department d on e.department_id = d.id;
