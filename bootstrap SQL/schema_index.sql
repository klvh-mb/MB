CREATE INDEX socialrel_idx_actor_action ON socialrelation (actor, action);

CREATE INDEX socialrel_idx_target_action ON socialrelation (target, action);

CREATE INDEX socialrel_idx_actor_target_action ON socialrelation (actor, target, action);