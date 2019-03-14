package com.navercorp.pinpoint.plugin.ons.annotation;

public interface AnnotationKey extends com.navercorp.pinpoint.common.trace.AnnotationKey {
    int MAX_ARGS_SIZE = 10;
    AnnotationKey API = AnnotationKeyFactory.of(12, "API");
    AnnotationKey API_METADATA = AnnotationKeyFactory.of(13, "API-METADATA");
    AnnotationKey RETURN_DATA = AnnotationKeyFactory.of(14, "RETURN_DATA", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey API_TAG = AnnotationKeyFactory.of(10015, "API-TAG");
    AnnotationKey ERROR_API_METADATA_ERROR = AnnotationKeyFactory.of(10000010, "API-METADATA-ERROR", AnnotationKeyProperty.ERROR_API_METADATA);
    AnnotationKey ERROR_API_METADATA_AGENT_INFO_NOT_FOUND = AnnotationKeyFactory.of(10000011, "API-METADATA-AGENT-INFO-NOT-FOUND", AnnotationKeyProperty.ERROR_API_METADATA);
    AnnotationKey ERROR_API_METADATA_IDENTIFIER_CHECK_ERROR = AnnotationKeyFactory.of(10000012, "API-METADATA-IDENTIFIER-CHECK_ERROR", AnnotationKeyProperty.ERROR_API_METADATA);
    AnnotationKey ERROR_API_METADATA_NOT_FOUND = AnnotationKeyFactory.of(10000013, "API-METADATA-NOT-FOUND", AnnotationKeyProperty.ERROR_API_METADATA);
    AnnotationKey ERROR_API_METADATA_DID_COLLSION = AnnotationKeyFactory.of(10000014, "API-METADATA-DID-COLLSION", AnnotationKeyProperty.ERROR_API_METADATA);
    AnnotationKey SQL_ID = AnnotationKeyFactory.of(20, "SQL-ID");
    AnnotationKey SQL = AnnotationKeyFactory.of(21, "SQL", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey SQL_METADATA = AnnotationKeyFactory.of(22, "SQL-METADATA");
    AnnotationKey SQL_PARAM = AnnotationKeyFactory.of(23, "SQL-PARAM");
    AnnotationKey SQL_BINDVALUE = AnnotationKeyFactory.of(24, "SQL-BindValue", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey STRING_ID = AnnotationKeyFactory.of(30, "STRING_ID");
    AnnotationKey HTTP_URL = AnnotationKeyFactory.of(40, "http.url");
    AnnotationKey HTTP_PARAM = AnnotationKeyFactory.of(41, "http.param", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey HTTP_PARAM_ENTITY = AnnotationKeyFactory.of(42, "http.entity", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey HTTP_COOKIE = AnnotationKeyFactory.of(45, "http.cookie", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey HTTP_STATUS_CODE = AnnotationKeyFactory.of(46, "http.status.code", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey HTTP_INTERNAL_DISPLAY = AnnotationKeyFactory.of(48, "http.internal.display");
    AnnotationKey HTTP_IO = AnnotationKeyFactory.of(49, "http.io", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey MESSAGE_QUEUE_URI = AnnotationKeyFactory.of(100, "message.queue.url");
    AnnotationKey ARGS0 = AnnotationKeyFactory.of(-1, "args[0]");
    AnnotationKey ARGS1 = AnnotationKeyFactory.of(-2, "args[1]");
    AnnotationKey ARGS2 = AnnotationKeyFactory.of(-3, "args[2]");
    AnnotationKey ARGS3 = AnnotationKeyFactory.of(-4, "args[3]");
    AnnotationKey ARGS4 = AnnotationKeyFactory.of(-5, "args[4]");
    AnnotationKey ARGS5 = AnnotationKeyFactory.of(-6, "args[5]");
    AnnotationKey ARGS6 = AnnotationKeyFactory.of(-7, "args[6]");
    AnnotationKey ARGS7 = AnnotationKeyFactory.of(-8, "args[7]");
    AnnotationKey ARGS8 = AnnotationKeyFactory.of(-9, "args[8]");
    AnnotationKey ARGS9 = AnnotationKeyFactory.of(-10, "args[9]");
    AnnotationKey ARGSN = AnnotationKeyFactory.of(-11, "args[N]");
    AnnotationKey CACHE_ARGS0 = AnnotationKeyFactory.of(-30, "cached_args[0]");
    AnnotationKey CACHE_ARGS1 = AnnotationKeyFactory.of(-31, "cached_args[1]");
    AnnotationKey CACHE_ARGS2 = AnnotationKeyFactory.of(-32, "cached_args[2]");
    AnnotationKey CACHE_ARGS3 = AnnotationKeyFactory.of(-33, "cached_args[3]");
    AnnotationKey CACHE_ARGS4 = AnnotationKeyFactory.of(-34, "cached_args[4]");
    AnnotationKey CACHE_ARGS5 = AnnotationKeyFactory.of(-35, "cached_args[5]");
    AnnotationKey CACHE_ARGS6 = AnnotationKeyFactory.of(-36, "cached_args[6]");
    AnnotationKey CACHE_ARGS7 = AnnotationKeyFactory.of(-37, "cached_args[7]");
    AnnotationKey CACHE_ARGS8 = AnnotationKeyFactory.of(-38, "cached_args[8]");
    AnnotationKey CACHE_ARGS9 = AnnotationKeyFactory.of(-39, "cached_args[9]");
    AnnotationKey CACHE_ARGSN = AnnotationKeyFactory.of(-40, "cached_args[N]");
    @Deprecated
    AnnotationKey EXCEPTION = AnnotationKeyFactory.of(-50, "Exception", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    @Deprecated
    AnnotationKey EXCEPTION_CLASS = AnnotationKeyFactory.of(-51, "ExceptionClass");
    AnnotationKey UNKNOWN = AnnotationKeyFactory.of(-9999, "UNKNOWN");
    AnnotationKey ASYNC = AnnotationKeyFactory.of(-100, "Asynchronous Invocation", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey PROXY_HTTP_HEADER = AnnotationKeyFactory.of(300, "PROXY_HTTP_HEADER", AnnotationKeyProperty.VIEW_IN_RECORD_SET);
    AnnotationKey DUBBO_ARGS_ANNOTATION_KEY = AnnotationKeyFactory.of(90, "dubbo.args");
    AnnotationKey DUBBO_RESULT_ANNOTATION_KEY = AnnotationKeyFactory.of(91, "dubbo.result");
    AnnotationKey HSF_ARGS_ANNOTATION_KEY = AnnotationKeyFactory.of(94, "hsf.args");
    AnnotationKey HSF_RESULT_ANNOTATION_KEY = AnnotationKeyFactory.of(95, "hsf.result");
    AnnotationKey ALIWARE_MQ_CONSUMER_DELAY = AnnotationKeyFactory.of(201, "aliWareMQConsumerDelay");
    AnnotationKey HTTP_STATUS_CODE_200 = AnnotationKeyFactory.of(211, "sc200");
    AnnotationKey HTTP_STATUS_CODE_2XX = AnnotationKeyFactory.of(212, "sc2XX");
    AnnotationKey HTTP_STATUS_CODE_3XX = AnnotationKeyFactory.of(213, "sc3XX");
    AnnotationKey HTTP_STATUS_CODE_4XX = AnnotationKeyFactory.of(214, "sc4XX");
    AnnotationKey HTTP_STATUS_CODE_5XX = AnnotationKeyFactory.of(215, "sc5XX");
    AnnotationKey USER_ENTRY_METHOD_TAGS = AnnotationKeyFactory.of(301, "userEntryMethodTags");

    String getName();

    int getCode();

    boolean isErrorApiMetadata();

    boolean isViewInRecordSet();
}
