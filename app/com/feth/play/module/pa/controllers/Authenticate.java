package com.feth.play.module.pa.controllers;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import Decoder.BASE64Encoder;

import com.feth.play.module.pa.PlayAuthenticate;

import controllers.Application;

public class Authenticate extends Controller {

	
	private static final String PAYLOAD_KEY = "p";
	private static final String USER_KEY = "pa.u.id";
	private static final String PROVIDER_KEY = "pa.p.id";
	
	public static void noCache(final Response response) {
		// http://stackoverflow.com/questions/49547/making-sure-a-web-page-is-not-cached-across-all-browsers
		response.setHeader(Response.CACHE_CONTROL, "no-cache, no-store, must-revalidate");  // HTTP 1.1
		response.setHeader(Response.PRAGMA, "no-cache");  // HTTP 1.0.
		response.setHeader(Response.EXPIRES, "0");  // Proxies.
	}

	@Transactional
	public static Result authenticate(final String provider) {
		noCache(response());
		
		final String payload = getQueryString(request(), PAYLOAD_KEY);
		return PlayAuthenticate.handleAuthentication(provider, ctx(), payload);
	}
	
	 private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec("TheBestSecretkey".getBytes(), "AES");
        return key;
	}
	
	@Transactional
	public static Result mobileAuthenticate(final String provider) {
		noCache(response());
		
		final String payload = getQueryString(request(), PAYLOAD_KEY);
		Result result = PlayAuthenticate.handleAuthentication(provider, ctx(), payload);
		play.api.mvc.Result wrappedResult = result.getWrappedResult();
		if (wrappedResult instanceof play.api.mvc.PlainResult) {
		    play.api.mvc.PlainResult plainResult = (play.api.mvc.PlainResult)wrappedResult;
		    session();
		    Application.getLocalUser(session());
		    int code = plainResult.header().status();
		    if (code == OK){
		    	return ok();
		    }
		      // Cache
		  }
		String encryptedValue = null;
		String plainData=session().get(PROVIDER_KEY)+"-"+session().get(USER_KEY);
		try { 
    		
    		Key key = generateKey();
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(plainData.getBytes());
            encryptedValue = new BASE64Encoder().encode(encVal);
    		
    	}
    	catch(Exception e) { }
		return ok(encryptedValue.replace("+", "%2b"));
	}
	
    @Transactional
    public static Result authenticatePopup(final String provider) {
	    DynamicForm form = DynamicForm.form().bindFromRequest();
	
        String redirectURL = form.get("rurl");   // TODO: Need to get actual url from context object
        session().put("pa.url.orig", redirectURL);
        noCache(response());

        final String payload = getQueryString(request(), PAYLOAD_KEY);
        return PlayAuthenticate.handleAuthentication(provider, ctx(), payload);
	}
	
	public static Result logout() {
		noCache(response());
		
		return PlayAuthenticate.logout(session());
	}

	// TODO remove on Play 2.1
	public static String getQueryString(final Request r, final Object key) {
		final String[] m = r.queryString().get(key);
		if(m != null && m.length > 0) {
			return m[0];
		}
		return null;
	}
}
