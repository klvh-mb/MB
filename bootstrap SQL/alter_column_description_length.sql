ALTER TABLE UserInfo MODIFY aboutMe VARCHAR(2000);
ALTER TABLE Community MODIFY description VARCHAR(2000);
ALTER TABLE Post MODIFY body VARCHAR(2000);
ALTER TABLE Comment MODIFY body VARCHAR(2000);
ALTER TABLE Message MODIFY body VARCHAR(500);