package com.ws.bookify.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * Factory ที่จัดการ Throwable และ subclass ทั้งหมด (Exception, RuntimeException,
 * exception ที่เราเขียนเอง ฯลฯ) ด้วย adapter เดียว.
 *
 * ทำไมต้องเป็น factory ไม่ใช่ registerTypeAdapter:
 *   Throwable มี subclass จำนวนมาก ลงทะเบียนทีละตัวไม่ไหว — factory เช็ค
 *   "เป็นลูกหลานของ Throwable ไหม" ครั้งเดียว ครอบคลุมทุกตัว.
 *
 * ทำไมต้อง custom ไม่ปล่อยให้ Gson reflect เอง:
 *   การ serialize Throwable แบบ default จะลาก stackTrace ที่ยาวมาก และ
 *   field cause/suppressed ที่อาจวนซ้ำ (circular) ออกมาด้วย ทำให้ JSON บวมและพังได้.
 */
public final class ThrowableAddapterFactory implements TypeAdapterFactory {

    /** singleton — ใช้ตัวเดียวร่วมกัน (อ้างผ่าน ThrowableAddapterFactory.INSTANCE) */
    public static final ThrowableAddapterFactory INSTANCE = new ThrowableAddapterFactory();

    private ThrowableAddapterFactory() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        // ไม่ใช่ Throwable -> คืน null เพื่อให้ Gson ไปใช้ adapter ปกติของ type นั้น
        if (!Throwable.class.isAssignableFrom(type.getRawType())) {
            return null;
        }
        // nullSafe() = จัดการ null ให้อัตโนมัติ ไม่ต้องเช็คเองใน write/read
        return (TypeAdapter<T>) new ThrowableAdapter().nullSafe();
    }

    private static final class ThrowableAdapter extends TypeAdapter<Throwable> {

        @Override
        public void write(JsonWriter out, Throwable value) throws IOException {
            out.beginObject();
            out.name("type").value(value.getClass().getName());
            out.name("message").value(value.getMessage());

            Throwable cause = value.getCause();
            // กันวนซ้ำ: บาง exception ตั้ง cause เป็นตัวเอง
            if (cause != null && cause != value) {
                out.name("cause").value(cause.getClass().getName() + ": " + cause.getMessage());
            }
            out.endObject();
        }

        @Override
        public Throwable read(JsonReader in) throws IOException {
            // ไม่รองรับการแปลง JSON กลับเป็น Throwable (แทบไม่มีกรณีใช้จริง)
            in.skipValue();
            return null;
        }
    }
}
