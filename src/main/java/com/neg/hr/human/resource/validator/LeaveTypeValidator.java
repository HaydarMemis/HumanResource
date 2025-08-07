package com.neg.hr.human.resource.validator;

import com.neg.hr.human.resource.dto.create.CreateLeaveTypeDTO;
import com.neg.hr.human.resource.dto.update.UpdateLeaveTypeDTO;
import com.neg.hr.human.resource.entity.LeaveType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LeaveTypeValidator {

    private void validatorCommon(String name, Boolean isAnnual, LeaveType.Gender genderRequired, Boolean isUnpaid, Integer borrowableLimit) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Leave type name must not be empty");
        }

        if (isAnnual == null) {
            throw new IllegalArgumentException("isAnnual must not be null");
        }

        if (genderRequired == null) {
            throw new IllegalArgumentException("genderRequired must not be null");
        }

        if (isUnpaid == null) {
            throw new IllegalArgumentException("isUnpaid must not be null");
        }

        if (borrowableLimit != null && borrowableLimit < 0) {
            throw new IllegalArgumentException("borrowableLimit must not be negative");
        }
    }

    public void validateCreate(CreateLeaveTypeDTO dto) {
        validatorCommon(dto.getName(), dto.getIsAnnual(), dto.getGenderRequired(), dto.getIsUnpaid(), dto.getBorrowableLimit());
    }

    public void validateUpdate(UpdateLeaveTypeDTO dto) {
        validatorCommon(dto.getName(), dto.getIsAnnual(), dto.getGenderRequired(), dto.getIsUnpaid(), dto.getBorrowableLimit());
    }

}
