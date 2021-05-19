drop table LINEITEM;
drop table ORDERS;
drop table VENDOR_PART;
drop table VENDOR;
drop table PART_DETAIL;
drop table PART;

create table PART (
    PART_NUMBER        VARCHAR(15)    NOT NULL,
    REVISION        NUMERIC(2)      NOT NULL,
    DESCRIPTION        VARCHAR(255),
    REVISION_DATE        TIMESTAMP       NOT NULL,
    BOM_PART_NUMBER         VARCHAR(15),
    BOM_REVISION        NUMERIC(2),
    PRIMARY KEY (PART_NUMBER, REVISION)
);

alter table PART
    add CONSTRAINT FK_1 FOREIGN KEY (BOM_PART_NUMBER, BOM_REVISION)
                REFERENCES PART (PART_NUMBER, REVISION);

create table PART_DETAIL (
    PART_NUMBER    VARCHAR(15)    NOT NULL,
    REVISION    NUMERIC(2)      NOT NULL,
    SPECIFICATION    CLOB(10K),
    DRAWING        BLOB(10K),
    PRIMARY KEY (PART_NUMBER, REVISION)
);

create table VENDOR (
    VENDOR_ID       INTEGER            NOT NULL PRIMARY KEY,
    NAME        VARCHAR(30)    NOT NULL,
    ADDRESS        VARCHAR(255)    NOT NULL,
    CONTACT        VARCHAR(255)    NOT NULL,
    PHONE        VARCHAR(30)    NOT NULL
);

create table VENDOR_PART (
    VENDOR_PART_NUMBER    BIGINT   NOT NULL               PRIMARY KEY,
    DESCRIPTION        VARCHAR(255),
    PRICE            DOUBLE PRECISION        NOT NULL,
    VENDOR_ID        INTEGER                 NOT NULL,
    PART_NUMBER        VARCHAR(15)        NOT NULL,
    PART_REVISION        NUMERIC(2)              NOT NULL,
    CONSTRAINT FK_2 FOREIGN KEY (VENDOR_ID) REFERENCES VENDOR (VENDOR_ID),
    CONSTRAINT FK_3 FOREIGN KEY (PART_NUMBER, PART_REVISION) REFERENCES PART (PART_NUMBER, REVISION),
    UNIQUE (PART_NUMBER, PART_REVISION)
);

create table ORDERS (
    ORDER_ID    INTEGER    NOT NULL  PRIMARY KEY,
    STATUS        CHAR(1)        NOT NULL,
    LAST_UPDATE    TIMESTAMP       NOT NULL,
    DISCOUNT    NUMERIC(2)      NOT NULL,
    SHIPMENT_INFO    VARCHAR(255)
);

create table LINEITEM (
    ORDER_ID        INTEGER         NOT NULL,
    ITEM_ID            NUMERIC(3)      NOT NULL,
    QUANTITY        NUMERIC(3)      NOT NULL,
    VENDOR_PART_NUMBER    BIGINT          NOT NULL,
    CONSTRAINT FK_4 FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ORDER_ID),
    CONSTRAINT FK_5 FOREIGN KEY (VENDOR_PART_NUMBER) REFERENCES VENDOR_PART (VENDOR_PART_NUMBER),
    PRIMARY KEY (ORDER_ID, ITEM_ID)
);

