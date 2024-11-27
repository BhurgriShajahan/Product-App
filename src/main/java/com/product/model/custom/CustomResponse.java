package com.product.model.custom;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse<T> {
    private T data;
    private String error;

    public static <T> CustomResponse<T> success(T data) {
        CustomResponse<T> response = new CustomResponse<>();
        response.setData(data);
        return response;
    }
    public static <T> CustomResponse<T> error(String error) {
        CustomResponse<T> response = new CustomResponse<>();
        response.setError(error);
        return response;
    }
}
