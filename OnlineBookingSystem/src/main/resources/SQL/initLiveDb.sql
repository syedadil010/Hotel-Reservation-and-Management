INSERT OR REPLACE INTO day(
  id,
  dayText
) VALUES(
  1,
  'Monday'
);

INSERT OR REPLACE INTO day(
  id,
  dayText
) VALUES(
  2,
  'Tuesday'
);

INSERT OR REPLACE INTO day(
  id,
  dayText
) VALUES(
  3,
  'Wednesday'
);

INSERT OR REPLACE INTO day(
  id,
  dayText
) VALUES(
  4,
  'Thursday'
);

INSERT OR REPLACE INTO day(
  id,
  dayText
) VALUES(
  5,
  'Friday'
);

INSERT OR REPLACE INTO day(
  id,
  dayText
) VALUES(
  7,
  'Saturday'
);

INSERT OR REPLACE INTO day(
  id,
  dayText
) VALUES(
  7,
  'Sunday'
);

INSERT INTO setup(
  area
) VALUES (
  'account'
);

INSERT INTO setup(
  area
) VALUES (
  'employee'
);

INSERT INTO setup(
  area
) VALUES (
  'services'
);

INSERT INTO setup(
  area
) VALUES (
  'openingHours'
);

INSERT INTO setup(
  area
) VALUES (
  'roster'
);

INSERT OR REPLACE INTO customer(
  id,
  username,
  password,
  firstName,
  lastName,
  email
) VALUES (
  1,
  'democu',
  'demopw',
  'john',
  'demo',
  'john@demo.com'
);