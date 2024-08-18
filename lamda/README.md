## 사전 요구 사항

- **Node.js** (버전 20 이상)

## 설치 및 사용법

1.**패키지 설치**:

```bash
npm install
```

2.**프로젝트 빌드**:

TypeScript를 컴파일하고 `.mjs` 파일로 변환합니다.

```bash
npm run build
```

3.**`/dist/index.js` Lambda 함수 사용**:

4.**Lambda 함수 호출**:

```bash
curl --location 'https://your-lambda-url/' \
--header 'Content-Type: application/json' \
--data '{
    "appKey": "{appKey}",
    "repeat": 10
}'
```

## 예시 응답

```json
{
  "averageTime": 32.77718769300003,
  "maxTime": 1909.24762,
  "minTime": 6.588578999999299,
  "successCount": 1000,
  "failureCount": 0,
  "totalExecutionTime": 32800.551787000004,
  "memoryUsed": "128 MB",
  "requestId": "3c269a70-b0ba-4912-88f0-f61e5dee018c",
  "timings": [
    {
      "index": 0,
      "time": 1909.24762,
      "statusCode": 201
    },
    {
      "index": 897,
      "time": 618.7067969999989,
      "statusCode": 201
    },
    {
      "index": 563,
      "time": 400.04752400000143,
      "statusCode": 201
    },
    {
      "index": 214,
      "time": 380.8735259999994,
      "statusCode": 201
    },
    {
      "index": 1,
      "time": 351.5926810000001,
      "statusCode": 201
    },
    {
      "index": 4,
      "time": 339.9373370000003,
      "statusCode": 201
    },
    {
      "index": 20,
      "time": 339.90371999999934,
      "statusCode": 201
    },
    {
      "index": 895,
      "time": 220.41194600000017,
      "statusCode": 201
    },
    {
      "index": 564,
      "time": 212.24562899999728,
      "statusCode": 201
    },
    {
      "index": 8,
      "time": 200.19748899999968,
      "statusCode": 201
    },
    {
      "index": 15,
      "time": 200.19742299999962,
      "statusCode": 201
    },
    {
      "index": 10,
      "time": 199.67243499999995,
      "statusCode": 201
    },
    {
      "index": 6,
      "time": 199.41130600000042,
      "statusCode": 201
    },
    {
      "index": 12,
      "time": 180.20744199999945,
      "statusCode": 201
    },
    {
      "index": 9,
      "time": 180.0771120000004,
      "statusCode": 201
    }
  ]
}
```