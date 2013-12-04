/**
 * 
 */
package com.telenav.user.search;

import java.util.Collection;

import com.telenav.user.resource.RoItemMark;
import com.telenav.user.resource.RoUserProfile;
import com.telenav.user.resource.RoUserRegistrationData;

/**
 * @author [Liu Jie]
 *
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
public class UserSearchResult {

    private RoUserRegistrationData roUserRegistrationData;
    private RoItemMark roItemMark;
    private Collection<RoUserProfile> roUserProfiles;
    
    /**
     * @return the roUserProfile
     */
    public Collection<RoUserProfile> getRoUserProfiles() {
        return roUserProfiles;
    }
    /**
     * @param roUserProfile the roUserProfile to set
     */
    public void setRoUserProfiles(Collection<RoUserProfile> roUserProfiles) {
        this.roUserProfiles = roUserProfiles;
    }
    /**
     * @return the roUserRegistrationData
     */
    public RoUserRegistrationData getRoUserRegistrationData() {
        return roUserRegistrationData;
    }
    /**
     * @param roUserRegistrationData the roUserRegistrationData to set
     */
    public void setRoUserRegistrationData(RoUserRegistrationData roUserRegistrationData) {
        this.roUserRegistrationData = roUserRegistrationData;
    }
    /**
     * @return the roItemMark
     */
    public RoItemMark getRoItemMark() {
        return roItemMark;
    }
    /**
     * @param roItemMark the roItemMark to set
     */
    public void setRoItemMark(RoItemMark roItemMark) {
        this.roItemMark = roItemMark;
    }

}
