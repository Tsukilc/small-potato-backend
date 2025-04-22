# 小红书系统后端项目

🧑‍💼 用户模块	注册/登录、资料页、关注、粉丝、屏蔽等

📝 笔记模块	图文/视频笔记的发布、编辑、删除。要求可以给笔记创建标签

❤️ 社交互动模块	点赞、收藏、评论、分享

🔍 搜索模块	搜索用户、笔记、标签，支持自动补全

📦 文件模块	图片、视频的上传与展示（支持 CDN 回源）

✉️ 消息模块	系统通知、私信、@通知、评论提醒等

📊 推荐模块	个性化推荐，根据用户兴趣行为排序内容流

🧠 内容安全模块	涉黄/敏感内容过滤，人工+AI 审核接口

🧾 后台管理模块	用户管理、内容审核、数据报表

## 需求

技术栈
dubbo3 triple+ssm+mysql+redis

基于dubbo3 （使用triple协议暴露rpc，http双端口），类似
@RequestMapping("/api")
public interface ProductCatalogService {

    /**
     * 获取商品列表
     */
    @GetMapping("/products")
    ListProductsResponse listProducts(Empty request);
    
    /**
     * 获取商品
     */
    @GetMapping("/products/{id}")
    Product getProduct(@PathVariable("id") String id);
}

mybatisplus要求使用最新的lambdaquerymapper拼接sql，不要xml

要求遵循标准微服务架构，可以先不用管那么多细节，但是模块划分要好



## HTTP接口文档

以下是后端API接口文档，供后端开发人员参考实现。

### 基础URL

```
/api
```

### 认证相关接口

#### 登录

- **URL**: `/user/login`
- **Method**: `POST`
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "token": "string",
      "userInfo": {
        "id": "string",
        "username": "string",
        "nickname": "string",
        "avatar": "string",
        "bio": "string",
        "gender": "string",
        "following": 0,
        "followers": 0,
        "noteCount": 0,
        "likeCount": 0,
        "isFollowed": false,
        "createdAt": "string",
        "updatedAt": "string"
      }
    }
  }
  ```

#### 注册

- **URL**: `/user/register`
- **Method**: `POST`
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string",
    "nickname": "string"
  }
  ```
- **响应**: 同登录接口

#### 获取当前用户信息

- **URL**: `/user/current`
- **Method**: `GET`
- **请求头**: `Authorization: Bearer {token}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "string",
      "username": "string",
      "nickname": "string",
      "avatar": "string",
      "bio": "string",
      "gender": "string",
      "following": 0,
      "followers": 0,
      "noteCount": 0,
      "likeCount": 0,
      "isFollowed": false,
      "createdAt": "string",
      "updatedAt": "string"
    }
  }
  ```

### 笔记相关接口

#### 获取笔记列表

- **URL**: `/notes`
- **Method**: `GET`
- **参数**:
  - `page`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认20)
  - `userId`: 用户ID (可选，筛选指定用户的笔记)
  - `tag`: 标签 (可选，筛选指定标签的笔记)
  - `keyword`: 关键词 (可选，搜索笔记标题或内容)
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "total": 0,
      "list": [
        {
          "id": "string",
          "title": "string",
          "content": "string",
          "images": ["string"],
          "userId": "string",
          "username": "string",
          "avatar": "string",
          "tags": ["string"],
          "likeCount": 0,
          "commentCount": 0,
          "collectCount": 0,
          "isLiked": false,
          "isCollected": false,
          "createdAt": "string",
          "updatedAt": "string"
        }
      ],
      "page": 1,
      "pageSize": 20
    }
  }
  ```

#### 获取笔记详情

- **URL**: `/notes/{id}`
- **Method**: `GET`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "string",
      "title": "string",
      "content": "string",
      "images": ["string"],
      "userId": "string",
      "username": "string",
      "avatar": "string",
      "tags": ["string"],
      "likeCount": 0,
      "commentCount": 0,
      "collectCount": 0,
      "isLiked": false,
      "isCollected": false,
      "createdAt": "string",
      "updatedAt": "string"
    }
  }
  ```

#### 创建笔记

- **URL**: `/notes`
- **Method**: `POST`
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
  ```json
  {
    "title": "string",
    "content": "string",
    "tags": ["string"],
    "images": ["string"]
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "string",
      "title": "string",
      "content": "string",
      "images": ["string"],
      "userId": "string",
      "username": "string",
      "avatar": "string",
      "tags": ["string"],
      "likeCount": 0,
      "commentCount": 0,
      "collectCount": 0,
      "isLiked": false,
      "isCollected": false,
      "createdAt": "string",
      "updatedAt": "string"
    }
  }
  ```

#### 上传笔记图片

- **URL**: `/upload/note-image`
- **Method**: `POST`
- **请求头**: 
  - `Authorization: Bearer {token}`
  - `Content-Type: multipart/form-data`
- **请求体**: 
  - `file`: 图片文件
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "url": "string"
    }
  }
  ```

#### 点赞笔记

- **URL**: `/notes/{id}/like`
- **Method**: `POST`
- **请求头**: `Authorization: Bearer {token}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 取消点赞

- **URL**: `/notes/{id}/unlike`
- **Method**: `POST`
- **请求头**: `Authorization: Bearer {token}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 收藏笔记

- **URL**: `/notes/{id}/collect`
- **Method**: `POST`
- **请求头**: `Authorization: Bearer {token}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 取消收藏

- **URL**: `/notes/{id}/uncollect`
- **Method**: `POST`
- **请求头**: `Authorization: Bearer {token}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

### 用户相关接口

#### 获取用户信息

- **URL**: `/user/{id}`
- **Method**: `GET`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "string",
      "username": "string",
      "nickname": "string",
      "avatar": "string",
      "bio": "string",
      "gender": "string",
      "following": 0,
      "followers": 0,
      "noteCount": 0,
      "likeCount": 0,
      "isFollowed": false,
      "createdAt": "string",
      "updatedAt": "string"
    }
  }
  ```

#### 关注用户

- **URL**: `/user/follow/{userId}`
- **Method**: `POST`
- **请求头**: `Authorization: Bearer {token}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 取消关注

- **URL**: `/user/unfollow/{userId}`
- **Method**: `POST`
- **请求头**: `Authorization: Bearer {token}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

### 评论相关接口

#### 获取笔记评论

- **URL**: `/notes/{noteId}/comments`
- **Method**: `GET`
- **参数**:
  - `page`: 页码 (默认1)
  - `pageSize`: 每页数量 (默认20)
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "total": 0,
      "list": [
        {
          "id": "string",
          "content": "string",
          "userId": "string",
          "username": "string",
          "avatar": "string",
          "noteId": "string",
          "createdAt": "string"
        }
      ],
      "page": 1,
      "pageSize": 20
    }
  }
  ```

#### 发表评论

- **URL**: `/notes/{noteId}/comments`
- **Method**: `POST`
- **请求头**: `Authorization: Bearer {token}`
- **请求体**:
  ```json
  {
    "content": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "string",
      "content": "string",
      "userId": "string",
      "username": "string",
      "avatar": "string",
      "noteId": "string",
      "createdAt": "string"
    }
  }
  ```

#### 删除评论

- **URL**: `/notes/{noteId}/comments/{commentId}`
- **Method**: `DELETE`
- **请求头**: `Authorization: Bearer {token}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

## 状态码说明

- 200: 成功
- 400: 请求参数错误
- 401: 未授权（token无效或已过期）
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误
