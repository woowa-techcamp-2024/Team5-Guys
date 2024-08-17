import LogBat from '../index';

// Mock fetch function
global.fetch = jest.fn(() =>
    Promise.resolve({
        ok: true,
        json: () => Promise.resolve({}),
    } as Response)
);

describe('LogBat SDK', () => {
    let consoleLogSpy: jest.SpyInstance;
    let consoleErrorSpy: jest.SpyInstance;

    beforeEach(() => {
        jest.clearAllMocks();
        consoleLogSpy = jest.spyOn(console, 'log').mockImplementation(() => {});
        consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
        LogBat['isInitialized'] = false;
        LogBat.init({ appKey: 'test-app' });
    });

    afterEach(() => {
        consoleLogSpy.mockRestore();
        consoleErrorSpy.mockRestore();
    });

    test('should initialize with appId', () => {
        expect(LogBat['appKey']).toBe('test-app');
    });

    test('should send log message', async () => {
        await LogBat.log('Test log message');

        expect(consoleLogSpy).toHaveBeenCalledWith('Test log message');
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(fetch).toHaveBeenCalledWith(
            'https://api.logbat.info/logs',
            expect.objectContaining({
                method: 'POST',
                headers: expect.objectContaining({
                    'Content-Type': 'application/json',
                    'appKey': 'test-app'
                }),
                body: expect.stringContaining('Test log message')
            })
        );
    });

    test('should send error message', async () => {
        await LogBat.error('Test error message');

        expect(consoleErrorSpy).toHaveBeenCalledWith('Test error message');
        expect(fetch).toHaveBeenCalledTimes(1);
        expect(fetch).toHaveBeenCalledWith(
            'https://api.logbat.info/logs',
            expect.objectContaining({
                method: 'POST',
                headers: expect.objectContaining({
                    'Content-Type': 'application/json',
                    'appKey': 'test-app'
                }),
                body: expect.stringContaining('Test error message')
            })
        );
    });

    test('should handle fetch failure', async () => {
        const networkError = new Error('Network error');
        (global.fetch as jest.Mock).mockImplementationOnce(() => Promise.reject(networkError));

        await expect(LogBat.log('Test log message')).rejects.toThrow('Network error');

        expect(consoleErrorSpy).toHaveBeenCalledWith('Failed to send log:', networkError);
    });

    test('should use console.log for regular logging', async () => {
        console.log('Regular log message');
        await new Promise(resolve => setTimeout(resolve, 0)); // Allow async operations to complete
        expect(consoleLogSpy).toHaveBeenCalledWith('Regular log message');
    });

    test('should use console.error for error logging', async () => {
        console.error('Regular error message');
        await new Promise(resolve => setTimeout(resolve, 0)); // Allow async operations to complete
        expect(consoleErrorSpy).toHaveBeenCalledWith('Regular error message');
    });
});