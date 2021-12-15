package io.github.zhangbinhub.acp.cloud.server.helloworld.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import io.github.zhangbinhub.acp.core.exceptions.EnumValueUndefinedException;
import io.github.zhangbinhub.acp.boot.enums.ResponseCode;
import io.github.zhangbinhub.acp.boot.exceptions.RestExceptionHandler;
import io.github.zhangbinhub.acp.boot.exceptions.ServerException;
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter;
import io.github.zhangbinhub.acp.boot.tools.PackageTools;

import javax.validation.ConstraintViolationException;

/**
 * @author zhang by 31/07/2019
 * @since JDK 11
 */
@ControllerAdvice
public class TestExceptionHandler extends RestExceptionHandler {

    private final LogAdapter logAdapter;

    @Autowired
    public TestExceptionHandler(LogAdapter logAdapter) {
        super(logAdapter);
        this.logAdapter = logAdapter;
    }

    /**
     * 处理自定义异常
     *
     * @param ex 异常类
     * @return 响应对象
     */
    @NotNull
    @ExceptionHandler({ServerException.class, ConstraintViolationException.class})
    public ResponseEntity<Object> handleServerException(@NotNull Exception ex) {
        ResponseCode responseCode;
        try {
            logAdapter.error("自定义异常");
            logAdapter.error(ex.getMessage(), ex);
            if (ex instanceof ServerException) {
                Integer code = ((ServerException) ex).getCode();
                if (code != null) {
                    responseCode = ResponseCode.getEnum(code);
                } else {
                    responseCode = ResponseCode.OtherError;
                }
            } else if (ex instanceof ConstraintViolationException || ex instanceof MethodArgumentNotValidException) {
                responseCode = ResponseCode.InvalidParameter;
            } else {
                responseCode = ResponseCode.OtherError;
            }
        } catch (EnumValueUndefinedException e) {
            responseCode = ResponseCode.OtherError;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(PackageTools.buildErrorResponsePackage(responseCode, ex.getMessage()));
    }

}
