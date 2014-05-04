package viewmodel;

import models.ArticleCategory;

import org.codehaus.jackson.annotate.JsonProperty;

public class ArticleVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("frd") public Boolean isFeatured;	
	@JsonProperty("ct") public ArticleCategory category;
	public ArticleVM(ArticleCategory articleCategory,String name,long id,Boolean isFeatured) {
		this.category = articleCategory;
		this.name = name;
		this.id = id;
		this.isFeatured = isFeatured;
	}

}
