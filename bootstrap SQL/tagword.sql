insert into TagWord (deleted,system,excludeFromTargeting,UPDATED_DATE,socialObjectCount,noClicks, tagCategory, tagCategoryId, displayWord, matchingWords)
values (0,0,0,CURDATE(),0,0, 0, 'HOT_ARTICLES', '寵物', '寵物');

insert into TagWord (deleted,system,excludeFromTargeting,UPDATED_DATE,socialObjectCount,noClicks, tagCategory, tagCategoryId, displayWord, matchingWords)
values (0,0,0,CURDATE(),0,0, 0, 'HOT_ARTICLES', '食譜', '食譜,菜單,餐單');

insert into TagWord (deleted,system,excludeFromTargeting,UPDATED_DATE,socialObjectCount,noClicks, tagCategory, tagCategoryId, displayWord, matchingWords)
values (0,0,0,CURDATE(),0,0, 0, 'HOT_ARTICLES', '閱讀', '閱讀,圖書,看書,故事書,繪本');

insert into TagWord (deleted,system,excludeFromTargeting,UPDATED_DATE,socialObjectCount,noClicks, tagCategory, tagCategoryId, displayWord, matchingWords)
values (0,0,0,CURDATE(),0,0, 0, 'HOT_ARTICLES', '尿片', '尿布,尿褲,尿片');

insert into TagWord (deleted,system,excludeFromTargeting,UPDATED_DATE,socialObjectCount,noClicks, tagCategory, tagCategoryId, displayWord, matchingWords)
values (0,0,0,CURDATE(),0,0, 0, 'HOT_ARTICLES', '攝影', '攝影,拍攝,相機,閃光燈');

insert into TagWord (deleted,system,excludeFromTargeting,UPDATED_DATE,socialObjectCount,noClicks, tagCategory, tagCategoryId, displayWord, matchingWords)
values (0,0,0,CURDATE(),0,0, 0, 'HOT_ARTICLES', '學說話', '學說話,學會說話,不會說話,說話能力');

insert into TagWord (deleted,system,excludeFromTargeting,UPDATED_DATE,socialObjectCount,noClicks, tagCategory, tagCategoryId, displayWord, matchingWords)
values (0,0,0,CURDATE(),0,0, 0, 'HOT_ARTICLES', '瘦身', '瘦身,瘦腿,減肥');

update TagWord set matchingWords = '閱讀,圖書,看書,故事書,繪本' where displayWord = '閱讀';
update TagWord set matchingWords = '雙胞胎,龍鳳胎,孖仔,孖女' where displayWord = '雙胞胎';


insert into TagWord (deleted,system,excludeFromTargeting,UPDATED_DATE,socialObjectCount,noClicks, tagCategory, tagCategoryId, displayWord, matchingWords)
values (0,0,0,CURDATE(),0,0, 0, 'SOON_TO_BE_MOMS_ARTICLES', '改名', '改名');

insert into TagWord (deleted,system,excludeFromTargeting,UPDATED_DATE,socialObjectCount,noClicks, tagCategory, tagCategoryId, displayWord, matchingWords)
values (0,0,0,CURDATE(),0,0, 0, 'SOON_TO_BE_MOMS_ARTICLES', '孕婦裝', '孕婦裝,大肚婆衫,孕婦褲,連身裙,孕期內衣');


update TagWord set matchingWords = '寵物,拳師犬,狗狗' where displayWord = '寵物';
