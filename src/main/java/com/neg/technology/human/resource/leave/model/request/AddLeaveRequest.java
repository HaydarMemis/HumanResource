package com.neg.technology.human.resource.leave.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddLeaveRequest {
    private Long employeeId;
    private Long leaveTypeId;
    private BigDecimal days; // Burayı ekle

    // Getter ve Setter
    public BigDecimal getDays() { return days; }
    public void setDays(BigDecimal days) { this.days = days; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(Long leaveTypeId) { this.leaveTypeId = leaveTypeId; }
}
