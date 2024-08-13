# LogBat SDK

LogBat SDK는 웹 애플리케이션에서 로그를 쉽게 수집하고 중앙 서버로 전송할 수 있게 해주는 JavaScript 라이브러리입니다.

## 특징

- 간편한 초기화 및 설정
- `console.log`와 `console.error`의 자동 캡처
- 비동기 로그 전송으로 애플리케이션 성능 영향 최소화
- TypeScript 지원

## 설치

CDN을 통해 직접 스크립트를 포함할 수 있습니다:

```html
<script src="https://sdk.logbat.info/sdk.js"></script>
```

## 사용법

### 초기화

LogBat SDK를 사용하기 전에 먼저 초기화해야 합니다:

```html
<script src="https://sdk.logbat.info/sdk.js"></script>
<script>
LogBat.init({ appId: 'YOUR_APP_ID' });
</script>
```

### 로그 전송

초기화 후에는 `console.log`와 `console.error`가 자동으로 캡처되어 LogBat 서버로 전송됩니다:

```javascript
console.log('This is a log message');
console.error('This is an error message');
```

LogBat SDK의 메서드를 직접 사용할 수도 있습니다:

```javascript
LogBat.log('This is a direct log message');
LogBat.error('This is a direct error message');
```

## 주의사항

- SDK를 사용하기 전에 반드시 `init` 메서드를 호출해야 합니다.
- `init` 메서드를 여러 번 호출해도 안전합니다. 중복 호출은 무시됩니다.
- 로그 전송 실패는 애플리케이션의 정상적인 실행을 방해하지 않습니다.
