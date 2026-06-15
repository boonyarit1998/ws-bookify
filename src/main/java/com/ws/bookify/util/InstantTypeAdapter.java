package com.ws.bookify.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Instant;

/**
 * แปลง Instant เป็น/จาก JSON เป็น ISO-8601 string (เช่น "2026-06-14T14:42:22Z").
 * จำเป็นเพราะ entity/DTO ใช้ Instant สำหรับ createdAt/updatedAt และ Gson
 * แปลง Instant เองไม่ได้ (reflect เข้า field ภายในไม่ได้ -> JsonIOException).
 */
public class InstantTypeAdapter extends TypeAdapter<Instant> {

    @Override
    public void write(JsonWriter out, Instant value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.toString());
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return Instant.parse(in.nextString());
    }
}
