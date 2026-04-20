package com.interviewcoach.service;

import com.interviewcoach.dto.request.UpdateProfileRequest;
import com.interviewcoach.dto.response.ProfileResponse;

public interface UserService {
    ProfileResponse getMyProfile();
    ProfileResponse updateMyProfile(UpdateProfileRequest request);
}