class LogBat {
    private static appKey: string = '';
    private static apiEndpoint: string = 'https://api.logbat.info/logs';
    private static originalConsole: {
        trace: typeof console.trace
        debug: typeof console.debug
        info: typeof console.info
        log: typeof console.log
        warn: typeof console.warn
        error: typeof console.error
    };
    private static isInitialized: boolean = false;
    private static logQueue: Array<{level: string, data: string, timestamp: string}> = [];
    private static batchInterval: number = 1000; // 1 second
    private static batchTimeoutId: number | null = null;

    private static maxQueueSize: number = 100 * 1024; // 100 KB
    private static currentQueueSize: number = 0;

    public static init(config: { appKey: string }): void {
        if (this.isInitialized) {
            console.warn('LogBat SDK is already initialized. Ignoring repeated initialization.');
            return;
        }

        this.appKey = config.appKey;
        this.overrideConsoleMethods();
        this.isInitialized = true;
        this.scheduleBatch();
    }


    private static scheduleBatch(): void {
        if (this.batchTimeoutId === null) {
            this.batchTimeoutId = window.setTimeout(() => {
                this.sendBatch();
                this.batchTimeoutId = null;
                if (this.logQueue.length > 0) {
                    this.scheduleBatch();
                }
            }, this.batchInterval);
        }
    }

    private static async sendBatch(): Promise<void> {
        if (this.logQueue.length === 0) return;

        const batchToSend = [...this.logQueue];
        this.logQueue = [];
        this.currentQueueSize = 0;

        try {
            const response = await fetch(this.apiEndpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'App-Key': this.appKey
                },
                body: JSON.stringify(batchToSend),
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        } catch (err) {
            this.originalConsole.error('Failed to send log batch:', err);
        }
    }

    private static queueLog(level: string, args: any[]): void {
        const logData = {
            level: level,
            data: args.map(arg =>
                typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
            ).join(' '),
            timestamp: new Date().toISOString()
        };

        const logSize = JSON.stringify(logData).length;
        this.logQueue.push(logData);
        this.currentQueueSize += logSize;
        if (this.currentQueueSize > this.maxQueueSize) {
            this.sendBatch();
        }
        else {
            this.scheduleBatch();
        }
    }

    public static trace(...args: any[]): void {
        this.originalConsole.trace(...args);
        this.queueLog('trace', args);
    }

    public static debug(...args: any[]): void {
        this.originalConsole.debug(...args);
        this.queueLog('debug', args);
    }

    public static info(...args: any[]): void {
        this.originalConsole.info(...args);
        this.queueLog('info', args);
    }

    public static log(...args: any[]): void {
        this.originalConsole.log(...args);
        this.queueLog('info', args);
    }

    public static warn(...args: any[]): void {
        this.originalConsole.warn(...args);
        this.queueLog('warn', args);
    }

    public static error(...args: any[]): void {
        this.originalConsole.error(...args);
        this.queueLog('error', args);
    }

    private static overrideConsoleMethods(): void {
        this.originalConsole = {
            trace: console.trace,
            debug: console.debug,
            info: console.info,
            log: console.log,
            warn: console.warn,
            error: console.error
        };

        console.trace = (...args: any[]): void => { this.trace(...args); };
        console.debug = (...args: any[]): void => { this.debug(...args); };
        console.info = (...args: any[]): void => { this.info(...args); };
        console.log = (...args: any[]): void => { this.log(...args); };
        console.warn = (...args: any[]): void => { this.warn(...args); };
        console.error = (...args: any[]): void => { this.error(...args); };
    }
}

if (typeof window !== 'undefined') {
    (window as any).LogBat = LogBat;
}

export default LogBat;