insert into account_user(id, name, created_at, updated_at)
values (1, 'Pororo', now(), now());
insert into account_user(id, name, created_at, updated_at)
values (2, 'Lupi', now(), now());
insert into account_user(id, name, created_at, updated_at)
values (3, 'Eddie', now(), now());

-- application.yml에서 jpa: defer-datasource-initialization : true 설정으로 인해 이 sql문이 초기 실행됨