insert into
    users (first_name, last_name, date_of_birth, gender, balance, email, password)
values
    ('Dominik', 'Wątor', '1999-08-11', 'M', '612.56', 'admin@example.com', '{noop}adminpass'),
    ('Jan', 'Kowalski', '1995-12-19', 'M', '23501.00', 'user@example.com', '{noop}userpass'),
    ('Adam', 'Nowak', '2005-01-01', 'F', '18.43', 'adam@example.com', '{noop}adampass');

insert into
    user_role (name, description)
values
    ('ADMIN', 'pełne uprawnienia'),
    ('USER', 'podstawowe uprawnienia, zalogowany użytkownik');

insert into
    user_roles (user_id, role_id)
values
    (1, 1),
    (2, 2),
    (3, 2);