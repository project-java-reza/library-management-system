package com.sinaukoding.librarymanagementsystem.model.request;

import com.sinaukoding.librarymanagementsystem.validation.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch(field = "newPassword", matchField = "confirmPassword")
public record ChangePasswordRequestRecord(

        @NotBlank(message = "ID tidak boleh kosong")
        String id,

        @NotBlank(message = "Password lama tidak boleh kosong")
        String oldPassword,

        @NotBlank(message = "Password baru tidak boleh kosong")
        String newPassword,

        @NotBlank(message = "Konfirmasi password tidak boleh kosong")
        String confirmPassword
) {
}
