import { Handler } from 'aws-lambda';

/*global fetch*/
/*global performance*/
export const handler: Handler = async (event, context) => {
    const startTotalTime = performance.now(); // 전체 시작 시간 기록

    // 요청 본문을 파싱
    const body = JSON.parse(event.body || '{}');
    const repeat = body.repeat;

    if (typeof repeat !== 'number' || repeat <= 0) {
        return {
            statusCode: 400,
            body: JSON.stringify({
                message: "'repeat' must be a positive number"
            }),
        };
    }

    const appKey = body.appKey;
    if (typeof appKey !== 'string' || appKey.length === 0) {
        return {
            statusCode: 400,
            body: JSON.stringify({
                message: "'appKey' must be a non-empty string"
            }),
        };
    }

    const url = 'https://api.logbat.info/logs';
    const level = 'info';

    const timings: { index: number; time: number; statusCode: number }[] = [];
    let totalTime = 0;
    let successCount = 0;
    let failureCount = 0;
    let maxTime = -Infinity;
    let minTime = Infinity;

    for (let i = 0; i < repeat; i++) {
        const startTime = performance.now(); // 개별 요청 시작 시간
        let statusCode = 0;

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'appKey': appKey
                },
                body: JSON.stringify({
                    level: level,
                    timestamp: new Date().toISOString(),
                    data: context.awsRequestId + ' - ' + i
                }),
            });

            statusCode = response.status;
            if (response.ok) {
                successCount++;
            } else {
                failureCount++;
            }
        } catch (error) {
            statusCode = 500; // 네트워크 오류 등으로 인한 실패 처리
            failureCount++;
        }

        const endTime = performance.now(); // 개별 요청 종료 시간
        const elapsedTime = endTime - startTime;

        timings.push({ index: i, time: elapsedTime, statusCode: statusCode });
        totalTime += elapsedTime;
        maxTime = Math.max(maxTime, elapsedTime);
        minTime = Math.min(minTime, elapsedTime);
    }

    // sort by time
    timings.sort((a, b) => b.time - a.time);

    const averageTime = totalTime / repeat;
    const endTotalTime = performance.now(); // 전체 종료 시간 기록
    const totalExecutionTime = endTotalTime - startTotalTime;

    return {
        statusCode: 201,
        body: JSON.stringify({
            averageTime: averageTime,
            maxTime: maxTime,
            minTime: minTime,
            successCount: successCount,
            failureCount: failureCount,
            totalExecutionTime: totalExecutionTime,
            memoryUsed: context.memoryLimitInMB + ' MB',
            requestId: context.awsRequestId,
            timings: timings,
        }),
    };
};