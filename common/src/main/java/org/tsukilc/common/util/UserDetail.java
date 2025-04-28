package org.tsukilc.common.util;

import org.apache.dubbo.remoting.http12.HttpRequest;
import org.apache.dubbo.rpc.RpcContext;

public class UserDetail {
    public static String getUserId(){
        HttpRequest request = (HttpRequest) RpcContext.getContext().getRequest();
        String userId = request.header("x-user-id");
        return userId;
    }
}
