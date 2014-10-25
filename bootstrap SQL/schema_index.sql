-- User
CREATE INDEX user_idx_created_system ON user (created_date, system);


-- SocialRelation (Friends, Community Members)
CREATE INDEX socialrel_idx_actor_action ON socialrelation (actor, action);

CREATE INDEX socialrel_idx_target_action ON socialrelation (target, action);

CREATE INDEX socialrel_idx_actor_target_action ON socialrelation (actor, target, action);

CREATE INDEX socialrel_idx_action_actType ON socialrelation (action(32), actionType(32));


-- SecondarySocialRelation (Bookmarks)
CREATE INDEX secsocialrel_idx_actor_action ON secondarysocialrelation (actor, action);

CREATE INDEX secsocialrel_idx_target_action ON secondarysocialrelation (target, action);

CREATE INDEX secsocialrel_idx_actor_target_action ON secondarysocialrelation (actor, target, action);


-- PrimarySocialRelation (Likes, Posts)
CREATE INDEX primsocialrel_idx_actor_action ON primarysocialrelation (actor, action);

CREATE INDEX primsocialrel_idx_target_action ON primarysocialrelation (target, action);

CREATE INDEX primsocialrel_idx_actor_target_action ON primarysocialrelation (actor, target, action);


-- Community
CREATE INDEX community_idx_tgtType_system ON community (targetingType, system);


-- Post
CREATE INDEX post_idx_comm_ptyp_upddate ON post (community_id, postType, UPDATED_DATE);


-- Comment
CREATE INDEX comment_idx_sobj_date ON comment (socialObject, date);


-- User Community Affinity
CREATE INDEX usercommunityaffinity_idx_usr_comm ON usercommunityaffinity (userId, communityId);



-- Article
CREATE INDEX article_idx_targetage ON article (excludeFromTargeting, targetAgeMinMonth, targetAgeMaxMonth);

CREATE INDEX article_idx_targetlocation ON article (excludeFromTargeting, targetLocation_id);

CREATE INDEX article_idx_cat ON article (category_id, publishedDate);

CREATE INDEX article_idx_pubdate ON article (publishedDate);

CREATE INDEX article_idx_views ON article (noOfViews);

CREATE INDEX article_idx_likes ON article (noOfLikes);


-- TagWord
CREATE INDEX tagwordscore_idx_twid_score ON TagWordScore (tagWordId, score);


-- PreNursery
CREATE INDEX prenursery_idx_region_dist ON prenursery (regionId, districtId);