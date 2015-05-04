
#select count(*) from post where community_id in (select k.communityId from Kindergarten k) and community_id not in (select p.communityId from PreNursery p); 

# PN posts
update Post set postSubType = 1 where community_id not in (select k.communityId from Kindergarten k) and community_id in (select p.communityId from PreNursery p);

# KG posts
update Post set postSubType = 2 where community_id in (select k.communityId from Kindergarten k) and community_id not in (select p.communityId from PreNursery p);

# PN_KG posts
update Post set postSubType = 3 where community_id in (select k.communityId from Kindergarten k) and community_id in (select p.communityId from PreNursery p);

# COMMUNITY posts
update Post set postSubType = 0 where community_id not in (select k.communityId from Kindergarten k) and community_id not in (select p.communityId from PreNursery p);