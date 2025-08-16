package com.neg.technology.human.resource.leave_policy.model.response;

import java.util.List;

public class LeavePolicyResponseList {
    private List<LeavePolicyResponse> leavePolicies;

    public LeavePolicyResponseList() {
    }

    public LeavePolicyResponseList(List<LeavePolicyResponse> leavePolicies) {
        this.leavePolicies = leavePolicies;
    }

    public List<LeavePolicyResponse> getLeavePolicies() {
        return leavePolicies;
    }

    public void setLeavePolicies(List<LeavePolicyResponse> leavePolicies) {
        this.leavePolicies = leavePolicies;
    }
}
