package com.neg.technology.human.resource.leave.model.response;

import java.util.List;

public class LeaveBalanceResponseList {
    private List<LeaveBalanceResponse> leaveBalances;

    public LeaveBalanceResponseList() {}

    public LeaveBalanceResponseList(List<LeaveBalanceResponse> leaveBalances) {
        this.leaveBalances = leaveBalances;
    }

    public List<LeaveBalanceResponse> getLeaveBalances() {
        return leaveBalances;
    }

    public void setLeaveBalances(List<LeaveBalanceResponse> leaveBalances) {
        this.leaveBalances = leaveBalances;
    }
}
