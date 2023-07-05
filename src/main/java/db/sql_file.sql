create database thiraka_suwa_sewa;
use thiraka_suwa_sewa;


create table admin
(
    name         varchar(100) not null
        primary key,
    password     varchar(50)  null,
    phone_number varchar(14)  null,
    gmail        varchar(100) null
);

INSERT INTO admin (name, password, phone_number, gmail)
VALUES ('Bogaha Waththa Doctor', '123', '0000000000', 'admin@gmail.com');

create table doctor
(
    doctorId    varchar(100)   not null
        primary key,
    mr_ms       varchar(10)    null,
    d_name      varchar(100)   null,
    gen         varchar(80)    null,
    free        decimal(10, 2) null,
    phoneNumber varchar(14)    null,
    gmail       varchar(80)    null
);

create table employee
(
    employee_id   varchar(50)                                    not null
        primary key,
    name          varchar(100)                                   null,
    phone_number  varchar(10)                                    null,
    register_date date                                           null,
    position      enum ('Admin', 'Cashier', 'Manager', 'Worker') null
);

INSERT INTO employee (employee_id, name, phone_number, register_date, position)
VALUES ('123', ' Doe', '0787969637', '2023-06-10', 'Manager');

create table items
(
    item_code        varchar(50)    not null
        primary key,
    item_description varchar(100)   null,
    qty              int            null,
    selling_price    decimal(10, 2) null,
    Purchase_price   decimal(10, 2) null,
    supplier_name    varchar(50)    null,
    expiration_date  date           null
);

create table orders
(
    order_Id   varchar(50)    not null
        primary key,
    tot_qty    varchar(200)   null,
    order_date date           null,
    item_cost  decimal(10, 2) null,
    profit     decimal(10, 2) null
);

create table orderdetails
(
    order_Id      varchar(50)    not null,
    item_code     varchar(50)    not null,
    name          varchar(100)   null,
    qty           int            null,
    selling_price decimal(10, 2) null,
    net_tot       decimal(10, 2) null,
    primary key (order_Id, item_code),
    constraint orderdetails_ibfk_1
        foreign key (order_Id) references orders (order_Id)
            on update cascade on delete cascade,
    constraint orderdetails_ibfk_2
        foreign key (item_code) references items (item_code)
            on update cascade on delete cascade
);

create table patient
(
    patientIdNumber varchar(50)  not null
        primary key,
    mr_ms           varchar(80)  null,
    patientName     varchar(100) null,
    age             int          null,
    phoneNumber     varchar(14)  null,
    gen             varchar(100) null,
    address         varchar(100) null
);

create table timeperiod
(
    no       varchar(100) not null
        primary key,
    doctorId varchar(100) null,
    d_name   varchar(100) null,
    day      varchar(50)  null,
    fromTime time         null,
    toTime   text         null,
    constraint timeperiod_ibfk_1
        foreign key (doctorId) references doctor (doctorId)
);

create table appointnment
(
    id              varchar(200)   not null,
    number          varchar(200)   null,
    patientIdNumber varchar(50)    not null,
    doutor_name     varchar(100)   null,
    doctor_free     decimal(10, 2) null,
    patient_Name    varchar(100)   null,
    hospital_free   decimal(10, 2) null,
    tot_amount      decimal(10, 2) null,
    chash_paod      decimal(10, 2) null,
    date            date           null,
    primary key (patientIdNumber, id),
    constraint appointnment_ibfk_1
        foreign key (patientIdNumber) references patient (patientIdNumber)
            on update cascade on delete cascade
);
