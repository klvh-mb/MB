/* Remove obsolete comms */
update community set deleted=true where id=40;
update community set deleted=true where id=41;
update community set deleted=true where id=42;
update community set deleted=true where id=209;
update community set deleted=true where id=212;
update community set deleted=true where id=214;
update community set deleted=true where id=215;
update community_communitycategory set communitycategories_id=7 where community_id=37;
update community_communitycategory set communitycategories_id=7 where community_id=38;
update community_communitycategory set communitycategories_id=7 where community_id=39;
update communitycategory set deleted=true where id=6;
update communitycategory set deleted=true where id=8;

/* Remove district comms */
update community set deleted=true where id>=13 and id<=30;