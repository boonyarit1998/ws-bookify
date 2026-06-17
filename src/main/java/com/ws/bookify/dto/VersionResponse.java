package com.ws.bookify.dto;

/**
 * ข้อมูลเวอร์ชันของ service ที่กำลังรันอยู่ — ใช้ตอบ GET /api/version.
 * buildTime เป็น ISO-8601 (อาจเป็น null ถ้ารันแบบยังไม่ได้ generate build-info).
 */
public record VersionResponse(String name, String version, String buildTime) {
}
