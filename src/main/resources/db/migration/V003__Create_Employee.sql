create sequence department_seq;

create table department
(
    id   integer not null default nextval('security_group_seq') primary key,
    name varchar not null
);

create sequence employee_seq;

create table employee
(
    id            integer not null default nextval('security_group_seq') primary key,
    first_name    varchar not null,
    last_name     varchar not null,
    date_of_birth date    not null,
    salary        integer,
    department_id integer not null,

    foreign key (department_id) references department (id)
);

create view v_employee as
select e.id as employee_id, e.first_name as employee_first_name, e.last_name as employee_last_name, d.name as department_name
from employee e
         join department d on e.department_id = d.id;
