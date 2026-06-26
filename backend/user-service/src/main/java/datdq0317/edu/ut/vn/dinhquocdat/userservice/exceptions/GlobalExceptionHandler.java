package datdq0317.edu.ut.vn.dinhquocdat.userservice.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidFormat(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Ngày sinh không hợp lệ");
        return ResponseEntity.badRequest().body(error);
    }

    // XỬ LÝ LỖI VALIDATION TỪ DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();

        // Gom tất cả các message lỗi lại thành 1 chuỗi (ngăn cách bởi dấu phẩy)
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);

        // Gắn vào cả "message" và "error" để pass toàn bộ test case Postman
        response.put("message", errorMessage);
        response.put("error", errorMessage);

        return ResponseEntity.badRequest().body(response);
    }
}