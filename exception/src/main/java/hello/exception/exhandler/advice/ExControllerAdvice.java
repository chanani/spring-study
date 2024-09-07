package hello.exception.exhandler.advice;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

    // 정상 흐름으로 반환되기 떄문에 즉, 200으로 반환 되기 떄문에 ResponseStatus 통해서 상태 코드 전달
    //@ExceptionHandler({IllegalArgumentException.class, RuntimeException.class}) // 다중으로 선언 가능
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    // @ExceptionHandler에 UserException을 넣지 않으면 메서드 파라미터 예외를 사용된다.
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e){
        log.error("[exceptionHandler ex", e);
        ErrorResult errorResult = new ErrorResult("USER_-EX", e.getMessage());
        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    // 스프링의 우선순위는 항상 자세한 것이 우선권을 가집니다
    // Excetion의 자식이 처리하지 못한 에러는 해당 메서드에서 처리
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e){
        log.error("[exceptionHandler ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

}
