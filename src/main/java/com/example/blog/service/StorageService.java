package com.example.blog.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    private final String supabaseUrl;
    private final String supabaseBucket;
    private final String supabaseApiKey;
    private final OkHttpClient client = new OkHttpClient();

    // Inject values from application.properties
    public StorageService(
            @Value("${SUPABASE_URL}") String supabaseUrl,
            @Value("${SUPABASE_BUCKET}") String supabaseBucket,
            @Value("${SUPABASE_API_KEY}") String supabaseApiKey) {
        this.supabaseUrl = supabaseUrl;
        this.supabaseBucket = supabaseBucket;
        this.supabaseApiKey = supabaseApiKey;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Generate a unique file name to prevent overwrites
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Create a request body directly from the MultipartFile's bytes
        RequestBody requestBody = RequestBody.create(file.getBytes(), MediaType.parse(file.getContentType()));

        Request request = new Request.Builder()
                .url(supabaseUrl + "/storage/v1/object/" + supabaseBucket + "/" + fileName)
                .header("apikey", supabaseApiKey)
                .header("Authorization", "Bearer " + supabaseApiKey)
                .put(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Upload failed: " + response.body().string());
            }
        }

        // Return the public URL for the uploaded file
        return supabaseUrl + "/storage/v1/object/public/" + supabaseBucket + "/" + fileName;
    }
}