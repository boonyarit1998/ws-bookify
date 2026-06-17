package com.ws.bookify.controller;

import com.ws.bookify.dto.VersionResponse;
import com.ws.bookify.util.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller layer — เปิด public ให้เช็คเวอร์ชันของ service ที่กำลังรันอยู่ (ดู SecurityConfig).
 * อ่านข้อมูลจาก build-info.properties ที่ spring-boot-maven-plugin (goal build-info) สร้างไว้.
 */
@RestController
@RequestMapping("/api/version")
public class VersionController {

    // ObjectProvider: build-info อาจยังไม่ถูก generate (เช่นรันใน IDE) -> ไม่มี bean ก็ไม่พัง
    private final BuildProperties buildProperties;

    public VersionController(ObjectProvider<BuildProperties> buildProperties) {
        this.buildProperties = buildProperties.getIfAvailable();
    }

    /** GET /api/version — ชื่อ, เวอร์ชัน และเวลา build ของแอป */
    @GetMapping
    public ResponseEntity<Object> version(HttpServletRequest httpRequest) {
        VersionResponse body = (buildProperties != null)
                ? new VersionResponse(
                        buildProperties.getName(),
                        buildProperties.getVersion(),
                        buildProperties.getTime() != null ? buildProperties.getTime().toString() : null)
                : new VersionResponse("bookify", "dev", null);
        return ResponseEntityUtil.returnDataObject(httpRequest, body);
    }
}
