/**
 * 
 */
package com.telenav.user.tool.service;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.expression.impl.ThisExpressionResolver;


/**
 * @author [Liu Jie]
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
@ManagedBean
@SessionScoped
public class UserSearcher {
	
	public static final String TYPE_USER_ID="USER_ID";
    public static final String TYPE_EMAIL="EMAIL";
    public static final String TYPE_FB_ID="FB_ID";
    public static final String TYPE_GOOGLE_PLUS_ID="GOOGLE_PLUS_ID";

    private static String[] availableKeyWordTypes =
        { TYPE_USER_ID, TYPE_EMAIL, TYPE_FB_ID, TYPE_GOOGLE_PLUS_ID};

      
    private String keyWordType;    
    private String keyWord;
    
    private String userId;

    private UserSearchResult result;
    
    private UserSearchService service = new  UserSearchServiceImpl();

    public String getKeyWordType() {
        return keyWordType;
    }

    public void setKeyWordType(String keyWordType) {
        this.keyWordType = keyWordType;
    }
    
    public String getKeyWord() {
        return keyWord;
    }
    
    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public UserSearchResult getResult() {
        return result;
    }
    
    public  String[] getAvailableKeyWordTypes() {
        return availableKeyWordTypes;
    }

    public  void searchUser() {
    	userId=null;
    	if(keyWordType.endsWith(TYPE_USER_ID)){
    		userId=keyWord;    
    	}else{
    		userId=service.lookUpUserKey(keyWordType, keyWord);    		
    	}
    	this.result=service.searchUser(userId.trim());
    }

	public String getUserId() {
		return this.userId;
	}
    
    
    
}
