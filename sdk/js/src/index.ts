class LogBat {
    private static appKey: string = '';
    private static apiEndpoint: string = 'https://api.logbat.info/logs';
    private static originalConsole: { log: typeof console.log; error: typeof console.error };
    private static isInitialized: boolean = false;

    public static init(config: { appKey: string }): void {
        if (this.isInitialized) {
            console.warn('LogBat SDK is already initialized. Ignoring repeated initialization.');
            return;
        }

        this.appKey = config.appKey;
        this.overrideConsoleMethods();
        this.isInitialized = true;
    }

    public static async log(...args: any[]): Promise<void> {
        const promise = this.sendLog('info', args);
        this.originalConsole.log(...args);
        await promise;
    }

    public static async error(...args: any[]): Promise<void> {
        const promise = this.sendLog('error', args);
        this.originalConsole.error(...args);
        await promise;
    }

    private static async sendLog(level: string, args: any[]): Promise<void> {
        const logData = {
            level: level,
            data: args.map(arg =>
                typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
            ).join(' '),
            timestamp: new Date().toISOString()
        };

        try {
            const response = await fetch(this.apiEndpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'appKey': this.appKey
                },
                body: JSON.stringify(logData),
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        } catch (err) {
            this.originalConsole.error('Failed to send log:', err);
            throw err;
        }
    }

    private static overrideConsoleMethods(): void {
        this.originalConsole = {
            log: console.log,
            error: console.error
        };

        console.log = (...args: any[]): void => {
            this.log(...args).catch(err => this.originalConsole.error('Error in LogBat log:', err));
        };

        console.error = (...args: any[]): void => {
            this.error(...args).catch(err => this.originalConsole.error('Error in LogBat error:', err));
        };
    }
}

if (typeof window !== 'undefined') {
    (window as any).LogBat = LogBat;
}

export default LogBat;