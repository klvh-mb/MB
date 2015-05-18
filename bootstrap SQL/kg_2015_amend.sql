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
update Kindergarten set hasPN = 1, name = '國際英文幼稚園(羅福道)' where name = '國際英文幼稚園' and address like '九龍九龍塘羅福道%';

delete from Kindergarten where name = '明慧幼稚園';
delete from Kindergarten where name = '明慧國際幼稚園';

delete from Kindergarten where nameEn = 'York English & Chinese Kindergarten';
delete from Kindergarten where nameEn = 'York International Kindergarten';
delete from Kindergarten where nameEn = 'York International Pre-school';
delete from Kindergarten where nameEn = 'York English Primary School & Kindergarten (Kowloon Tong)' and address like '九龍九龍塘九龍內地段%';

delete from Kindergarten where nameEn = 'Causeway Bay Victoria International Kindergarten';
delete from Kindergarten where nameEn = 'Kornhill Victoria Kindergarten';
delete from Kindergarten where nameEn = 'Victoria Nursery';
update Kindergarten set name = '維多利亞(海峰園)幼兒園', nameEn='Victoria (Harbour Heights) Nursery' where name = '銅鑼灣維多利亞幼兒園';

delete from Kindergarten where nameEn = 'Sheng Kung Hui Kindergarten (Mount Butler)';
update Kindergarten set districtId=8, address='香港銅鑼灣東院道7號',
govId='6672', govUrl='http://kgp.highlight.hk/website/schoolinfo.php?lang=tc&schid=6672',
curriculum = '1.透過宗教課程,從生活實踐中,讓兒童認識主耶穌。2.透過全語文、英語及普通話課程,為兒童的兩文三語奠定良好基礎。3.編訂由淺入深的螺旋式課程,以配合兒童的智力發展。',
curriculumType = '本地', annualFeeAM_N='$28,200 (10)', annualFeeAM_LKG='$28,200 (10)', annualFeeAM_UKG='$28,200 (10)',
annualFeePM_N='$28,200 (10)', annualFeePM_LKG='$28,200 (10)', annualFeePM_UKG='$28,200 (10)', numEnrollAM_N='90', numEnrollAM_LKG='90', numEnrollAM_UKG='60', numEnrollPM_N='90', numEnrollPM_LKG='89', numEnrollPM_UKG='60'
where nameEn='Sheng Kung Hui Kindergarten';



