package com.neg.technology.human.resource.LeaveType.validator;

import com.neg.technology.human.resource.dto.create.CreateLeaveTypeRequestDTO;
import com.neg.technology.human.resource.dto.update.UpdateLeaveTypeRequestDTO;
import com.neg.technology.human.resource.LeaveType.entity.LeaveType;
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

    public void validateCreate(CreateLeaveTypeRequestDTO dto) {
        validatorCommon(
                dto.getName(),
                dto.getIsAnnual(),
                dto.getGenderRequired(),
                dto.getIsUnpaid(),
                dto.getBorrowableLimit());
    }

    public void validateUpdate(UpdateLeaveTypeRequestDTO dto) {
        validatorCommon(
                dto.getName(),
                dto.getIsAnnual(),
                dto.getGenderRequired(),
                dto.getIsUnpaid(),
                dto.getBorrowableLimit()
        );
    }
}