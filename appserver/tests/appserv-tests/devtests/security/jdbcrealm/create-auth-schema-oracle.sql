CREATE TABLE USER_TABLE (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_BASE64 (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_HEX (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);



CREATE TABLE USER_TABLE_MD2_BASE64 (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_MD5_BASE64 (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_SHA_BASE64 (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_SHA256_BASE64 (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_SHA384_BASE64 (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);
CREATE TABLE USER_TABLE_SHA512_BASE64 (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);



CREATE TABLE USER_TABLE_MD2_HEX (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_MD5_HEX (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_SHA_HEX (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_SHA256_HEX (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_SHA384_HEX (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);

CREATE TABLE USER_TABLE_SHA512_HEX (
        USERNAME VARCHAR (255) PRIMARY KEY NOT NULL,
        PASSWORD VARCHAR (255)
);



CREATE TABLE GROUP_TABLE (
        USERNAME VARCHAR (255) NOT NULL,
        GROUPNAME VARCHAR (255)
);
