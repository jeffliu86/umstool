/**
 * 
 */
package com.telenav.user.search;

import javax.faces.bean.ManagedBean;

/**
 * @author [Liu Jie]
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
@ManagedBean 
public class UserSearcher {

    private static String[] availableKeyWordTypes =
        { "user_id", "email", "fb_id,", "google_plus_id"};

    private String keyWordType;
    
    private String keyWord;

    private UserSearchResult result;
    
    //todo: refactor this to singleton
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

    public  String searchUser() {
        this.result = service.searchUser(keyWord);
        //this.user =  UserSearchService.getInstance().search(keyWord, keyWordType);
//        this.user = new User();
//        user.setEmail("abc@gmail.com");
//        user.setUserName("niko");
//        
//        this.userCredential = new UserCredential();
//        userCredential.setCredentialType("EMAIL-PASSWORD");
//        userCredential.setCredentialValue("aaa@gmail.com");
        return "index";
    }

    
}
