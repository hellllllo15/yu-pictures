# 仅供学习参考

该项目是学习阶段，未扩展

运行项目还需要直接配置阿里云对象存储：
cos:
  client:
    host: 
    secretId: 
    secretKey: 
    region:  ap-chengdu
    bucket:  



为兼容云服务，已将分库分表的启动注释，可全局搜索“分库分表”
com/example/demo/manager/sharding/DynamicShardingManager
com/example/demo/manager/sharding/PictureShardingAlgorithm
这两个是java类，已修改成文件

## 技术亮点（后端）

- 权限校验：自定义 `@AuthCheck` 注解 + AOP 切面拦截；集成 Sa-Token，采用 Kit 多认证模式（`StpKit`、`SaSpaceCheckPermission`），支持空间级权限与注解合并策略（`SaTokenConfigure`）。
- 全局异常：统一的业务异常 `BusinessException`、错误码枚举 `ErrorCode`、全局异常处理 `GlobalExceptionHandler`，配合 `ThrowUtils.throwIf` 快速断言。
- JSON 精度：全局 `JsonConfig` 将 Long/long 使用 `ToStringSerializer` 输出，彻底避免前端精度丢失；WebSocket 下行同样手动注册模块确保一致。
- 文件服务：封装腾讯云 COS（`CosClientConfig` + `CosManager`），抽象上传模板（`manager/upload` 模板方法：文件、本地/URL 等多策略），返回标准 `UploadPictureResult`。
- 实时协同：基于 Spring WebSocket 的图片协同编辑（`PictureEditHandler` + `WebSocketConfig` + 握手拦截器），引入 Disruptor（`PictureEditEvent*`）做高性能事件驱动，区分进入/编辑/退出等消息类型。
- 高并发：基于 Disruptor 的无锁队列事件模型，减少上下文切换与 GC，显著提升吞吐与时延稳定性。
- 数据分片：集成 ShardingSphere 动态分库分表（`manager/sharding/*`），可按环境开关，兼容云服务部署。
- 数据访问：MyBatis-Plus 分页拦截器配置（`MyBatisPlusConfig`），统一响应模型 `BaseResponse` + `ResultUtils`。
- 跨域&请求封装：全局 CORS 放行配置（`CorsConfig`）；`HttpRequestWrapperFilter` + `RequestWrapper` 对 JSON 请求体二次读取与增强。
- 工具与规范：统一常量与枚举、DTO/VO 分层清晰；大量使用 Hutool 工具库；控制器入参统一 `PageRequest`/`DeleteRequest` 等；Cos/上传等与业务弱耦合便于复用。


## 技术亮点（前端）

- 技术栈：Vue 3 + TypeScript + Vite，组件化开发，模块清晰（页面/组件/分析/空间/图片）。
- 状态管理：Pinia 全局登录用户仓库（`useLoginUserStore`），统一获取与设置登录态。
- 路由守卫：基于 `vue-router` 的 `meta.requiresAdmin` 管控后台页，未授权重定向首页/认证页。
- 请求层：Axios 实例与响应拦截器统一处理 401，清理本地态并带 redirect 参数跳转，跨域场景可用。
- OpenAPI：`@umijs/openapi` 一键生成类型安全 API SDK（`openapi.config.js`），与全局请求库解耦。
- 可视化：集成 ECharts 与 `echarts-wordcloud`，封装全局组件 `VChart`，空间分析可视化支持。
- WebSocket：提供图片协同编辑测试页与连接诊断工具（`/pages/test/WebSocketTest.vue`），便于联调排障。
- 开发体验：Vite 代理 `/api`、开发态启用 Vue DevTools 插件、`@` 别名；脚本含类型检查、ESLint、Prettier。
- 代码规范：严格 TS 配置、统一格式化与 Lint，前后端接口类型对齐，减少运行时错误。
