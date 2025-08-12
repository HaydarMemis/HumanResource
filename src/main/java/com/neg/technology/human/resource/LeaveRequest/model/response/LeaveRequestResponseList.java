package com.neg.technology.human.resource.LeaveRequest.model.response;

import java.util.List;

public class LeaveRequestResponseList {
    private List<LeaveRequestResponse> leaveRequests;

    public LeaveRequestResponseList() {}

    public LeaveRequestResponseList(List<LeaveRequestResponse> leaveRequests) {
        this.leaveRequests = leaveRequests;
    }

    public List<LeaveRequestResponse> getLeaveRequests() {
        return leaveRequests;
    }

    public void setLeaveRequests(List<LeaveRequestResponse> leaveRequests) {
        this.leaveRequests = leaveRequests;
    }
}
