package com.ws.bookify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * เปิดใช้ JPA Auditing เพื่อให้ @CreatedDate / @LastModifiedDate
 * บน entity ถูกเซ็ตค่าอัตโนมัติตอน insert/update.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
