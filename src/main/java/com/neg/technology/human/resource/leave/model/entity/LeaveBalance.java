package com.neg.technology.human.resource.leave.model.entity;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.utility.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "leave_balance", uniqueConstraints = @UniqueConstraint(columnNames = { "employee_id", "leave_type_id",
        "year" }))
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
    private Integer year;

    @Column(nullable = false)
    private BigDecimal totalDays;

    @Column(name = "used_days", nullable = false)
    @Builder.Default
    private BigDecimal usedDays = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private String deleted = "N";

    // --- Helper Methods ---

    /**
     * Kullanılabilir bakiye hesaplama helper metodu
     */
    public BigDecimal getAvailableBalance() {
        return totalDays.subtract(usedDays);
    }

    /**
     * Kullanılabilir bakiye varsa düşme işlemi
     */
    public void deduct(BigDecimal days) {
        if (getAvailableBalance().compareTo(days) < 0) {
            throw new IllegalArgumentException("Insufficient leave balance.");
        }
        this.usedDays = this.usedDays.add(days);
    }

    /**
     * Bakiye ekleme helper (örn. yeni yılda hak tanımlama veya devretme)
     */
    public void add(BigDecimal days) {
        this.totalDays = this.totalDays.add(days);
    }

    /**
     * Yeni yıl devri için helper:
     * - kullanılmamış günleri döndürür
     * - yeni yıl için usedDays resetlenir
     */
    public BigDecimal carryOverToNewYear(BigDecimal newYearEntitlement) {
        BigDecimal remaining = getAvailableBalance();
        return remaining.add(newYearEntitlement);
    }
}
