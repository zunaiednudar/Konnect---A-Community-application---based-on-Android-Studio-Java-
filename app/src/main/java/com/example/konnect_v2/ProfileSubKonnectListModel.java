package com.example.konnect_v2;

import java.util.ArrayList;

public class ProfileSubKonnectListModel {
    String subKonnectId, subKonnectTitle, subKonnectDescription;
    ArrayList<String> subKonnectMemberIds;

    public ProfileSubKonnectListModel() {
    }

    public ProfileSubKonnectListModel(String subKonnectId, String subKonnectTitle, String subKonnectDescription, ArrayList<String> subKonnectMemberIds) {
        this.subKonnectId = subKonnectId;
        this.subKonnectTitle = subKonnectTitle;
        this.subKonnectDescription = subKonnectDescription;
        this.subKonnectMemberIds = subKonnectMemberIds;
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

    public ArrayList<String> getSubKonnectMemberIds() {
        return subKonnectMemberIds;
    }

    public void setSubKonnectMemberIds(ArrayList<String> subKonnectMemberIds) {
        this.subKonnectMemberIds = subKonnectMemberIds;
    }
}
