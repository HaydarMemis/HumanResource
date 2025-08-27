package com.neg.technology.human.resource.leave.model.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeLeaveRequestStatusRequest {

    @NotNull(message = "Leave request ID is required")
    private Long id;

    @NotNull(message = "Status cannot be null")
    @Size(min = 3, max = 50, message = "Status must be between 3 and 50 characters")
    private String status;
}