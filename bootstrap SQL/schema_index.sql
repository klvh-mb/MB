-- SocialRelation
CREATE INDEX socialrel_idx_actor_action ON socialrelation (actor, action);

CREATE INDEX socialrel_idx_target_action ON socialrelation (target, action);

CREATE INDEX socialrel_idx_actor_target_action ON socialrelation (actor, target, action);


-- SecondarySocialRelation
CREATE INDEX secsocialrel_idx_actor_action ON secondarysocialrelation (actor, action);

CREATE INDEX secsocialrel_idx_target_action ON secondarysocialrelation (target, action);

CREATE INDEX secsocialrel_idx_actor_target_action ON secondarysocialrelation (actor, target, action);


-- Post
CREATE INDEX post_idx_comm_ptyp_upddate ON post (community_id, postType, UPDATED_DATE);


-- User Community Affinity
CREATE INDEX usercommunityaffinity_idx_usr_comm ON usercommunityaffinity (userId, communityId);