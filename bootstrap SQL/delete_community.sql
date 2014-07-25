update Comment set deleted = true where socialObject in (select p.id from Post p where p.community_id = 32);
update Post set deleted = true where community_id = 32;
update Community set deleted = true where id = 32;
