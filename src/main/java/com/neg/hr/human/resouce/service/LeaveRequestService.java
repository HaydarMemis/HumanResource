package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.LeaveRequest;
import com.neg.hr.human.resouce.repository.LeaveRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequest save(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest findById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LeaveRequest not found with id: " + id));
    }

    public List<LeaveRequest> findAll() {
        return leaveRequestRepository.findAll();
    }

    public void deleteById(Long id) {
        leaveRequestRepository.deleteById(id);
    }
}
