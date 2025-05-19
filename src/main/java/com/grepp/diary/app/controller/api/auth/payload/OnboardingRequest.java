package com.grepp.diary.app.controller.api.auth.payload;

import lombok.Data;

@Data
public class OnboardingRequest {
    private int aiId;
    private int isFormal;
    private int isLong;
}
