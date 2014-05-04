package viewmodel;

import models.Icons;

import org.codehaus.jackson.annotate.JsonProperty;

public class IconVM {
	@JsonProperty("name") public String name;
	@JsonProperty("url") public String url;
	
	public static IconVM iconVM(Icons icon) {
		IconVM iconVM = new IconVM();
		iconVM.name = icon.name;
		iconVM.url = icon.url;
		return iconVM;
	}
}
