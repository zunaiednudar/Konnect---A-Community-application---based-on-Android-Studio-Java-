package com.example.konnect_v2;

import java.io.Serializable;
import java.util.ArrayList;

public class SubKonnect {
    private String ownerId, subKonnectId, subKonnectTitle, subKonnectDescription;
    ArrayList<String> memberIds;

    SubKonnect() {
    }

    SubKonnect(String ownerId, String subKonnectId, String subKonnectTitle, String subKonnectDescription, ArrayList<String> memberIds) {
        this.ownerId = ownerId;
        this.subKonnectId = subKonnectId;
        this.subKonnectTitle = subKonnectTitle;
        this.subKonnectDescription = subKonnectDescription;
        this.memberIds = memberIds;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSubKonnectId() {
        return subKonnectId;
    }

    public void setSubKonnectId(String subKonnectId) {
        this.subKonnectId = subKonnectId;
    }

    public String getSubKonnectTitle() {
        return subKonnectTitle;
    }

    public void setSubKonnectTitle(String subKonnectTitle) {
        this.subKonnectTitle = subKonnectTitle;
    }

    public String getSubKonnectDescription() {
        return subKonnectDescription;
    }

    public void setSubKonnectDescription(String subKonnectDescription) {
        this.subKonnectDescription = subKonnectDescription;
    }

    public ArrayList<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(ArrayList<String> memberIds) {
        this.memberIds = memberIds;
    }
}
