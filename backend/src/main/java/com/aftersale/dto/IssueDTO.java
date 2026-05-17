package com.aftersale.dto;

import com.aftersale.enums.CompensationType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class IssueDTO {
    @NotBlank(message = "赔付记录ID不能为空")
    private String compensationId;
    @NotNull(message = "赔付类型不能为空")
    private CompensationType compensationType;

    public String getCompensationId() {
        return compensationId;
    }

    public void setCompensationId(String compensationId) {
        this.compensationId = compensationId;
    }

    public CompensationType getCompensationType() {
        return compensationType;
    }

    public void setCompensationType(CompensationType compensationType) {
        this.compensationType = compensationType;
    }
}
