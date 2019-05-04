CREATE TABLE `score` (
  `scid` int(11) NOT NULL,
  `course` varchar(10) NOT NULL,
  `score` int(3) unsigned zerofill NOT NULL,
  `stid` int(11) NOT NULL,
  PRIMARY KEY (`scid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;