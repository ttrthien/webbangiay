package poly.com.asm.model;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String fullname;
    private String email;
    private String photo;
}