package xzf.spiderman.common;

public interface RetCode
{
    String SUCCESS = "0";


    String UNKNOWN_ERROR = "unknown_error";
    String BIZ_ERROR = "biz_error";
    String ARGUMENT_NOT_VALID_ERROR = "argument_not_valid_error";
    String REQUEST_TIMEOUT_ERROR = "request_timeout_error";
    String THIRD_PARTY_SERVICE_INVOKE_ERROR = "third_party_service_invoke_error";
    String JSON_PARSER_ERROR = "json_parser_error";



}
