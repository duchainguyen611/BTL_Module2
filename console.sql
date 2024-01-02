create database WAREHOUSE_MANAGEMENT;
use WAREHOUSE_MANAGEMENT;

create table product
(
    product_id     char(5) primary key,
    product_name   varchar(150) not null unique,
    manufacturer   varchar(200) not null,
    created        date default (current_date),
    batch          smallint     not null,
    quantity       int  default 0,
    product_status bit  default 1
);

create table employee
(
    emp_id        char(5) primary key,
    emp_name      varchar(100) not null unique,
    birth_of_date date,
    email         varchar(100) not null,
    phone         varchar(100) not null,
    address       text         not null,
    emp_status    smallint     not null
);

create table account
(
    acc_id     int primary key auto_increment,
    user_name  varchar(30) not null unique,
    password   varchar(30) not null,
    permission bit default 1,
    emp_id     char(5)     not null unique,
    acc_status bit default 1,
    foreign key (emp_id) references employee (emp_id)
);

create table bill
(
    bill_id        int primary key auto_increment,
    bill_code      varchar(10) not null,
    bill_type      bit         not null,
    emp_id_created char(5)     not null,
    created        date,
    emp_id_auth    char(5),
    auth_date      date,
    bill_status    smallint default 0,
    foreign key (emp_id_created) references employee (emp_id),
    foreign key (emp_id_auth) references employee (emp_id)
);

create table bill_detail
(
    bill_detail_Id int primary key auto_increment,
    bill_id        int     not null,
    product_id     char(5) not null,
    quantity       int     not null check (quantity > 0),
    price          float   not null check ( price > 0 ),
    foreign key (bill_id) references bill (bill_id),
    foreign key (product_id) references product (product_id)
);


insert into account(user_name, password, permission, emp_id)
values ('admin', '123456', 0, 'E01'),
       ('user01', '123456', 1, 'E02');


-- procedure login
DELIMITER &&
create procedure log_in_account(userNameLogIn varchar(30), passWordLogIn varchar(30), out check_acc int)
begin
    set check_acc = (select count(acc_id) from account where user_name = userNameLogIn and password = passWordLogIn);
end &&

# DELIMITER &&
# create procedure check_status_account(userNameLogIn varchar(30))
# begin
#     select acc_status from account where user_name like userNameLogIn;
# end
&&
#
# DELIMITER
&&
# create procedure check_permission_account(userNameLogIn varchar(30))
# begin
#     select permission from account where user_name like userNameLogIn;
# end
&&

DELIMITER &&
create procedure get_infor_account(userNameLogIn varchar(30))
begin
    select * from account where user_name like userNameLogIn;
end &&

-- Procedure Product
DELIMITER &&
create PROCEDURE get_all_infor_product()
begin
    select * from product limit 10;
end &&

DELIMITER &&
create procedure add_infor_product(
    product_id_add char(5),
    product_name_add varchar(150),
    manufacturer_add varchar(200),
    batch_add smallint
)
begin
    insert into product(product_id, product_name, manufacturer, batch)
    values (product_id_add, product_name_add, manufacturer_add, batch_add);
end &&

DELIMITER &&
create procedure check_product_by_id(id_product_get varchar(5), out cnt_product int)
BEGIN
    set cnt_product = (select count(product_id) from product where product_id like id_product_get);
end &&

DELIMITER &&
create PROCEDURE update_infor_product(
    product_id_update char(5),
    product_name_update varchar(150),
    manufacturer_update varchar(200),
    created_update date,
    batch_update smallint
)
begin
    update product
    set product_name = product_name_update,
        manufacturer = manufacturer_update,
        created      = created_update,
        batch        = batch_update
    where product_id = product_id_update;
end &&

DELIMITER &&
create procedure get_infor_to_update_product(
    product_id_update char(5)
)
begin
    select product_name, manufacturer, created, batch from product where product_id like product_id_update;
end &&

DELIMITER &&
create procedure check_product_by_name(name_product_check varchar(150), out cnt_product int)
BEGIN
    set cnt_product =
            (select count(product_id) from product where product_name like concat('%', name_product_check, '%'));
end &&

DELIMITER &&
create procedure get_product_by_name(name_product_get varchar(30))
BEGIN
    select * from product where product_name like concat('%', name_product_get, '%') limit 10;
end &&

DELIMITER &&
create PROCEDURE update_status_product(
    product_id_update char(5),
    product_status_update bit
)
begin
    update product
    set product_status = product_status_update
    where product_id = product_id_update;
end &&

DELIMITER &&
create procedure get_product_name_by_product_id(
    input_product_id char(5)
)
begin
    select product_name from product where product_id = input_product_id;
end &&

-- Procedure Employee
DELIMITER &&
create PROCEDURE get_all_infor_employee()
begin
    select * from employee order by emp_name limit 10;
end &&

DELIMITER &&
create procedure add_infor_employee(
    emp_id_add char(5),
    emp_name_add varchar(100),
    birth_of_date_add date,
    email_add varchar(100),
    phone_add varchar(100),
    address_add text,
    emp_status_add smallint
)
begin
    insert into employee
    values (emp_id_add, emp_name_add, birth_of_date_add, email_add, phone_add, address_add, emp_status_add);
end &&

DELIMITER &&
create procedure check_employee_by_id(id_employee_get varchar(5), out cnt_employee int)
BEGIN
    set cnt_employee = (select count(emp_id) from employee where emp_id like id_employee_get);
end &&

DELIMITER &&
create procedure check_employee_by_name(name_employee_get varchar(30), out cnt_employee int)
BEGIN
    set cnt_employee = (select count(emp_id) from employee where employee.emp_name like name_employee_get);
end &&

DELIMITER &&
create PROCEDURE update_infor_employee(
    emp_id_update char(5),
    emp_name_update varchar(100),
    birth_of_date_update date,
    email_update varchar(100),
    phone_update varchar(100),
    address_update text
)
begin
    update employee
    set emp_name      = emp_name_update,
        birth_of_date = birth_of_date_update,
        email         = email_update,
        phone         = phone_update,
        address       = address_update
    where emp_id = emp_id_update;
end &&

DELIMITER &&
create procedure get_infor_to_update_employee(
    employee_id_update char(5)
)
begin
    select emp_name, birth_of_date, email, phone, address from employee where emp_id like employee_id_update;
end &&

DELIMITER &&
create procedure check_employee_by_name_or_id(input_search varchar(30), out cnt_Employee int)
BEGIN
    set cnt_Employee = (select count(emp_id)
                        from employee
                        where emp_name like concat('%', input_search, '%')
                           or emp_id like concat('%', input_search, '%'));
end &&

DELIMITER &&
create procedure get_employee_by_name_or_id(input_search varchar(30))
BEGIN
    select *
    from employee
    where emp_name like concat('%', input_search, '%')
       or emp_id like concat('%', input_search, '%')
    order by emp_name
    limit 10;
end &&

DELIMITER &&
create PROCEDURE update_status_employee(
    emp_id_update char(5),
    emp_status_update smallint
)
begin
    update employee
    set emp_status = emp_status_update
    where emp_id like emp_id_update;
end &&


DELIMITER &&
create procedure get_name_employee_by_id_employee(emp_id_search char(5))
begin
    select emp_name from employee where emp_id = emp_id_search;
end &&

-- procedure account
DELIMITER &&
create PROCEDURE get_all_infor_account()
begin
    select * from account;
end &&

DELIMITER &&
create procedure add_infor_account(
    user_name_add varchar(30),
    password_add varchar(30),
    emp_id_add char(5)
)
begin
    insert into account(user_name, password, emp_id) values (user_name_add, password_add, emp_id_add);
end &&

DELIMITER &&
create procedure check_account_by_id(
    acc_id_check int,
    out cnt_acc int
)
begin
    set cnt_acc = (select count(acc_id) from account where acc_id = acc_id_check);
end &&

DELIMITER &&
create PROCEDURE update_infor_account(
    acc_id_update int,
    user_name_update varchar(30),
    password_update varchar(30),
    permission_update bit,
    emp_id_update char(5)
)
begin
    update account
    set user_name  = user_name_update,
        password   = password_update,
        permission = permission_update,
        emp_id     = emp_id_update
    where acc_id = acc_id_update;
end &&

DELIMITER &&
create procedure check_account_by_username_or_nameEmp(input_search varchar(30), out cnt_account int)
BEGIN
    set cnt_account = (select count(acc_id)
                       from account a
                                join employee e on a.emp_id = e.emp_id
                       where a.user_name like concat('%', input_search, '%')
                          or e.emp_name like concat('%', input_search, '%'));
end &&

DELIMITER &&
create procedure get_account_by_username_or_nameEmp(input_search varchar(30))
BEGIN
    select a.*
    from account a
             join employee e on a.emp_id = e.emp_id
    where a.user_name like concat('%', input_search, '%')
       or e.emp_name like concat('%', input_search, '%');
end &&

DELIMITER &&
create procedure check_account_by_and_id_username_or_nameEmp(input_search varchar(30), id_update_status int, out cnt_acc int)
BEGIN
    set cnt_acc = (select count(a.acc_id)
                   from account a
                            join employee e on a.emp_id = e.emp_id
                   where a.acc_id = id_update_status
                     and (a.user_name like concat('%', input_search, '%') or
                          e.emp_name like concat('%', input_search, '%')));
end &&

DELIMITER &&
create procedure update_status_account_by_username_or_nameEmp(input_search varchar(30), id_update_status int,
                                                              status_update_acc bit)
BEGIN
    update account a join employee e on a.emp_id = e.emp_id
    set a.acc_status = status_update_acc
    where a.acc_id = id_update_status
      and (a.user_name like concat('%', input_search, '%') or e.emp_name like concat('%', input_search, '%'));
end &&

DELIMITER &&
create PROCEDURE update_status_account(
    acc_id_update int,
    acc_status_update bit
)
begin
    update account
    set acc_status = acc_status_update
    where acc_id = acc_id_update;
end &&

DELIMITER &&
create procedure check_account_by_username(user_name_check varchar(30), out cnt_account int)
BEGIN
    set cnt_account = (select count(acc_id) from account where user_name like user_name_check);
end &&


DELIMITER  &&
create procedure get_infor_to_update_account(
    acc_id_check int
)
begin
    select user_name, password, permission, emp_id from account where acc_id = acc_id_check;
end &&

DELIMITER &&
create procedure check_emp_id_account(
    input_emp_id char(5),
    out cnt_acc int
)
begin
    set cnt_acc = (select acc_id from account where emp_id like input_emp_id);
end &&

DELIMITER &&
create procedure get_empName_in_account(
    input_emp_id char(5)
)
begin
    select e.emp_name from employee e join account a on e.emp_id=a.emp_id where a.emp_id like  input_emp_id;
end &&

-- procedure bill and bill_detail
DELIMITER &&
create PROCEDURE get_all_infor_bill(bill_type_input bit)
begin
    select * from bill where bill_type = bill_type_input;
end &&

DELIMITER &&
create procedure add_infor_bill(
    bill_code_add varchar(10),
    bill_type_add bit,
    emp_id_created_add char(5)
)
begin
    insert into bill(bill_code, bill_type, emp_id_created, created)
    values (bill_code_add, bill_type_add, emp_id_created_add, current_date);
end &&

DELIMITER &&
create procedure check_duplicate_bill_by_id_or_code(inputSearch varchar(10), bill_type_get bit, out cnt_bill int)
BEGIN
    set cnt_bill = (select count(bill_id)
                    from bill
                    where (bill_id like inputSearch or bill_code like inputSearch)
                      and bill_type = bill_type_get);
end &&

DELIMITER &&
create procedure check_duplicate_bill_by_id_or_code_empId(inputSearch varchar(10), bill_type_get bit,input_empId char(5), out cnt_bill int)
BEGIN
    set cnt_bill = (select count(bill_id)
                    from bill
                    where (bill_id like inputSearch or bill_code like inputSearch)
                      and bill_type = bill_type_get and emp_id_created = input_empId);
end &&

DELIMITER &&
create procedure get_id_to_update_by_id_or_code(inputSearch varchar(10))
BEGIN
    select bill_id from bill where bill_id like inputSearch or bill_code like inputSearch;
end &&

DELIMITER &&
create procedure get_status_bill_by_id(input_bill int)
BEGIN
    select bill_status from bill where bill_id = input_bill;
end &&

DELIMITER &&
create procedure update_infor_bill(
    Bill_id_update int,
    Emp_id_created_update char(5),
    Bill_Status_update bit
)
begin
    update bill
    set emp_id_created = Emp_id_created_update,
        bill_status    = Bill_Status_update
    where Bill_id = Bill_id_update;
end &&

DELIMITER &&
create procedure add_infor_bill_detail(
    bill_id_add int,
    product_id_add char(5),
    quantity_add int,
    price_add float
)
begin
    insert into bill_detail(bill_id, product_id, quantity, price)
    values (bill_id_add, product_id_add, quantity_add, price_add);
end &&

DELIMITER &&
create PROCEDURE get_infor_bill_by_billId(bill_id_input int)
begin
    select * from bill where bill_id = bill_id_input;
end &&

DELIMITER &&
create PROCEDURE get_infor_bill_detail(bill_id_input int)
begin
    select * from bill_detail where bill_id = bill_id_input;
end &&

DELIMITER &&
create procedure update_infor_bill_detail(
    Bill_detail_id_update int,
    Bill_id_update int,
    Quantity_update int,
    price_update float
)
begin
    update bill_detail
    set quantity = Quantity_update,
        price    = price_update
    where bill_id = Bill_id_update and bill_detail_Id = Bill_detail_id_update;
end &&

DELIMITER  &&
create procedure confirm_bill(
    bill_id_confirm int,
    emp_id_auth_confirm char(5)
)
begin
    update bill
    set bill_status = 2,
        emp_id_auth = emp_id_auth_confirm,
        auth_date   = current_date
    where bill_id = bill_id_confirm;
end &&

# DELIMITER &&
# create procedure check_bill_by_id_bill_or_code_bill(
#     input_search varchar(10),
#     bill_type_search bit,
#     cnt_bill int
# )
# begin
#     set cnt_bill = (select count(bill_id)
#                     from bill
#                     where (bill_id like concat('%',input_search,'%') or bill_code like concat('%',input_search,'%'))
#                       and bill_type = bill_type_search);
# end
&&

DELIMITER &&
CREATE PROCEDURE check_bill_by_id_bill_or_code_bill(
    IN input_search VARCHAR(10),
    IN bill_type_search BIT,
    OUT cnt_bill INT
)
BEGIN
    SELECT COUNT(bill_id)
    INTO cnt_bill
    FROM bill
    WHERE (bill_id LIKE CONCAT('%', input_search, '%') OR bill_code LIKE CONCAT('%', input_search, '%'))
      AND bill_type = bill_type_search;
END &&
DELIMITER ;

DELIMITER &&
create procedure get_bill_by_id_bill_or_code_bill(
    input_search varchar(10),
    bill_type_search varchar(10)
)
begin
    select *
    from bill
    where (bill_id like concat('%', input_search, '%') or bill_code like concat('%', input_search, '%'))
      and bill_type = bill_type_search;
end &&

DELIMITER &&
create procedure get_id_bill_by_id_bill_or_code_bill(
    input_search varchar(10),
    bill_type_search varchar(10)
)
begin
    select bill_id
    from bill
    where (bill_id like concat('%', input_search, '%') or bill_code like concat('%', input_search, '%'))
      and bill_type = bill_type_search;
end &&

DELIMITER &&
create procedure get_infor_to_update_bill(
    bill_id_update int
)
begin
    select emp_id_created, bill_status from bill where bill_id = bill_id_update;
end &&

DELIMITER &&
create procedure check_infor_to_update_bill_detail(
    bill_detail_id_update int,
    biLL_id_update int,
    out cnt_detail_bill int
)
begin
    set cnt_detail_bill = (select count(bill_detail_Id) from bill_detail where bill_detail_Id = bill_detail_id_update and bill_id = biLL_id_update);
end &&

DELIMITER &&
create procedure get_infor_to_update_bill_detail(
    bill_detail_id_update int,
    biLL_id_update int
)
begin
    select quantity, price from bill_detail where bill_detail_Id = bill_detail_id_update and bill_id = biLL_id_update;
end &&

DELIMITER &&
create procedure get_bill_status_by_id(
    bill_id_get int
)
begin
    select bill_status from bill where bill_id = bill_id_get;
end &&

DELIMITER &&
create procedure add_quantity_product(
    bill_id_update_product int
)
begin
    update product p join bill_detail bd on p.product_id = bd.product_id join bill b on bd.bill_id = b.bill_id
    set p.quantity = p.quantity + bd.quantity
    where b.bill_id = bill_id_update_product;
end &&

DELIMITER &&
create procedure minus_quantity_product(
    bill_id_update_product int
)
begin
    update product p join bill_detail bd on p.product_id = bd.product_id join bill b on bd.bill_id = b.bill_id
    set p.quantity = p.quantity - bd.quantity
    where b.bill_id = bill_id_update_product;
end &&


DELIMITER &&

DROP PROCEDURE IF EXISTS compare_quantity;

CREATE PROCEDURE compare_quantity(
    IN bill_id_input INT,
    OUT numCompare INT
)
BEGIN
    DECLARE product_quantity INT;
    DECLARE bill_detail_quantity INT;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur CURSOR FOR
        SELECT p.quantity,
               bd.quantity as bill_detail_quantity
        FROM product p
                 JOIN bill_detail bd ON p.product_id = bd.product_id
        WHERE bd.bill_id = bill_id_input;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO product_quantity, bill_detail_quantity;

        IF done THEN
            LEAVE read_loop;
        END IF;

        IF (product_quantity >= bill_detail_quantity) THEN
            SET numCompare = 1;
        ELSE
            SET numCompare = 0;
            LEAVE read_loop; -- Dừng ngay khi có sản phẩm nào không đáp ứng điều kiện
        END IF;
    END LOOP;

    CLOSE cur;

    SELECT numCompare;
END &&

DELIMITER ;



DELIMITER &&
create procedure get_status_product(
    id_product_input char(5)
)
begin
    select product_status from product where product_id like id_product_input;
end &&

DELIMITER &&
create procedure get_status_Employee(
    id_employee_input char(5)
)
begin
    select emp_status from employee where emp_id like id_employee_input;
end &&

-- Report procedure
DELIMITER &&
create procedure get_sum_money_by_day(
    input_date date,
    input_bill_type boolean,
    input_bill_status smallint,
    out sum_expense int
)
begin
    set sum_expense = (select sum(bd.quantity * bd.price)
                       from bill b
                                join bill_detail bd on b.bill_id = bd.bill_id
                       where b.auth_date = input_date
                         and b.bill_type = input_bill_type
                         and b.bill_status = input_bill_status
                       group by b.auth_date);
end &&

DELIMITER &&
create procedure get_sum_money_from_day_to_day(
    input_date1 date,
    input_date2 date,
    input_bill_type boolean,
    input_bill_status smallint,
    out sum_expense int
)
begin
    set sum_expense = (select sum(bd.quantity * bd.price)
                       from bill b
                                join bill_detail bd on b.bill_id = bd.bill_id
                       where (b.auth_date between input_date1 and input_date2)
                         and b.bill_type = input_bill_type
                       group by b.auth_date
                                    and b.bill_status = input_bill_status);
end &&

DELIMITER &&
create procedure count_employee_by_status()
begin
    select emp_status, count(emp_id) from employee group by emp_status;
end &&

DELIMITER &&
CREATE PROCEDURE statistic_max_from_day_to_day(
    input_date1 DATE,
    input_date2 DATE,
    input_bill_type BOOLEAN,
    input_bill_status SMALLINT
)
BEGIN
    SELECT p.product_name, sum(bd.quantity) as quantity
    FROM bill b
             JOIN bill_detail bd ON b.bill_id = bd.bill_id
             JOIN product p ON bd.product_id = p.product_id
    WHERE (b.auth_date BETWEEN input_date1 AND input_date2)
      AND b.bill_type = input_bill_type
      AND b.bill_status = input_bill_status
    GROUP BY p.product_id order by quantity desc limit 1;
END &&;

DELIMITER &&
CREATE PROCEDURE statistic_min_from_day_to_day(
    input_date1 DATE,
    input_date2 DATE,
    input_bill_type BOOLEAN,
    input_bill_status SMALLINT
)
BEGIN
    SELECT p.product_name, sum(bd.quantity) as quantity
    FROM bill b
             JOIN bill_detail bd ON b.bill_id = bd.bill_id
             JOIN product p ON bd.product_id = p.product_id
    WHERE (b.auth_date BETWEEN input_date1 AND input_date2)
      AND b.bill_type = input_bill_type
      AND b.bill_status = input_bill_status
    GROUP BY bd.product_id order by quantity limit 1;
END &&;

DELIMITER &&
create procedure get_bill_by_id_user(
    input_id_user char(5),
    input_bill_type boolean
)
begin
    select * from  bill where emp_id_created = input_id_user and bill_type = input_bill_type order by bill_status;
end &&

DELIMITER &&
CREATE PROCEDURE check_bill_by_id_bill_or_code_bill_and_idEmp(
    IN input_search VARCHAR(10),
    IN input_empId char(5),
    IN bill_type_search BIT,
    OUT cnt_bill INT
)
BEGIN
    SELECT COUNT(bill_id)
    INTO cnt_bill
    FROM bill
    WHERE (bill_id LIKE CONCAT('%', input_search, '%') OR bill_code LIKE CONCAT('%', input_search, '%'))
      AND bill_type = bill_type_search and emp_id_created = input_empId;
END &&
DELIMITER ;

DELIMITER &&
create procedure get_bill_by_id_bill_or_code_bill_and_idEmp(
    input_search varchar(10),
    input_empId char(5),
    bill_type_search varchar(10)
)
begin
    select *
    from bill
    where (bill_id like concat('%', input_search, '%') or bill_code like concat('%', input_search, '%'))
      and bill_type = bill_type_search and emp_id_created = input_empId;
end &&

DELIMITER &&
create procedure check_bill_code_by_bill_code(
    bill_code_input varchar(10),
    out cnt_bill int
)
begin
    set cnt_bill = (select count(bill_id) from bill where bill_code like  bill_code_input);
end &&

DELIMITER &&
create procedure get_acc_id_by_empId(
    input_empId char(5)
)
begin
    select acc_id from account where emp_id = input_empId;
end &&