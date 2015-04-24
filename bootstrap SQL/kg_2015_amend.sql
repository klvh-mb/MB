

delete from Kindergarten where name = '偉思幼兒園';
update Kindergarten set hasPN = 1, communityId = (select communityId from PreNursery where name = '偉思幼兒園'), noOfPosts = (select noOfPosts from PreNursery where name = '偉思幼兒園') where name = '偉思幼稚園';

delete from Kindergarten where name = '北角衞理堂幼兒園';
update Kindergarten set hasPN = 1, communityId = (select communityId from PreNursery where name = '北角衞理堂幼兒園'), noOfPosts = (select noOfPosts from PreNursery where name = '北角衞理堂幼兒園') where name = '北角衞理堂幼稚園';

delete from Kindergarten where name = '九龍靈糧堂幼兒園';
update Kindergarten set hasPN = 1, communityId = (select communityId from PreNursery where name = '九龍靈糧堂幼兒園'), noOfPosts = (select noOfPosts from PreNursery where name = '九龍靈糧堂幼兒園') where name = '九龍靈糧堂幼稚園';

delete from Kindergarten where name = '啟思幼稚園(匯景花園)';
delete from Kindergarten where name = '嘉德麗中英文幼稚園';

delete from Kindergarten where nameEn = 'St Catherine''s Kindergarten (Harbour Place)';
update Kindergarten set hasPN = 1, name = '國際英文幼稚園(雅息士道)' where name = '國際英文幼稚園' and address like '九龍九龍塘雅息%';
update Kindergarten set hasPN = 1, name = '國際英文幼稚園(羅福道)'where name = '國際英文幼稚園' and address like '九龍九龍塘羅福道%';

delete from Kindergarten where name = '明慧幼稚園';
delete from Kindergarten where name = '明慧國際幼稚園';

delete from Kindergarten where nameEn = 'York English & Chinese Kindergarten';
delete from Kindergarten where nameEn = 'York International Kindergarten';
delete from Kindergarten where nameEn = 'York International Pre-school';
delete from Kindergarten where nameEn = 'York English Primary School & Kindergarten (Kowloon Tong)' and address like '九龍九龍塘九龍內地段%';

delete from Kindergarten where nameEn = 'Causeway Bay Victoria International Kindergarten';
update Kindergarten set name = '維多利亞(海峰園)幼兒園', nameEn='Victoria (Harbour Heights) Nursery' where name = '銅鑼灣維多利亞幼兒園';


