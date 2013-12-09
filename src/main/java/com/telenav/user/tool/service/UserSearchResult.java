/**
 * 
 */
package com.telenav.user.tool.service;

import java.util.Collection;
import java.util.LinkedList;

import com.telenav.user.model.UmTelenavReceiptsCollection;
import com.telenav.user.resource.RoItemMark;
import com.telenav.user.resource.RoMarker;
import com.telenav.user.resource.RoTelenavReceipt;
import com.telenav.user.resource.RoUserCredentials;
import com.telenav.user.resource.RoUserItem;
import com.telenav.user.resource.RoUserMigrationState;
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
	private RoUserMigrationState roUserMigrationState;
	
	private Collection<RoUserCredentials> roUserCredentials=new LinkedList<>();
	
	private Collection<RoUserProfile> roUserProfiles=new LinkedList<>();
	
	private Collection<RoMarker> roMarkers=new LinkedList<>();
	
	private Collection<RoUserItem> roUserItems=new LinkedList<>();
	
	private Collection<RoTelenavReceipt> activeReceipts=new LinkedList<>();
	
	private Collection<RoTelenavReceipt> archivedReceipts=new LinkedList<>();
	
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

	
	public Collection<RoUserCredentials> getRoUserCredentials() {
		return roUserCredentials;
	}
	public void setRoUserCredentials(Collection<RoUserCredentials> roUserCredentials) {
		this.roUserCredentials = roUserCredentials;
	}
	public Collection<RoMarker> getRoMarkers() {
		return roMarkers;
	}
	public void setRoMarkers(Collection<RoMarker> roMarkers) {
		this.roMarkers = roMarkers;
	}
	public Collection<RoUserItem> getRoUserItems() {
		return roUserItems;
	}
	public void setRoUserItems(Collection<RoUserItem> roUserItems) {
		this.roUserItems = roUserItems;
	}
	public Collection<RoTelenavReceipt> getActiveReceipts() {
		return activeReceipts;
	}
	public void setActiveReceipts(Collection<RoTelenavReceipt> activeReceipts) {
		this.activeReceipts = activeReceipts;
	}
	public Collection<RoTelenavReceipt> getArchivedReceipts() {
		return archivedReceipts;
	}
	public void setArchivedReceipts(Collection<RoTelenavReceipt> archivedReceipts) {
		this.archivedReceipts = archivedReceipts;
	}
	public RoUserMigrationState getRoUserMigrationState() {
		return roUserMigrationState;
	}
	public void setRoUserMigrationState(RoUserMigrationState roUserMigrationState) {
		this.roUserMigrationState = roUserMigrationState;
	}
   
}
