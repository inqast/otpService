package otp.controller.request;

public record OtpGenerateRequest(String operationId, String channel, String destination) {}
