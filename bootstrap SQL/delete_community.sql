update Comment set deleted = true where socialObject in (select p.id from Post p where p.community_id = );
update Post set deleted = true where community_id = ;
update Community set deleted = true, excludeFromNewsfeed = true where id = ;
update UserCommunityAffinity set newsfeedEnabled = false where communityId = ;