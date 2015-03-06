/* Remove obsolete comms */
update community set deleted=true where id=40;
update community set deleted=true where id=41;
update community set deleted=true where id=42;
update community set deleted=true where id=209;
update community set deleted=true where id=212;
update community set deleted=true where id=214;
update community set deleted=true where id=215;
update post set deleted=true where community_id=40;
update post set deleted=true where community_id=41;
update post set deleted=true where community_id=42;
update post set deleted=true where community_id=209;
update post set deleted=true where community_id=212;
update post set deleted=true where community_id=214;
update post set deleted=true where community_id=215;
update community_communitycategory set communitycategories_id=7 where community_id=37;
update community_communitycategory set communitycategories_id=7 where community_id=38;
update community_communitycategory set communitycategories_id=7 where community_id=39;
update communitycategory set deleted=true where id=6;
update communitycategory set deleted=true where id=8;

/* Remove district comms */
update community set deleted=true where id>=13 and id<=30;
update post set deleted=true where community_id>=13 and community_id<=30;

/* rename 母乳專區 to PN幼兒班面試 */
update community set name='幼兒班面試',description='幼兒班面試討論區',targetingInfo=NULL,icon='/assets/app/images/general/icons/community/grad_hat.png' where id=208;
update post set community_id=38 where id=286;
update post set community_id=38 where id=288;
update post set community_id=38 where id=338;

update community set name='幼稚園面試',description='幼稚園面試討論區',targetingInfo=NULL,icon='/assets/app/images/general/icons/community/grad_hat.png' where id=209;