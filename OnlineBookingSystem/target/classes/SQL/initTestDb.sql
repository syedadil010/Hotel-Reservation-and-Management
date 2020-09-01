INSERT OR REPLACE INTO businessOwner(
  id,
  username,
  businessName,
  businessOwnerName,
  address,
  phone,
  password,
  tagline
) VALUES (
  1,
  'demobo',
  'democorp',
  'john demo',
  '123 demo street',
  '03 9876 5432',
  'demopw',
  'tag'
);


INSERT OR REPLACE INTO businessOwner(
  id,
  username,
  businessName,
  businessOwnerName,
  address,
  phone,
  password,
  tagline
) VALUES (
  2,
  'demobo2',
  'democorp2',
  'john demo 2',
  '125 demo street',
  '03 9876 5433',
  'demopw',
  'tagline'
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

INSERT OR REPLACE INTO customer(
  id,
  username,
  password,
  firstName,
  lastName,
  email
) VALUES (
  2,
  'democu2',
  'demopw',
  'johnb',
  'demob',
  'john2@demo.com'
);

INSERT OR REPLACE INTO employee(
  id,
  name,
  business,
  email,
  phone,
  address
) VALUES (
  1,
  'demo employee',
  1,
  'employee@democorp.com',
  '0412 345 678',
  '123 demo street, demo town, VIC 3123'
);

INSERT OR REPLACE INTO employee(
  id,
  name,
  business,
  email,
  phone,
  address
) VALUES (
  2,
  'demo other employee',
  1,
  'employee2@democorp.com',
  '0412 345 679',
  '1234 demo street, demo town, VIC 3123'
);

INSERT OR REPLACE INTO employee(
  id,
  name,
  business,
  email,
  phone,
  address,
  deleted
) VALUES (
  3,
  'Jenny',
  1,
  'jenny@democorp.com',
  '0412 345 678',
  '123 demo street, demo town, VIC 3123',
  1
);

INSERT OR REPLACE INTO employee(
  id,
  name,
  business,
  email,
  phone,
  address,
  deleted
) VALUES (
  4,
  'Peter',
  1,
  'peter@democorp.com',
  '0412 345 678',
  '123 demo street, demo town, VIC 3123',
  1
);

INSERT OR REPLACE INTO shift(
  id,
  business,
  name,
  startHour,
  startMin,
  endHour,
  endMin
) VALUES (
  1,
  1,
  'Morning Shift',
  9,
  0,
  12,
  0
);

INSERT OR REPLACE INTO shift(
  id,
  business,
  name,
  startHour,
  startMin,
  endHour,
  endMin
) VALUES (
  2,
  1,
  'Midday Shift',
  12,
  0,
  15,
  0
);

INSERT OR REPLACE INTO shift(
  id,
  business,
  name,
  startHour,
  startMin,
  endHour,
  endMin
) VALUES (
  3,
  1,
  'Afternoon Shift',
  15,
  0,
  18,
  0
);

INSERT OR REPLACE INTO shift(
  id,
  business,
  name,
  startHour,
  startMin,
  endHour,
  endMin
) VALUES (
  4,
  2,
  'AM Shift',
  8,
  0,
  11,
  0
);

INSERT OR REPLACE INTO shift(
  id,
  business,
  name,
  startHour,
  startMin,
  endHour,
  endMin
) VALUES (
  5,
  2,
  'PM Shift',
  12,
  0,
  17,
  0
);


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
  6,
  'Saturday'
);

INSERT OR REPLACE INTO day(
  id,
  dayText
) VALUES(
  7,
  'Sunday'
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  1,
  1,
  1,
  1
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  2,
  1,
  1,
  2
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  3,
  1,
  1,
  3
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  4,
  2,
  1,
  1
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  5,
  2,
  1,
  2
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  6,
  2,
  1,
  3
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  7,
  3,
  1,
  1
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  8,
  3,
  1,
  2
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  9,
  3,
  1,
  3
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  10,
  4,
  1,
  1
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  11,
  4,
  1,
  2
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  12,
  4,
  1,
  3
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  13,
  5,
  1,
  1
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  14,
  5,
  1,
  2
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  15,
  5,
  1,
  3
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  16,
  6,
  1,
  2
);

INSERT OR REPLACE INTO workShift(
  id,
  day,
  business,
  shift
) VALUES (
  17,
  7,
  1,
  2
);

INSERT OR REPLACE INTO service(
  id,
  business,
  serviceName,
  serviceCost,
  duration
) VALUES (
  1,
  1,
  'Tyre Clean',
  10.00,
  30 --Minutes
);

INSERT OR REPLACE INTO service(
  id,
  business,
  serviceName,
  serviceCost,
  duration
) VALUES (
  2,
  1,
  'Standard Clean',
  50.00,
  60 --Minutes
);

INSERT OR REPLACE INTO service(
  id,
  business,
  serviceName,
  serviceCost,
  duration
) VALUES (
  3,
  1,
  'Premium Clean',
  10.00,
  120 --Minutes
);

INSERT OR REPLACE INTO service(
  id,
  business,
  serviceName,
  serviceCost,
  duration
) VALUES (
  4,
  2,
  'Haircut',
  10.00,
  30 --Minutes
);
INSERT OR REPLACE INTO work(
  id,
  employee,
  workShift
) VALUES (
  1,
  1,
  1
);

INSERT OR REPLACE INTO work(
  id,
  employee,
  workShift
) VALUES (
  2,
  1,
  10
);

INSERT OR REPLACE INTO work(
  id,
  employee,
  workShift
) VALUES (
  3,
  1,
  11
);

INSERT OR REPLACE INTO work(
  id,
  employee,
  workShift
) VALUES (
  4,
  1,
  12
);

INSERT OR REPLACE INTO specialisation(
  employee, service
) VALUES (
  1, 1
);

INSERT OR REPLACE INTO specialisation(
  employee, service
) VALUES (
  1, 2
);

INSERT OR REPLACE INTO specialisation(
  employee, service
) VALUES (
  1, 3
);

INSERT OR REPLACE INTO specialisation(
  employee, service
) VALUES (
  2, 1
);
