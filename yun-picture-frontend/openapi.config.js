import { generateService } from '@umijs/openapi'

generateService({

    //请求文件  src/request.ts
  requestLibPath: "import request from '@/request'",

  //接口文档地址
  schemaPath: 'http://localhost:8123/api/v2/api-docs',

  //生成文件地址
  serversPath: './src/a', 

  //运行命令：node openapi.config.js
})