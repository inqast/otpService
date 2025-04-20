package otp.controller.request;

public record OtpValidationRequest(int userId, String operationId, String code) {}
