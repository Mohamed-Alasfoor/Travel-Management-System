package com.travelplan.engagement;
import java.time.Instant; import java.util.Map; import org.springframework.http.*; import org.springframework.security.access.AccessDeniedException; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice class ApiExceptionHandler {
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) ResponseEntity<Map<String,Object>> bad(RuntimeException e){return body(HttpStatus.BAD_REQUEST,e.getMessage());}
 @ExceptionHandler(AccessDeniedException.class) ResponseEntity<Map<String,Object>> denied(AccessDeniedException e){return body(HttpStatus.FORBIDDEN,e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<Map<String,Object>> invalid(MethodArgumentNotValidException e){return body(HttpStatus.BAD_REQUEST,e.getBindingResult().getAllErrors().getFirst().getDefaultMessage());}
 private ResponseEntity<Map<String,Object>> body(HttpStatus s,String message){return ResponseEntity.status(s).body(Map.of("timestamp",Instant.now(),"status",s.value(),"error",s.getReasonPhrase(),"message",message==null?s.getReasonPhrase():message));}
}
