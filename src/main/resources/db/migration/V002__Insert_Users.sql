-- Default roles
insert into security_group (id, name)
values (1, 'ADMIN');
insert into security_group (id, name)
values (2, 'USER');

-- Default users
insert into security_user (id, username, secret)
values (1, 'admin', '$2a$12$ETfnTJmtlEFE8cyVAF9h9uP.qtOWB2Wcy5ubJqM33Ot3vhNZQhz7S');
insert into security_user (id, username, secret)
values (2, 'user', '$2a$12$aaoDwzBy8f3NKl02wuD7XeXhedlMieVVjPrjrZ0yq8bJBcSMC9RY.');

insert into user_group (user_id, group_id)
values (1, 1);
insert into user_group (user_id, group_id)
values (2, 2);
