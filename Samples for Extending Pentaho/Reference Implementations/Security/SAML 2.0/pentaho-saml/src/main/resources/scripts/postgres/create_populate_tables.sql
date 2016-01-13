CREATE TABLE USERS
(
  username character varying(50) NOT NULL,
  password character varying(50),
  enabled character varying(5) NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (username)
);
ALTER TABLE USERS OWNER TO hibuser;


CREATE TABLE AUTHORITIES
(
  authority character varying(50) NOT NULL,
  CONSTRAINT authorities_pkey PRIMARY KEY (authority)
);
ALTER TABLE AUTHORITIES OWNER TO hibuser;

CREATE TABLE GRANTED_AUTHORITIES
(
  username character varying(50) NOT NULL,
  authority character varying(50) NOT NULL,
  CONSTRAINT granted_authorities_pkey PRIMARY KEY (username,authority)
);
ALTER TABLE GRANTED_AUTHORITIES OWNER TO hibuser;


INSERT INTO USERS(username, password, enabled) VALUES ('admin', 'ignored', 'true');
-- ADD YOUR USER HERE ( password field is not relevant )

INSERT INTO AUTHORITIES(authority) VALUES ('Administrator');
INSERT INTO AUTHORITIES(authority) VALUES ('Authenticated');
INSERT INTO AUTHORITIES(authority) VALUES ('Power User');
INSERT INTO AUTHORITIES(authority) VALUES ('Report Author');
INSERT INTO AUTHORITIES(authority) VALUES ('Business Analyst');
INSERT INTO AUTHORITIES(authority) VALUES ('ceo');
INSERT INTO AUTHORITIES(authority) VALUES ('cto');
INSERT INTO AUTHORITIES(authority) VALUES ('dev');

INSERT INTO GRANTED_AUTHORITIES(username,authority) VALUES ('admin','Administrator');
INSERT INTO GRANTED_AUTHORITIES(username,authority) VALUES ('admin','Authenticated');
-- ADD YOUR USER/ROLE MAP HERE
