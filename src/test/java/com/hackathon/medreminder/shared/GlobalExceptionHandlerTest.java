package com.hackathon.medreminder.shared;

import com.hackathon.medreminder.dose.exception.DoseNotFoundById;
import com.hackathon.medreminder.medication.exception.MedicationNotFoundById;
import com.hackathon.medreminder.posology.exception.PosologyNotFoundById;
import com.hackathon.medreminder.shared.exception.ErrorResponse;
import com.hackathon.medreminder.shared.exception.GlobalExceptionHandler;
import com.hackathon.medreminder.user.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    private final Long sampleId = 3L;
    private final String sampleUsername = "user12";
    private final String sampleEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getRequestURI()).thenReturn("/test-uri");
    }

    @Test
    void handleUserNotFoundByUsername_returnsNotFound() {
        UserNotFoundByUsername ex = new UserNotFoundByUsername(sampleUsername);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundByUsername(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        String message = String.valueOf(response.getBody().message());
        assertTrue(message.contains(sampleUsername));
    }

    @Test
    void handleUserNotFoundById_returnsNotFound() {
        UserNotFoundById ex = new UserNotFoundById(sampleId);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundById(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String message = String.valueOf(response.getBody().message());
        assertTrue(message.contains(sampleId.toString()));
    }

    @Test
    void handleMedicationNotFoundById_returnsNotFound() {
        MedicationNotFoundById ex = new MedicationNotFoundById(sampleId);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundById(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String message = String.valueOf(response.getBody().message());
        assertTrue(message.contains(sampleId.toString()));
    }

    @Test
    void handlePosologyNotFoundById_returnsNotFound() {
        PosologyNotFoundById ex = new PosologyNotFoundById(sampleId);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handlePosologyNotFoundById(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String message = String.valueOf(response.getBody().message());
        assertTrue(message.contains(String.valueOf(sampleId)));
    }

    @Test
    void handleDoseNotFoundById_returnsNotFound() {
        DoseNotFoundById ex = new DoseNotFoundById(sampleId);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDoseNotFoundById(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String message = String.valueOf(response.getBody().message());
        assertTrue(message.contains(String.valueOf(sampleId)));
    }

    @Test
    void handleUserAlreadyExistsByUsername_returnsConflict() {
        UserAlreadyExistsByUsername ex = new UserAlreadyExistsByUsername(sampleUsername);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserAlreadyExistsByUsername(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        String message = String.valueOf(response.getBody().message());
        assertTrue(message.contains(sampleUsername));
    }

    @Test
    void handleUserAlreadyExistsByEmail_returnsConflict() {
        UserAlreadyExistsByEmail ex = new UserAlreadyExistsByEmail(sampleEmail);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserAlreadyExistsByEmail(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        String message = String.valueOf(response.getBody().message());
        assertTrue(message.contains(sampleEmail));
    }

    @Test
    void handleValidationExceptions_returnsBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldname", "must not be null");

        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        // Cast message() to Map<String, String> if your ErrorResponse stores validation errors there
        Object messageObj = response.getBody().message();
        assertNotNull(messageObj);
        assertTrue(messageObj instanceof Map<?, ?>);

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) messageObj;

        assertTrue(errors.containsKey("fieldname"));
        assertEquals("must not be null", errors.get("fieldname"));
    }


}
