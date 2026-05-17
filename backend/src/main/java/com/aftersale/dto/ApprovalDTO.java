package com.aftersale.dto;

import javax.validation.constraints.NotBlank;

public class ApprovalDTO {
    @NotBlank(message = "赔付记录ID不能为空")
    private String compensationId;
    @NotBlank(message = "审批人不能为空")
    private String approver;
    private boolean approved;
    private String rejectReason;

    public String getCompensationId() {
        return compensationId;
    }

    public void setCompensationId(String compensationId) {
        this.compensationId = compensationId;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
