# syntax=docker/dockerfile:1

# ---- Stage 1: build ----
# ใช้ JDK 21 (temurin) build เป็น jar ด้วย Maven wrapper ที่อยู่ใน repo
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# คัดลอกเฉพาะไฟล์ที่จำเป็นต่อการ resolve dependency ก่อน
# -> layer นี้ถูก cache ไว้ ตราบใดที่ pom.xml ไม่เปลี่ยน build รอบหลังเร็วขึ้นมาก
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw --batch-mode --no-transfer-progress dependency:go-offline

# คัดลอก source แล้ว build
# skipTests: เทสรันบน GitHub Actions แล้ว (และ Testcontainers ต้องใช้ Docker ซึ่งไม่มีใน build stage นี้)
COPY src/ src/
RUN ./mvnw --batch-mode --no-transfer-progress clean package -DskipTests

# rename เป็นชื่อคงที่ก่อน เพื่อให้ ENTRYPOINT ไม่ผูกกับเลข version ใน pom
RUN cp target/*.jar app.jar

# แตก fat jar เป็น layer ด้วย jarmode 'tools' (Spring Boot 3.3+/4.x แทน layertools เดิม)
# dependency เปลี่ยนน้อย -> Docker cache layer นั้นได้ build รอบหลังเร็วขึ้น
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# ---- Stage 2: runtime ----
# JRE อย่างเดียว (ไม่ต้องมี compiler) -> image เล็กและ attack surface น้อยกว่า
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# รันด้วย non-root user เพื่อความปลอดภัย
RUN groupadd --system app && useradd --system --gid app app
USER app

# คัดลอก layer เรียงจาก "เปลี่ยนน้อย -> เปลี่ยนบ่อย" เพื่อให้ Docker cache ได้สูงสุด
COPY --from=build --chown=app:app /app/extracted/dependencies/ ./
COPY --from=build --chown=app:app /app/extracted/spring-boot-loader/ ./
COPY --from=build --chown=app:app /app/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=app:app /app/extracted/application/ ./

# Render กำหนด PORT มาทาง env -> ให้ Spring ฟังตาม (default 8080 สำหรับรันในเครื่อง)
ENV SERVER_PORT=8080
EXPOSE 8080

# MaxRAMPercentage: ให้ JVM ปรับ heap ตาม RAM ของ container (สำคัญบน Render free 512MB)
# jarmode 'tools' รันผ่าน application.jar โดยตรง (มันอ้าง lib ที่ copy มาในไดเรกทอรีเดียวกัน)
ENTRYPOINT ["sh", "-c", "java -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT:-8080} -jar app.jar"]
