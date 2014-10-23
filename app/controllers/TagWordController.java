package controllers;

import models.ArticleCategory;
import models.TagWord;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.TagWordVM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 23/10/14
 * Time: 11:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagWordController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(TagWordController.class);

    @Transactional
	public static Result getSoonToBeMomTagWords() {
        List<TagWord> tagWords = TagWord.getTagWordsByCategory(TagWord.TagCategory.ARTICLE,
                ArticleCategory.ArticleCategoryGroup.SOON_TO_BE_MOMS_ARTICLES.name());

		List<TagWordVM> tagWordVMs = new ArrayList<>();
		for(TagWord tagWord : tagWords) {
			TagWordVM vm = TagWordVM.toTagWordVM(tagWord);
			tagWordVMs.add(vm);
		}
		return ok(Json.toJson(tagWordVMs));
	}
}
