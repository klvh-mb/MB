

insert into `UserChild` (`id`,`date_of_birth`,`gender`) values
(1,'2013-03-13 00:00:00','Male'),
(2,'2014-08-26 00:00:00','Male');


insert into `User_UserChild` (`User_id`,`children_id`) values
(1,1),
(1,2);
