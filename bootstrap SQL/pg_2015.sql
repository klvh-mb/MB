delete from PlayGroup;

alter table PlayGroup AUTO_INCREMENT = 1;

insert into PlayGroup (objectType, regionId, districtId, name, nameEn, url, phoneText, email, address, description, target, trailClass, inEnglish, inCantonese, inMandarin, noOfPosts, noOfLikes, noOfViews, noOfBookmarks, deleted, system) values ('PLAYGROUP', 4, 8, 'SpaceKids HK 灣仔分校', 'SpaceKids', 'www.spacekids-hk.org', '26289816', null, '灣仔樂基中心3樓303室', null, '1-16歲', '請自行查詢', true, false, false, 0, 0, 0, 0, false, false);
