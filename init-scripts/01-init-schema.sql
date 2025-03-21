-- 계좌 테이블 생성
create table if not exists accountReads
(
    id             binary(16)                              not null primary key,
    account_number varchar(50)                             not null,
    account_name   varchar(100)                            not null,
    balance        decimal(19, 4)                          not null,
    status         varchar(20)                             not null,
    created_at     timestamp default current_timestamp()   not null on update current_timestamp(),
    updated_at     timestamp default '0000-00-00 00:00:00' not null,
    constraint account_number
        unique (account_number)
);

create index if not exists idx_account_number
    on accountReads (account_number);



-- 트랜잭션 테이블 생성
create table if not exists transactionReads
(
    id                binary(16)                              not null primary key,
    source_account_id binary(16)                              null,
    target_account_id binary(16)                              null,
    amount            decimal(19, 4)                          not null,
    type              varchar(20)                             not null,
    status            varchar(20)                             not null,
    description       varchar(255)                            null,
    created_at        timestamp default current_timestamp()   not null on update current_timestamp(),
    updated_at        timestamp default '0000-00-00 00:00:00' not null,
    constraint transactions_ibfk_1
        foreign key (source_account_id) references accountReads (id),
    constraint transactions_ibfk_2
        foreign key (target_account_id) references accountReads (id)
);

create index if not exists idx_source_account_id
    on transactionReads (source_account_id);

create index if not exists idx_target_account_id
    on transactionReads (target_account_id);

create index if not exists idx_transaction_status
    on transactionReads (status);

create index if not exists idx_transaction_type
    on transactionReads (type);
