DROP TRIGGER T_UNKNOWNPKVC1;
DROP TRIGGER T_UNKNOWNPKVC2;

DROP TABLE UNKNOWNPKVC1;
DROP TABLE UNKNOWNPKVC2;

CREATE TABLE UNKNOWNPKVC1
(
    ID     NUMERIC(30)    PRIMARY KEY NOT NULL,
    NAME   VARCHAR(32),
    VERSION NUMERIC(19)    NOT NULL
);

CREATE TABLE UNKNOWNPKVC2
(
    ID     NUMERIC(30)      PRIMARY KEY NOT NULL,
    NAME   VARCHAR(32),
    VERSION NUMERIC(19)    NOT NULL
);

commit;

CREATE TRIGGER T_UNKNOWNPKVC1
   NO CASCADE
   BEFORE UPDATE ON UNKNOWNPKVC1
   REFERENCING
      NEW AS N_ROW
      OLD AS O_ROW
   FOR EACH ROW MODE DB2SQL
   WHEN (N_ROW.VERSION = O_ROW.VERSION)
      SET N_ROW.VERSION = O_ROW.VERSION + 1
;

CREATE TRIGGER T_UNKNOWNPKVC2
   NO CASCADE
   BEFORE UPDATE ON UNKNOWNPKVC2
   REFERENCING
      NEW AS N_ROW
      OLD AS O_ROW
   FOR EACH ROW MODE DB2SQL
   WHEN (N_ROW.VERSION = O_ROW.VERSION)
      SET N_ROW.VERSION = O_ROW.VERSION + 1
;

commit;

quit;
