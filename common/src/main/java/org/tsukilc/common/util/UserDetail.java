package org.tsukilc.common.util;

import org.apache.dubbo.rpc.RpcContext;

public class UserDetail {
    public static String getUserId(){
        RpcContext context = RpcContext.getContext();
        String userId = RpcContext.getContext().getAttachment("x-user-id");
        return "1";
    }
}
