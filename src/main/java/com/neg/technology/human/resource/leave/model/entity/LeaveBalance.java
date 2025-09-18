package com.neg.technology.human.resource.leave.model.entity;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.utility.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "leave_balance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "leave_type_id"})
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveBalance extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private BigDecimal totalAmount; // Toplam hak

    @Column(name = "used_days")
    @Builder.Default
    private Integer usedDays = 0;

    public BigDecimal getAvailableBalance() {
        return totalAmount.subtract(BigDecimal.valueOf(usedDays));
    }

    public void deduct(BigDecimal days) {
        if (getAvailableBalance().compareTo(days) < 0) {
            throw new IllegalArgumentException("Insufficient leave balance.");
        }
        this.usedDays += days.intValue();
    }

    public void addLeave(BigDecimal days) {
        this.totalAmount = this.totalAmount.add(days);
    }
}
