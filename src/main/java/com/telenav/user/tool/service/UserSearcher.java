/**
 * 
 */
package com.telenav.user.tool.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import com.telenav.user.resource.RoUserProfile;

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

    private static String[] availableKeyWordTypes =
        { "user_id", "email", "fb_id,", "google_plus_id"};

    private String keyWordType;
    
    private String keyWord;

    private UserSearchResult result;
    
    private Collection<RoUserProfile> profiles=new ArrayList<>();
    
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

    public  void searchUser() {
        this.result = service.searchUser(keyWord);
        profiles=this.result.getRoUserProfiles();
       
    }

	public Collection<RoUserProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(Collection<RoUserProfile> profiles) {
		this.profiles = profiles;
	}

    
}
